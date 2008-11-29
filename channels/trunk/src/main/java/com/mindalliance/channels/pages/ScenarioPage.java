package com.mindalliance.channels.pages;

import com.mindalliance.channels.dao.NotFoundException;
import com.mindalliance.channels.dao.ScenarioDao;
import com.mindalliance.channels.export.Importer;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.pages.components.AttachmentPanel;
import com.mindalliance.channels.pages.components.FlowListPanel;
import com.mindalliance.channels.pages.components.PartPanel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.StringValueConversionException;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * The scenario editor page.
 */
public final class ScenarioPage extends WebPage {

    /** The 'scenario' parameter in the URL. */
    static final String SCENARIO_PARM = "scenario";                                       // NON-NLS

    /** The 'part' parameter in the URL. */
    static final String NODE_PARM = "node";                                               // NON-NLS

    /** The 'expand' parameter in the URL. */
    static final String EXPAND_PARM = "expand";                                           // NON-NLS

    /** Class logger. */
    private static final Log LOG = LogFactory.getLog( ScenarioPage.class );

    /** Name property name... */
    private static final String NAME_PROPERTY = "name";                                   // NON-NLS

    /** Description property name. */
    private static final String DESC_PROPERTY = "description";                            // NON-NLS

    /** Specialty field name. */
    private static final String SPECIALTY_FIELD = "specialty";                            // NON-NLS

    /** Id of components that are expanded. */
    private Set<String> expansions;

    /**
     * Used when page is called without parameters.
     * Redirect to default scenario, default node, all collapsed.
     */
    public ScenarioPage() {
        this( new PageParameters() );
    }

    public ScenarioPage( PageParameters parameters ) {
        // Call super to remember parameters in links
        super( parameters );

        final ScenarioDao scenarioDao = getScenarioDao();
        final Scenario scenario = findScenario( scenarioDao, parameters );

        if ( scenario == null )
            redirectTo( scenarioDao.getDefaultScenario() );

        else {
            final Node node = findNode( scenario, parameters );
            if ( node != null )
                init( scenario, node, findExpansions( parameters ) );
            else
                redirectTo( scenario );
        }
    }

    /**
     * Utility constructor for tests.
     * @param scenario a scenario
     */
    public ScenarioPage( Scenario scenario ) {
        this( scenario, scenario.nodes().next() );
    }

    /**
     * Utility constructor for tests.
     * @param scenario a scenario
     * @param node a node in the scenario
     */
    public ScenarioPage( Scenario scenario, Node node ) {
        final Set<String> expanded = Collections.emptySet();
        init( scenario, node, expanded );
    }

    private static Set<String> findExpansions( PageParameters parameters ) {
        final Set<String> result = new HashSet<String>( parameters.size() );
        if ( parameters.containsKey( EXPAND_PARM ) )
            result.addAll( Arrays.asList( parameters.getStringArray( EXPAND_PARM ) ) );

        return result;
    }

    /**
     * Find scenario specified in parameters.
     * @param scenarioDao the scenario container
     * @param parameters the page parameters
     * @return a scenario, or null if not found
     */
    private static Scenario findScenario( ScenarioDao scenarioDao, PageParameters parameters ) {
        if ( parameters.containsKey( SCENARIO_PARM ) )
            try {
                return scenarioDao.findScenario( parameters.getLong( SCENARIO_PARM ) );
            } catch ( StringValueConversionException ignored ) {
                LOG.warn( "Invalid scenario specified in parameters. Using default." );
            } catch ( NotFoundException ignored ) {
                LOG.warn( "Unknown scenario specified in parameters. Using default." );
            }
        return null;
    }

    /**
     * Find node specified in parameters.
     * @param scenario the scenario
     * @param parameters the page parameters
     * @return a node, or null if not found
     */
    private static Node findNode( Scenario scenario, PageParameters parameters ) {
        if ( parameters.containsKey( NODE_PARM ) )
            try {
                return scenario.getNode( parameters.getLong( NODE_PARM ) );
            } catch ( StringValueConversionException ignored ) {
                LOG.warn( "Invalid node specified in parameters. Using default." );
            }
        return null;
    }

