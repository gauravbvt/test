package com.mindalliance.channels.pages;

import com.mindalliance.channels.Node;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.UserIssue;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.export.Importer;
import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.command.LockManager;
import com.mindalliance.channels.pages.components.ScenarioPanel;
import com.mindalliance.channels.pages.components.ScenarioLink;
import com.mindalliance.channels.pages.components.Updatable;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.menus.ScenarioActionsMenuPanel;
import com.mindalliance.channels.pages.components.menus.ScenarioPagesMenuPanel;
import org.apache.wicket.PageParameters;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.StringValueConversionException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.InputStream;
import java.io.IOException;

/**
 * The scenario editor page.
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
    static final String NODE_PARM = "node";                                               // NON-NLS

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
     * The current node.
     */
    private Node node;
    /**
     * The scenario to display after submit.
     * If null, redisplay current node.
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
     * Used when page is called without parameters.
     * Redirect to default scenario, default node, all collapsed.
     */
    public ProjectPage() {
        this( new PageParameters() );
    }

    public ProjectPage( PageParameters parameters ) {
        // Call super to remember parameters in links
        super( parameters );

        Service service = getService();
        Scenario scenario = findScenario( service, parameters );

        if ( scenario == null )
            redirectTo( service.getDefaultScenario() );

        else {
            Node n = findNode( scenario, parameters );
            if ( n != null )
                init( scenario, n, findExpansions( parameters ) );
            else
                redirectTo( scenario );
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
     * @param node     a node in the scenario
     */
    public ProjectPage( Scenario scenario, Node node ) {
        Set<Long> expanded = Collections.emptySet();
        init( scenario, node, expanded );
    }

    /**
     * Utility constructor.
     *
     * @param node the node to display
     * @param id   a section to expand
     */
    public ProjectPage( Node node, long id ) {
        Set<Long> expanded = new HashSet<Long>();
        expanded.add( id );
        init( node.getScenario(), node, expanded );
    }

    private void init( Scenario scenario, Node n, Set<Long> expanded ) {
        getLockManager().releaseAllLocks( Project.getUserName() );
        getLockManager().requestLockOn( n );
        node = n;
        expansions = expanded;
        setVersioned( false );
        setStatelessHint( true );
        add( new Label( "sc-title", new PropertyModel<String>( scenario, "name" ) ) );    // NON-NLS
        form = new Form( "big-form" ) {
            protected void onSubmit() {
                importScenario();
            }
        };
        add( form );
        addHeader( scenario );
        addScenarioMenubar( scenario );
        addSelectScenario();
        ScenarioPanel scenarioPanel = new ScenarioPanel(
                "scenario",
                new Model<Scenario>( scenario ),
                (Part) getNode(),
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
            } catch ( IOException e ) {
                // TODO redirect to a proper error screen... user has to know...
                String s = "Import error";
                LOG.error( s, e );
                throw new RuntimeException( s, e );
            }
        }
    }

    private void addHeader( final Scenario scenario ) {
        Label scenarioNameLabel = new Label(
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
        Analyst analyst = ( (Project) getApplication() ).getAnalyst();
        String issue = analyst.getIssuesSummary(
                scenario, Analyst.INCLUDE_PROPERTY_SPECIFIC );
        if ( !issue.isEmpty() ) {
            scenarioNameLabel.add( new AttributeModifier(
                    "class", true, new Model<String>( "error" ) ) );                  // NON-NLS
            scenarioNameLabel.add( new AttributeModifier(
                    "title", true, new Model<String>( issue ) ) );                    // NON-NLS
        }

        form.add( scenarioNameLabel );
        Label scenarioDescriptionLabel = new Label( "sc-desc",                              // NON-NLS
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
        form.add( new Label( "user", Project.getUserName() ) );                            // NON-NLS
    }

    private void addScenarioMenubar( Scenario scenario ) {
        MenuPanel scenarioActionsMenu = new ScenarioActionsMenuPanel(
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
        DropDownChoice<Scenario> scenarioDropDownChoice = new DropDownChoice<Scenario>(
                "sc-sel", new PropertyModel<Scenario>( this, "target" ),                    // NON-NLS
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
     * Find node specified in parameters.
     *
     * @param scenario   the scenario
     * @param parameters the page parameters
     * @return a node, or null if not found
     */
    static Node findNode( Scenario scenario, PageParameters parameters ) {
        if ( parameters.containsKey( NODE_PARM ) )
            try {
                return scenario.getNode( parameters.getLong( NODE_PARM ) );
            } catch ( StringValueConversionException ignored ) {
                LOG.warn( "Invalid node specified in parameters. Using default." );
            }
        return null;
    }

    public void redirectTo( Scenario scenario ) {
        redirectTo( scenario.getDefaultPart() );
    }

    private void redirectTo( Node n ) {
        Set<Long> ids = Collections.emptySet();
        setResponsePage( new RedirectPage( ScenarioLink.linkStringFor( n, ids ) ) );
    }

    public void redirectHere() {
        long sid = node.getScenario().getId();
        long nid = node.getId();
        StringBuffer exps = new StringBuffer( 128 );
        for ( long id : expansions ) {
            exps.append( "&expand=" );                                                    // NON-NLS
            exps.append( Long.toString( id ) );
        }
        setResponsePage(
                new RedirectPage( MessageFormat.format( "?scenario={0,number,0}&node={1,number,0}{2}",      // NON-NLS
                        sid, nid, exps ) ) );
    }

    /**
     * Return initialized parameters for given scenario and node.
     *
     * @param scenario the scenario
     * @param node     the node, maybe null (in which case, would link to first node in scenario)
     * @param expanded components id that should be expanded
     * @return page parameters to use in links, etc.
     */
    public static PageParameters getParameters(
            Scenario scenario, Node node, Set<Long> expanded ) {

        PageParameters result = new PageParameters();
        result.put( SCENARIO_PARM, Long.toString( scenario.getId() ) );
        if ( node != null ) {
            result.put( NODE_PARM, Long.toString( node.getId() ) );
            for ( long id : expanded )
                result.add( EXPAND_PARM, Long.toString( id ) );
        }
        return result;
    }

    /**
     * Return initialized parameters for given scenario and node.
     *
     * @param scenario the scenario
     * @param node     the node, maybe null (in which case, would link to first node in scenario)
     * @return page parameters to use in links, etc.
     */
    public static PageParameters getParameters( Scenario scenario, Node node ) {
        Set<Long> expansions = Collections.emptySet();
        return getParameters( scenario, node, expansions );
    }

    /**
     * Return initialized parameters for given scenario and node.
     *
     * @param scenario the scenario
     * @param node     the node, maybe null (in which case, would link to first node in scenario)
     * @param id       the id to expand
     * @return page parameters to use in links, etc.
     */
    public static PageParameters getParameters( Scenario scenario, Node node, long id ) {
        Set<Long> expansions = new HashSet<Long>( 1 );
        expansions.add( id );
        return getParameters( scenario, node, expansions );
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

    public Node getNode() {
        return node;
    }

    /**
     * A component was changed. An update signal is received.
     *
     * @param target the ajax target
     */
    public void updateWith( AjaxRequestTarget target, Object context ) {
        Scenario scenario = node.getScenario();
        if ( !getService().list( Scenario.class ).contains( scenario ) ) {
            redirectTo( getService().getDefaultScenario() );
        } else {
            if ( context.equals( "undo" ) || context.equals( "redo" ) ) {
                redirectHere();
            } else if ( context instanceof Long ) {
                // toggle show/hide
                long id = (Long) context;
                if ( expansions.contains( id ) ) {
                    expansions.remove( id );
                } else {
                    expansions.add( (Long) context );
                }
            } else if ( context instanceof UserIssue ) {
                UserIssue userIssue = (UserIssue) context;
                expansions.add( userIssue.getId() );
                if ( userIssue.getAbout() == scenario ) {
                    expansions.add( scenario.getId() );
                    redirectHere();
                }
            }
        }
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


}
