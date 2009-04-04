package com.mindalliance.channels.pages;

import com.mindalliance.channels.Identifiable;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.ScenarioObject;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.UserIssue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.ExternalFlow;
import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.export.Importer;
import com.mindalliance.channels.pages.components.ScenarioLink;
import com.mindalliance.channels.pages.components.ScenarioPanel;
import com.mindalliance.channels.pages.components.PlanMapPanel;
import com.mindalliance.channels.pages.components.entities.EntityPanel;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.menus.ProjectActionsMenuPanel;
import com.mindalliance.channels.pages.components.menus.ProjectShowMenuPanel;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.StringValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.text.Collator;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * The project's home page.
 */
public final class ProjectPage extends WebPage implements Updatable {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ProjectPage.class );

    /**
     * The 'scenario' parameter in the URL.
     */
    static final String SCENARIO_PARM = "scenario";                                       // NON-NLS

    /**
     * The 'part' parameter in the URL.
     */
    static final String PART_PARM = "node";                                               // NON-NLS

    /**
     * The 'expand' parameter in the URL.
     */
    public static final String EXPAND_PARM = "expand";                                    // NON-NLS

    /**
     * Length a scenario title is abbreviated to
     */
    private static final int SCENARIO_TITLE_MAX_LENGTH = 40;

    /**
     * Length a scenario title is abbreviated to
     */
    private static final int SCENARIO_DESCRIPTION_MAX_LENGTH = 94;
    /**
     * Id of components that are expanded.
     */
    private Set<Long> expansions;
    /**
     * Ids of expanded entities.
     */
    private List<Long> expandedEntities = new ArrayList<Long>();
    /**
     * Label with name of scenario.
     */
    private Label scenarioNameLabel;
    /**
     * Label with description of scenario.
     */
    private Label scenarioDescriptionLabel;
    /**
     * Choice of scenarios.
     */
    private DropDownChoice<Scenario> scenarioDropDownChoice;
    /**
     * Scenarios action menu.
     */
    private MenuPanel projectActionsMenu;
    /**
     * Scenarios show menu.
     */
    private MenuPanel projectShowMenu;
    /**
     * The current part.
     */
    private Part part;
    /**
     * The current scenario.
     */
    private Scenario scenario;
    /**
     * The scenario to display after submit.
     * If null, redisplay current part.
     */
    private Scenario target;

    /**
     * The scenario import field.
     */
    private FileUploadField scenarioImport;
    /**
     * the big form
     */
    private Form form;
    /**
     * The scenario panel.
     */
    private ScenarioPanel scenarioPanel;
    /**
     * The entity panel.
     */
    private Component entityPanel;
    /**
     * The scenarios map panel.
     */
    private Component planMapPanel;

    /**
     * Used when page is called without parameters.
     * Redirect to default scenario, default part, all collapsed.
     */
    public ProjectPage() {
        this( new PageParameters() );
    }

    public ProjectPage( PageParameters parameters ) {
        // Call super to remember parameters in links
        super( parameters );

        DataQueryObject dqo = getDqo();
        Scenario sc = findScenario( dqo, parameters );

        if ( sc == null )
            redirectTo( dqo.getDefaultScenario() );

        else {
            Part p = findPart( sc, parameters );
            if ( p != null )
                init( sc, p, findExpansions( parameters ) );
            else
                redirectTo( sc );
        }
    }

    /**
     * Utility constructor for tests.
     *
     * @param scenario a scenario
     */
    public ProjectPage( Scenario scenario ) {
        this( scenario, scenario.getDefaultPart() );
    }

    /**
     * Utility constructor for tests.
     *
     * @param scenario a scenario
     * @param p        a part in the scenario
     */
    public ProjectPage( Scenario scenario, Part p ) {
        Set<Long> expanded = Collections.emptySet();
        init( scenario, p, expanded );
    }

    /**
     * Utility constructor.
     *
     * @param p  the part to display
     * @param id a section to expand
     */
    public ProjectPage( Part p, long id ) {
        Set<Long> expanded = new HashSet<Long>();
        expanded.add( id );
        init( p.getScenario(), p, expanded );
    }

    private void init( Scenario scenario, Part p, Set<Long> expanded ) {
        getCommander().releaseAllLocks( Project.getUserName() );
        getCommander().requestLockOn( p );
        setPart( p );
        expansions = expanded;
        for ( Long id : expansions ) {
            getCommander().requestLockOn( id );
        }
        setVersioned( false );
        setStatelessHint( true );
        add( new Label( "sc-title", new PropertyModel<String>( scenario, "name" ) ) );    // NON-NLS
        form = new Form( "big-form" ) {
            protected void onSubmit() {
                getProject().getCommander().resetUserHistory( Project.getUserName() );
                importScenario();
            }
        };
        add( form );
        addHeader( scenario );
        addScenarioMenubar( scenario );
        addSelectScenario();
        scenarioPanel = new ScenarioPanel(
                "scenario",
                new PropertyModel<Scenario>( this, "scenario" ),
                new PropertyModel<Part>( this, "part" ),
                getReadOnlyExpansions() );
        form.add( scenarioPanel );
        addEntityPanel();
        addPlanMapPanel();
        LOG.debug( "Scenario page generated" );
    }

    private void importScenario() {
        FileUpload fileUpload = scenarioImport.getFileUpload();
        if ( fileUpload != null ) {
            // Import and switch to scenario
            Importer importer = Project.getProject().getImporter();
            try {
                InputStream inputStream = fileUpload.getInputStream();
                Scenario imported = importer.importScenario( inputStream );
                setTarget( imported );
                redirectTo( imported );
            } catch ( IOException e ) {
                // TODO redirect to a proper error screen... user has to know...
                String s = "Import error";
                LOG.error( s, e );
                throw new RuntimeException( s, e );
            }
        }
    }

    private void addHeader( final Scenario scenario ) {
        scenarioNameLabel = new Label(
                "header",                                                             // NON-NLS
                /* new PropertyModel<String>( scenario, NAME_PROPERTY ) ); */
                new AbstractReadOnlyModel() {
                    @Override
                    public Object getObject() {
                        return StringUtils.abbreviate(
                                scenario.getName(), SCENARIO_TITLE_MAX_LENGTH );
                    }
                }
        );
        scenarioNameLabel.setOutputMarkupId( true );
        // Add style mods from scenario analyst.
        annotateScenarioName( scenario );
        form.add( scenarioNameLabel );
        scenarioDescriptionLabel = new Label( "sc-desc",                              // NON-NLS
                new AbstractReadOnlyModel<String>() {
                    @Override
                    public String getObject() {
                        return StringUtils.abbreviate(
                                scenario.getDescription(),
                                SCENARIO_DESCRIPTION_MAX_LENGTH );
                    }
                }
        );
        scenarioDescriptionLabel.setOutputMarkupId( true );
        form.add( scenarioDescriptionLabel );
        form.add( new Label( "user", Project.getUserName() ) );       // NON-NLS
    }

    private void annotateScenarioName( Scenario scenario ) {
        Analyst analyst = ( (Project) getApplication() ).getAnalyst();
        String issue = analyst.getIssuesSummary(
                scenario, Analyst.INCLUDE_PROPERTY_SPECIFIC );
        if ( !issue.isEmpty() ) {
            scenarioNameLabel.add( new AttributeModifier(
                    "class", true, new Model<String>( "error" ) ) ); // NON-NLS
            scenarioNameLabel.add( new AttributeModifier(
                    "title", true, new Model<String>( issue ) ) );  // NON-NLS
        } else {
            scenarioNameLabel.add( new AttributeModifier(
                    "class", true, new Model<String>( "no-error" ) ) ); // NON-NLS
            scenarioNameLabel.add( new AttributeModifier(
                    "title", true, new Model<String>( "No known issue" ) ) );  // NON-NLS
        }

    }

    private void addScenarioMenubar( Scenario scenario ) {
        projectActionsMenu = new ProjectActionsMenuPanel(
                "projectActionsMenu",
                new Model<Scenario>( scenario ),
                getReadOnlyExpansions() );
        projectActionsMenu.setOutputMarkupId( true );
        form.add( projectActionsMenu );
        projectShowMenu = new ProjectShowMenuPanel(
                "projectShowMenu",
                new Model<Scenario>( scenario ),
                getReadOnlyExpansions() );
        projectShowMenu.setOutputMarkupId( true );
        form.add( projectShowMenu );
    }

    private void addSelectScenario() {
        scenarioImport = new FileUploadField( "sc-import", new Model<FileUpload>() ); // NON-NLS
        form.add( scenarioImport );
        form.add( createSelectScenario() );                                      // NON-NLS
    }

    private DropDownChoice<Scenario> createSelectScenario() {
        scenarioDropDownChoice = new DropDownChoice<Scenario>(
                "sc-sel", new PropertyModel<Scenario>( this, "target" ),    // NON-NLS
                new PropertyModel<List<? extends Scenario>>( this, "allScenarios" )
        ) {

            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            protected void onSelectionChanged( Scenario newSelection ) {
                ( (ProjectPage) getWebPage() ).redirectTo( newSelection );
            }
        };
        scenarioDropDownChoice.setOutputMarkupId( true );

        return scenarioDropDownChoice;
    }

    private void addEntityPanel() {
        ModelObject entity = findExpandedEntity();
        if ( entity == null ) {
            entityPanel = new Label( "entity", "" );
        } else {
            entityPanel = new EntityPanel(
                    "entity",
                    new Model<ModelObject>( entity ),
                    getReadOnlyExpansions() );
        }
        makeVisible( entityPanel, entity != null );
        entityPanel.setOutputMarkupId( true );
        form.addOrReplace( entityPanel );
    }

    private void addPlanMapPanel() {
        boolean showPlanMap = expansions.contains( Project.getProject().getId() );
        if ( showPlanMap ) {
            planMapPanel = new PlanMapPanel(
                    "plan-map",
                    getReadOnlyExpansions() );
        } else {
            planMapPanel = new Label( "plan-map", "" );
        }
        makeVisible( planMapPanel, showPlanMap );
        planMapPanel.setOutputMarkupId( true );
        form.addOrReplace( planMapPanel );
    }

    private ModelObject findExpandedEntity() {
        ModelObject entity = null;

        for ( long id : expansions ) {
            try {
                ModelObject mo = getDqo().find( ModelObject.class, id );
                if ( mo.isEntity() ) return mo;
            }
            catch ( NotFoundException e ) {
                // ignore
            }
        }
        return entity;
    }

    public List<Scenario> getAllScenarios() {
        List<Scenario> allScenarios = Project.getProject().getDqo().list( Scenario.class );
        Collections.sort( allScenarios, new Comparator<Scenario>() {
            public int compare( Scenario sc1, Scenario sc2 ) {
                return Collator.getInstance().compare( sc1.getName(), sc2.getName() );
            }
        } );
        return allScenarios;
    }

    /**
     * Find scenario specified in parameters.
     *
     * @param dqo        data query object
     * @param parameters the page parameters
     * @return a scenario, or null if not found
     */
    static Scenario findScenario( DataQueryObject dqo, PageParameters parameters ) {
        if ( parameters.containsKey( SCENARIO_PARM ) )
            try {
                return dqo.find( Scenario.class, parameters.getLong( SCENARIO_PARM ) );
            } catch ( StringValueConversionException ignored ) {
                LOG.warn( "Invalid scenario specified in parameters. Using default." );
            } catch ( NotFoundException ignored ) {
                LOG.warn( "Unknown scenario specified in parameters. Using default." );
            } catch ( ClassCastException ignored ) {
                LOG.warn( "Other object specified as scenario in parameters. Using default." );
            }
        return null;
    }

    /**
     * Find part specified in parameters.
     *
     * @param scenario   the scenario
     * @param parameters the page parameters
     * @return a part, or null if not found
     */
    static Part findPart( Scenario scenario, PageParameters parameters ) {
        if ( parameters.containsKey( PART_PARM ) )
            try {
                return (Part) scenario.getNode( parameters.getLong( PART_PARM ) );
            } catch ( StringValueConversionException ignored ) {
                LOG.warn( "Invalid part specified in parameters. Using default." );
            }
        return null;
    }

    /**
     * Redirect to a scenario.
     *
     * @param scenario a scenario
     */
    public void redirectTo( Scenario scenario ) {
        redirectTo( scenario.getDefaultPart() );
    }

    /**
     * redirect to a part.
     *
     * @param p a part
     */
    private void redirectTo( Part p ) {
        Set<Long> ids = Collections.emptySet();
        setResponsePage( new RedirectPage( ScenarioLink.linkStringFor( p, ids ) ) );
    }

    /**
     * Redirect here.
     */
    public void redirectHere() {
        long sid = getScenario().getId();
        long nid = getPart().getId();
        StringBuffer exps = new StringBuffer( 128 );
        for ( long id : expansions ) {
            exps.append( "&expand=" );                                                    // NON-NLS
            exps.append( Long.toString( id ) );
        }
        setResponsePage(
                new RedirectPage(
                        MessageFormat.format(
                                "?scenario={0,number,0}&part={1,number,0}{2}",      // NON-NLS
                                sid,
                                nid,
                                exps ) ) );
    }

    /**
     * Return initialized parameters for given scenario and part.
     *
     * @param scenario the scenario
     * @param p        the part, maybe null (in which case, would link to first part in scenario)
     * @param expanded components id that should be expanded
     * @return page parameters to use in links, etc.
     */
    public static PageParameters getParameters(
            Scenario scenario, Part p, Set<Long> expanded ) {

        PageParameters result = new PageParameters();
        result.put( SCENARIO_PARM, Long.toString( scenario.getId() ) );
        if ( p != null ) {
            result.put( PART_PARM, Long.toString( p.getId() ) );
            for ( long id : expanded )
                result.add( EXPAND_PARM, Long.toString( id ) );
        }
        return result;
    }

    /**
     * Return initialized parameters for given scenario and part.
     *
     * @param scenario the scenario
     * @param p        the part, maybe null (in which case, would link to first part in scenario)
     * @return page parameters to use in links, etc.
     */
    public static PageParameters getParameters( Scenario scenario, Part p ) {
        Set<Long> expansions = Collections.emptySet();
        return getParameters( scenario, p, expansions );
    }

    /**
     * Return initialized parameters for given scenario and part.
     *
     * @param scenario the scenario
     * @param p        the part, maybe null (in which case, would link to first part in scenario)
     * @param id       the id to expand
     * @return page parameters to use in links, etc.
     */
    public static PageParameters getParameters( Scenario scenario, Part p, long id ) {
        Set<Long> expansions = new HashSet<Long>( 1 );
        expansions.add( id );
        return getParameters( scenario, p, expansions );
    }

    /**
     * Find expansions in page parameters
     *
     * @return set of ids
     */
    public Set<Long> findExpansions() {
        return findExpansions( getPageParameters() );
    }

    /**
     * Find expansions in page parameters
     *
     * @param parameters page parameters
     * @return set of ids
     */
    public Set<Long> findExpansions( PageParameters parameters ) {
        if ( parameters == null ) return new HashSet<Long>();
        Set<Long> result = new HashSet<Long>( parameters.size() );
        if ( parameters.containsKey( EXPAND_PARM ) ) {
            List<String> stringList = Arrays.asList( parameters.getStringArray( EXPAND_PARM ) );
            for ( String id : stringList )
                try {
                    result.add( Long.valueOf( id ) );
                } catch ( NumberFormatException ignored ) {
                    LOG.warn( MessageFormat.format( "Invalid expansion parameter: {0}", id ) );
                }
        }
        return result;
    }

    /**
     * Get the channels data query object from project via the application context.
     *
     * @return the data query object
     */
    private DataQueryObject getDqo() {
        return getProject().getDqo();
    }

    private Project getProject() {
        return (Project) getApplication();
    }

    private Commander getCommander() {
        return getProject().getCommander();
    }

    /**
     * Get set or defualt part.
     *
     * @return a part
     */
    public Part getPart() {
        if ( isZombie( part ) ) {
            return scenario.getDefaultPart();
        } else {
            return part;
        }
    }

    private boolean isZombie( Part part ) {
        return part == null
                || scenario.getNode( part.getId() ) == null
                || part.getScenario() == null;
    }

    /**
     * Set part shown.
     *
     * @param p a part
     */
    public void setPart( Part p ) {
        if ( part != null ) getCommander().releaseAnyLockOn( part );
        part = p;
        getCommander().requestLockOn( p );
        scenario = p.getScenario();
    }

    /**
     * Return page parameters with an added expand parameter.
     *
     * @param id a model object id
     * @return page parameters
     */
    public PageParameters getParametersExpanding( long id ) {
        PageParameters result = getPageParameters();
        if ( !this.findExpansions().contains( id ) ) {
            result.add( EXPAND_PARM, Long.toString( id ) );
        }
        return result;
    }

    /**
     * To support tests.
     *
     * @return page parameters
     */
    public PageParameters getPageParameters() {
        PageParameters params = super.getPageParameters();
        if ( params == null ) params = new PageParameters();
        return params;
    }

    /**
     * Returns page parameters with an expand parameter removed.
     *
     * @param id a model object id
     * @return page parameters
     */
    public PageParameters getParametersCollapsing( long id ) {
        PageParameters result = getPageParameters();
        String[] expanded = result.getStringArray( EXPAND_PARM );
        String idString = Long.toString( id );
        result.remove( EXPAND_PARM );
        if ( expanded != null ) {
            for ( String exp : expanded )
                if ( !exp.equals( idString ) ) result.add( EXPAND_PARM, exp );
        }
        return result;
    }

    public void setTarget( Scenario target ) {
        this.target = target;
    }

    public Scenario getTarget() {
        return target;
    }

    public Scenario getScenario() {
        return scenario;
    }

    /**
     * Set a component's visibility.
     *
     * @param component a component
     * @param visible   a boolean
     */
    private void makeVisible( Component component, boolean visible ) {
        component.add(
                new AttributeModifier(
                        "style",
                        true,
                        new Model<String>( visible ? "display:inline" : "display:none" ) ) );
    }

    private void expand( Identifiable identifiable ) {
        // First collapse any already expanded entity
        if ( identifiable instanceof ModelObject
                && ( (ModelObject) identifiable ).isEntity() ) {
            ModelObject entity = findExpandedEntity();
            if ( entity != null ) {
                expansions.remove( entity.getId() );
                getCommander().releaseAnyLockOn( entity );
                expandedEntities.add( 0, entity.getId() );
            }
            // expandedEntities.remove( identifiable.getId() );
        }
        // Never lock a scenario
        if ( !( identifiable instanceof Scenario || identifiable instanceof Project ) ) {
            getCommander().requestLockOn( identifiable );
        }
        expansions.add( identifiable.getId() );
    }

    private void collapse( Identifiable identifiable ) {
        if ( identifiable instanceof ModelObject
                && ( (ModelObject) identifiable ).isEntity() ) {
            if ( !expandedEntities.isEmpty() ) {
                long entityId = expandedEntities.remove( 0 );
                getCommander().requestLockOn( entityId );
                expansions.add( entityId );
            }
        }
        getCommander().releaseAnyLockOn( identifiable );
        expansions.remove( identifiable.getId() );
    }


    /**
     * {@inheritDoc}
     */
    public void changed( Change change ) {
        Identifiable identifiable = change.getSubject();
        if ( change.isCollapsed() ) {
            collapse( identifiable );
        } else if ( change.isExpanded() ) {
            expand( identifiable );
        } else if ( change.isAdded() ) {
            expand( identifiable );
        } else if ( change.isRemoved() ) {
            collapse( identifiable );
        }
        if ( identifiable instanceof Scenario ) {
            if ( change.isExists() ) {
                getCommander().resetUserHistory( Project.getUserName() );
            }
        }
        if ( identifiable instanceof Part ) {
            if ( change.isAdded() || change.isSelected() ) {
                setPart( (Part) identifiable );
            } else if ( change.isRemoved() ) {
                setPart( getScenario().getDefaultPart() );
                expand( getScenario().getDefaultPart() );
            }
        }
        if ( identifiable instanceof Flow ) {
            if ( change.isUpdated() && change.getProperty().equals( "other" ) ) {
                expand( identifiable );
            }
        }
        if ( identifiable instanceof UserIssue && change.isAdded() ) {
            UserIssue userIssue = (UserIssue) identifiable;
            ModelObject mo = userIssue.getAbout();
            if ( mo instanceof Scenario ) {
                expand( identifiable );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change ) {
        Identifiable identifiable = change.getSubject();
        if ( change.isUnknown() ) {
            redirectHere();
        } else if ( change.isUndoing() ) {
            target.addComponent(planMapPanel );
            target.addComponent( scenarioPanel );
            target.addComponent( entityPanel );
            target.addComponent( planMapPanel );
        }
        if ( identifiable instanceof Project ) {
            if ( change.isDisplay() ) {
                target.addComponent( planMapPanel );
            }
        }
        if ( identifiable instanceof Scenario ) {
            target.addComponent(planMapPanel );
            if ( change.isUpdated() ) {
                if ( change.getProperty().equals( "name" ) ) {
                    target.addComponent( scenarioNameLabel );
                    target.addComponent( scenarioDropDownChoice );
                } else if ( change.getProperty().equals( "description" ) ) {
                    target.addComponent( scenarioDescriptionLabel );
                }
            } else if ( change.isAdded() || change.isSelected() ) {
                redirectTo( (Scenario) identifiable );
            } else if ( change.isRemoved() ) {
                redirectTo( getDqo().getDefaultScenario() );
            } else if ( change.isRecomposed() ) {
                target.addComponent( scenarioPanel );
            }
        }
        if ( identifiable instanceof Part ) {
            if ( change.isExists() || change.isSelected() ) {
                target.addComponent( scenarioPanel );
            }
        }
        if ( identifiable instanceof ExternalFlow ) {
            target.addComponent(planMapPanel );
        }
        if ( identifiable instanceof ScenarioObject
                || ( identifiable instanceof Issue
                && ( (Issue) identifiable ).getAbout().getId() == getScenario().getId() ) ) {
            annotateScenarioName( getScenario() );
            target.addComponent( scenarioNameLabel );
        }
        if ( identifiable instanceof Issue
                && change.isExists()
                && ( (Issue) identifiable ).getAbout().getId() == getScenario().getId() ) {
            annotateScenarioName( getScenario() );
            target.addComponent( scenarioNameLabel );
            scenarioPanel.expandScenarioEditPanel( target );
            addPlanMapPanel();
            target.addComponent(planMapPanel );
        }
        if ( identifiable instanceof ModelObject
                && ( (ModelObject) identifiable ).isEntity() ) {
            if ( change.isDisplay() ) {
                addEntityPanel();
                target.addComponent( entityPanel );
            } else {
                target.addComponent( scenarioPanel );
            }

        }
        target.addComponent( projectActionsMenu );
        target.addComponent( projectShowMenu );
    }

    /**
     * Get read-only expansions.
     *
     * @return a read-only set of Longs
     */
    private Set<Long> getReadOnlyExpansions() {
        return Collections.unmodifiableSet( expansions );
    }
}