    private void redirectTo( Scenario scenario ) {
        redirectTo( scenario.nodes().next() );
    }

    private void redirectTo( Node node ) {
        final long sid = node.getScenario().getId();
        final long nid = node.getId();
        setResponsePage(
                new RedirectPage(
                        MessageFormat.format( "?scenario={0}&node={1}", sid, nid ) ) );   // NON-NLS
    }

    /**
     * Return initialized parameters for given scenario and node.
     * @param scenario the scenario
     * @param node the node, maybe null (in which case, would link to first node in scenario)
     * @param expanded components id that should be expanded
     * @return page parameters to use in links, etc.
     */
    public static PageParameters getParameters(
            Scenario scenario, Node node, Set<String> expanded ) {

        final PageParameters result = new PageParameters();
        result.put( SCENARIO_PARM, scenario.getId() );
        if ( node != null ) {
            result.put( NODE_PARM, node.getId() );
            for ( String id : expanded )
                result.add( EXPAND_PARM, id );
        }
        return result;
    }

    /**
     * Return initialized parameters for given scenario and node.
     * @param scenario the scenario
     * @param node the node, maybe null (in which case, would link to first node in scenario)
     * @return page parameters to use in links, etc.
     */
    public static PageParameters getParameters( Scenario scenario, Node node ) {
        final Set<String> expansions = Collections.emptySet();
        return getParameters( scenario, node, expansions );
    }

    /** Set content-type to application/xhtml+xml. */
    @Override
    protected void configureResponse() {
        super.configureResponse();
        getResponse().setContentType( "application/xhtml+xml" );                          // NON-NLS
    }

    private void init( Scenario scenario, Node node, Set<String> expanded ) {
        expansions = expanded;

        final PropertyModel<String> title =
                new PropertyModel<String>( scenario, "name" );                            // NON-NLS
        add( new Label( "sc-title", title ) );                                            // NON-NLS

        final Form<?> form = new ScenarioForm( "big-form", scenario, node );              // NON-NLS
        add( form );

        LOG.info( "Scenario page generated" );
    }

    /**
     * Get the scenario manager from project via the application context.
     * @return the scenario DAO
     */
    private ScenarioDao getScenarioDao() {
        return getProject().getScenarioDao();
    }

    private Project getProject() {
        return (Project) getApplication();
    }

    //==============================================================
    /**
     * The scenario form.
     */
    private final class ScenarioForm extends Form<Scenario> {

        /** The scenario import field. */
        private FileUploadField scenarioImport;

        /** The node edited by the form. */
        private Node node;

        /**
         * The scenario to display after submit.
         * If null, redisplay current node.
         */
        private Scenario target;

        //------------------------------
        private ScenarioForm( String id, Scenario scenario, Node node ) {
            super( id );
            this.node = node;
            setMultiPart( true );

            add( new Label( "node-title", new PropertyModel<String>( node, "title" ) ) ); // NON-NLS
            add( new TextArea<String>( "node-desc",                                       // NON-NLS
                     new PropertyModel<String>( node, DESC_PROPERTY ) ) );

            final Component panel = node.isPart() ?
                    new PartPanel( SPECIALTY_FIELD, (Part) node )
                  : new Label( SPECIALTY_FIELD, "" );
            panel.setRenderBodyOnly( true );
            add( panel );

            add( new AttachmentPanel( "attachments", node ) );                            // NON-NLS

            addScenarioFields( scenario );

            add( new FlowListPanel( "reqs", node, false, expansions ) );                  // NON-NLS
            add( new FlowListPanel( "outcomes", node, true, expansions ) );               // NON-NLS
        }

