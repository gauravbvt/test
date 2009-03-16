package com.mindalliance.channels.pages;

import com.mindalliance.channels.Identifiable;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.ScenarioObject;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.command.LockManager;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.export.Importer;
import com.mindalliance.channels.pages.components.ScenarioLink;
import com.mindalliance.channels.pages.components.ScenarioPanel;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.menus.ScenarioActionsMenuPanel;
import com.mindalliance.channels.pages.components.menus.ScenarioPagesMenuPanel;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private MenuPanel scenarioActionsMenu;
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
     * Used when page is called without parameters.
     * Redirect to default scenario, default part, all collapsed.
     */
    public ProjectPage() {
        this( new PageParameters() );
    }

    public ProjectPage( PageParameters parameters ) {
        // Call super to remember parameters in links
        super( parameters );

        Service service = getService();
        Scenario sc = findScenario( service, parameters );

        if ( sc == null )
            redirectTo( service.getDefaultScenario() );

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
        getLockManager().releaseAllLocks( Project.getUserName() );
        getLockManager().requestLockOn( p );
        setPart( p );
        expansions = expanded;
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
                expansions );
        form.add( scenarioPanel );
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
        }
        else {
            scenarioNameLabel.add( new AttributeModifier(
                    "class", true, new Model<String>( "no-error" ) ) ); // NON-NLS
            scenarioNameLabel.add( new AttributeModifier(
                    "title", true, new Model<String>( "No known issue" ) ) );  // NON-NLS
        }

    }

    private void addScenarioMenubar( Scenario scenario ) {
        scenarioActionsMenu = new ScenarioActionsMenuPanel(
                "scenarioActionsMenu",
                new Model<Scenario>( scenario ),
                expansions );
        scenarioActionsMenu.setOutputMarkupId( true );
        form.add( scenarioActionsMenu );
        ScenarioPagesMenuPanel scenarioPagesMenu = new ScenarioPagesMenuPanel(
                "scenarioPagesMenu",
                new Model<Scenario>( scenario ) );
        scenarioPagesMenu.setOutputMarkupId( true );
        form.add( scenarioPagesMenu );
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

    public List<Scenario> getAllScenarios() {
        return Project.getProject().getService().list( Scenario.class );
    }

    /**
     * Find scenario specified in parameters.
     *
     * @param service    the scenario container
     * @param parameters the page parameters
     * @return a scenario, or null if not found
     */
    static Scenario findScenario( Service service, PageParameters parameters ) {
        if ( parameters.containsKey( SCENARIO_PARM ) )
            try {
                return service.find( Scenario.class, parameters.getLong( SCENARIO_PARM ) );
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
     * Get the channels service from project via the application context.
     *
     * @return the service
     */
    private Service getService() {
        return getProject().getService();
    }

    private Project getProject() {
        return (Project) getApplication();
    }

    private LockManager getLockManager() {
        return getProject().getLockManager();
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
     * @param part a part
     */
    public void setPart( Part part ) {
        this.part = part;
        scenario = part.getScenario();
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
     * {@inheritDoc}
     */
    public void changed( Change change ) {
        Identifiable identifiable = change.getSubject();
        if ( change.isCollapsed() ) {
            expansions.remove( identifiable.getId() );
        } else if ( change.isExpanded() ) {
            expansions.add( identifiable.getId() );
        } else if ( change.isAdded() ) {
            expansions.add( identifiable.getId() );
        } else if ( change.isRemoved() ) {
            expansions.remove( identifiable.getId() );
        }
        if ( identifiable instanceof Part ) {
            if ( change.isAdded() ) {
                setPart( (Part) identifiable );
            } else if ( change.isRemoved() ) {
                setPart( getScenario().getDefaultPart() );
            }
        }
        if ( identifiable instanceof Issue
                && change.isAdded()
                && ( (Issue) identifiable ).getAbout() instanceof Scenario )
        {
            expansions.add( identifiable.getId() );
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
            target.addComponent( scenarioPanel );
        }
        if ( identifiable instanceof Scenario ) {
            if ( change.isUpdated() ) {
                if ( change.getProperty().equals( "name" ) ) {
                    target.addComponent( scenarioNameLabel );
                    target.addComponent( scenarioDropDownChoice );
                } else if ( change.getProperty().equals( "description" ) ) {
                    target.addComponent( scenarioDescriptionLabel );
                }
            } else if ( change.isAdded() ) {
                getProject().getCommander().resetUserHistory( Project.getUserName() );
                redirectTo( (Scenario) identifiable );
            } else if ( change.isRemoved() ) {
                getProject().getCommander().resetUserHistory( Project.getUserName() );
                redirectTo( getService().getDefaultScenario() );
            } else if ( change.isRecomposed() ) {
                target.addComponent( scenarioPanel );
            }
        }
        if ( identifiable instanceof Part && change.isExists() ) {
            target.addComponent( scenarioPanel );
        }
        if ( identifiable instanceof ScenarioObject
                || ( identifiable instanceof Issue
                && ( (Issue) identifiable ).getAbout() instanceof Scenario ) )
        {
            annotateScenarioName( getScenario() );
            target.addComponent( scenarioNameLabel );
        }
        if ( identifiable instanceof Issue
                && change.isExists()
                && ( (Issue) identifiable ).getAbout() instanceof Scenario )
        {
            annotateScenarioName( getScenario() );
            target.addComponent( scenarioNameLabel );
            scenarioPanel.expandScenarioEditPanel( target );
        }
        target.addComponent( scenarioActionsMenu );
    }
}