        //------------------------------
        /**
         * Add scenario-related components.
         * @param scenario the underlying scenario
         */
        private void addScenarioFields( Scenario scenario ) {
            add( new Label( "header",                                                     // NON-NLS
                            new PropertyModel<String>( scenario, NAME_PROPERTY ) ) );
            add( new Label( "sc-desc",                                                    // NON-NLS
                            new PropertyModel<String>( scenario, DESC_PROPERTY ) ) );

            add( createExportScenario( "sc-export", scenario ) );                         // NON-NLS
            add( new NewScenarioLink( "sc-new" ) );                                       // NON-NLS

            add( new DeleteScenarioBox( "sc-del", scenario ) );                           // NON-NLS
            add( createSelectScenario( "sc-sel", scenario ) );                            // NON-NLS

            scenarioImport = new FileUploadField( "sc-import", new Model<FileUpload>() ); // NON-NLS
            add( scenarioImport );
        }

        //------------------------------
        private DropDownChoice<Scenario> createSelectScenario( String id, Scenario scenario ) {
            final List<Scenario> scenarios =
                    new ArrayList<Scenario>( ScenarioDao.INITIAL_CAPACITY );

            final Iterator<Scenario> iterator = getScenarioDao().scenarios();
            while ( iterator.hasNext() )
                scenarios.add( iterator.next() );

            return new DropDownChoice<Scenario>( id, new Model<Scenario>( scenario ), scenarios ) {
                @Override
                protected void onSelectionChanged( Scenario newSelection ) {
                    target = newSelection;
                }
            };
        }

        //------------------------------
        private BookmarkablePageLink<Scenario> createExportScenario(
                String id, Scenario scenario ) {

            final PageParameters parms = new PageParameters();
            parms.put( SCENARIO_PARM, Long.toString( scenario.getId() ) );
            return new BookmarkablePageLink<Scenario>( id, ExportPage.class, parms );
        }

        //------------------------------
        @Override
        protected void onSubmit() {
            super.onSubmit();

            final FileUpload fileUpload = scenarioImport.getFileUpload();
            if ( fileUpload != null ) {
                // Import and switch to scenario
                final Importer importer = getProject().getImporter();
                try {
                    final InputStream inputStream = fileUpload.getInputStream();
                    final Scenario imported = importer.importScenario( inputStream );
                    getScenarioDao().addScenario( imported );
                    target = imported;
                } catch ( IOException e ) {
                    final String s = "Import error";
                    LOG.error( s, e );
                    throw new RuntimeException( s, e );
                }
            }

            if ( target != null )
                redirectTo( target );
            else
                redirectTo( node );
        }
    }

    //==============================================================
        /**
         * A link that creates and link to a new scenario.
     */
    private final class NewScenarioLink extends Link<Scenario> {

        private NewScenarioLink( String id ) {
            super( id );
        }

        @Override
        public void onClick() {
            final Scenario newScenario = new Scenario();
            getScenarioDao().addScenario( newScenario );
            LOG.info( "Created new scenario" );
            redirectTo( newScenario );
        }
    }

    //==============================================================
    /**
     * A check box that causes the current scenario to be deleted,
     * if selected and form is submitted.
     */
    private final class DeleteScenarioBox extends CheckBox {

        /** The scenario to delete. */
        private final Scenario scenario;

        /** The selection state of the checkbox. */
        private boolean selected;

        private DeleteScenarioBox( String id, Scenario scenario ) {
            super( id );
            this.scenario = scenario;
            setModel( new PropertyModel<Boolean>( this, "selected" ) );                   // NON-NLS
        }

        @Override
        protected void onSelectionChanged( Object newSelection ) {
            if ( (Boolean) newSelection ) {
                final ScenarioDao dao = getScenarioDao();
                dao.removeScenario( scenario );
                if ( LOG.isInfoEnabled() )
                    LOG.info( MessageFormat.format(
                            "Deleted scenario {0} - {1}",
                            scenario.getId(), scenario.getName() ) );
                redirectTo( dao.getDefaultScenario() );
            }
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected( boolean selected ) {
            this.selected = selected;
        }

    }
}
