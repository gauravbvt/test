package com.mindalliance.channels.pages;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.analysis.ScenarioAnalyst;
import com.mindalliance.channels.dao.NotFoundException;
import com.mindalliance.channels.dao.ScenarioDao;
import com.mindalliance.channels.export.Importer;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.FlowDiagram;
import com.mindalliance.channels.pages.components.AttachmentPanel;
import com.mindalliance.channels.pages.components.FlowListPanel;
import com.mindalliance.channels.pages.components.PartPanel;
import com.mindalliance.channels.pages.components.ScenarioEditPanel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
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
    private Set<Long> expansions;

    /** The current node. */
    private Node node;

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
            final Node n = findNode( scenario, parameters );
            if ( n != null )
                init( scenario, n, findExpansions( parameters ) );
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
        final Set<Long> expanded = Collections.emptySet();
        init( scenario, node, expanded );
    }

    private static Set<Long> findExpansions( PageParameters parameters ) {
        final Set<Long> result = new HashSet<Long>( parameters.size() );
        if ( parameters.containsKey( EXPAND_PARM ) ) {
            final List<String> stringList =
                    Arrays.asList( parameters.getStringArray( EXPAND_PARM ) );
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
     * Find scenario specified in parameters.
     * @param scenarioDao the scenario container
     * @param parameters the page parameters
     * @return a scenario, or null if not found
     */
    static Scenario findScenario( ScenarioDao scenarioDao, PageParameters parameters ) {
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
    static Node findNode( Scenario scenario, PageParameters parameters ) {
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

    private void redirectTo( Node n ) {
        final long sid = n.getScenario().getId();
        final long nid = n.getId();
        setResponsePage(
                new RedirectPage(
                        MessageFormat.format( "?scenario={0}&node={1}", sid, nid ) ) );   // NON-NLS
    }

    private void redirectHere() {
        final long sid = node.getScenario().getId();
        final long nid = node.getId();
        final StringBuffer exps = new StringBuffer();
        for ( long id : expansions ) {
            exps.append( "&expand=" );                                                    // NON-NLS
            exps.append( Long.toString( id ) );
        }
        setResponsePage(
                new RedirectPage( MessageFormat.format( "?scenario={0}&node={1}{2}",      // NON-NLS
                                                        sid, nid, exps ) ) );
    }

    /**
     * Return initialized parameters for given scenario and node.
     * @param scenario the scenario
     * @param node the node, maybe null (in which case, would link to first node in scenario)
     * @param expanded components id that should be expanded
     * @return page parameters to use in links, etc.
     */
    public static PageParameters getParameters(
            Scenario scenario, Node node, Set<Long> expanded ) {

        final PageParameters result = new PageParameters();
        result.put( SCENARIO_PARM, Long.toString( scenario.getId() ) );
        if ( node != null ) {
            result.put( NODE_PARM, Long.toString( node.getId() ) );
            for ( long id : expanded )
                result.add( EXPAND_PARM, Long.toString( id  ) );
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
        final Set<Long> expansions = Collections.emptySet();
        return getParameters( scenario, node, expansions );
    }

    /**
     * Return initialized parameters for given scenario and node.
     * @param scenario the scenario
     * @param node the node, maybe null (in which case, would link to first node in scenario)
     * @param id the id to expand
     * @return page parameters to use in links, etc.
     */
    public static PageParameters getParameters( Scenario scenario, Node node, long id ) {
        final Set<Long> expansions = new HashSet<Long>( 1 );
        expansions.add( id );
        return getParameters( scenario, node, expansions );
    }

    /** Set content-type to application/xhtml+xml. */
    @Override
    protected void configureResponse() {
        super.configureResponse();
        getResponse().setContentType( "application/xhtml+xml" );                          // NON-NLS
    }

    private void init( Scenario scenario, Node n, Set<Long> expanded ) {
        node = n;
        expansions = expanded;

        setVersioned( false );
        setStatelessHint( true );

        add( new Label( "sc-title", new PropertyModel<String>( scenario, "name" ) ) );    // NON-NLS
        add( new ScenarioForm( "big-form", scenario, n ) );                               // NON-NLS

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

    public Node getNode() {
        return node;
    }

    //==============================================================
    /**
     * The scenario form.
     */
    private final class ScenarioForm extends Form<Scenario> {

        /** The scenario import field. */
        private FileUploadField scenarioImport;

        /**
         * The scenario to display after submit.
         * If null, redisplay current node.
         */
        private Scenario target;

        /**
         * The delete scenario check box.
         */
        private DeleteBox deleteScenario;

        //------------------------------
        private ScenarioForm( String id, Scenario scenario, Node node ) {
            super( id, new Model<Scenario>( scenario ) );
            target = scenario;

            add( new Label( "node-title", new PropertyModel<String>( node, "title" ) ) ); // NON-NLS
            add( new TextArea<String>( "description",                                     // NON-NLS
                     new PropertyModel<String>( node, DESC_PROPERTY ) ) );
            addGraph( scenario, node );
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
        private void addGraph( final Scenario scenario, final Node n ) {
            add( new MarkupContainer( "graph" ) {                                         // NON-NLS
                @Override
                protected void onComponentTag( ComponentTag tag ) {
                    super.onComponentTag( tag );
                    tag.put( "src", MessageFormat.format(                                 // NON-NLS
                        "scenario.png?scenario={0}&amp;node={1}",                         // NON-NLS
                        scenario.getId(), n.getId() ) );
                }

                @Override
                protected void onRender( MarkupStream markupStream ) {
                    super.onRender( markupStream );
                    try {
                        final ScenarioAnalyst analyst = getProject().getScenarioAnalyst();
                        final FlowDiagram<Node,Flow> diagram = getProject().getFlowDiagram();
                        getResponse().write( diagram.getImageMap( scenario, analyst ) );

                    } catch ( DiagramException e ) {
                        LOG.error( "Can't generate image map", e );
                    }
                }
            } );
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

            if ( expansions.contains( scenario.getId() ) ) {
                add( new BookmarkablePageLink<Scenario>(
                    "sc-edit", ScenarioPage.class,                                        // NON-NLS
                    getParameters( scenario, node ) ) );

                add( new ScenarioEditPanel( "sc-editor", scenario ) );                    // NON-NLS

            } else {
                add( new BookmarkablePageLink<Scenario>( "sc-edit", ScenarioPage.class,   // NON-NLS
                    getParameters( scenario, node, scenario.getId() ) ) );
                add( new Label( "sc-editor" ) );                                          // NON-NLS
            }

            add( createExportScenario( "sc-export", scenario ) );                         // NON-NLS
            add( new NewScenarioLink( "sc-new" ) );                                       // NON-NLS

            deleteScenario = new DeleteBox( "sc-del" );                                   // NON-NLS
            add( deleteScenario );
            add( createSelectScenario( "sc-sel" ) );                                      // NON-NLS

            scenarioImport = new FileUploadField( "sc-import", new Model<FileUpload>() ); // NON-NLS
            add( scenarioImport );
        }

        //------------------------------
        private DropDownChoice<Scenario> createSelectScenario( String id ) {
            final List<Scenario> scenarios =
                    new ArrayList<Scenario>( ScenarioDao.INITIAL_CAPACITY );

            final Iterator<Scenario> iterator = getScenarioDao().scenarios();
            while ( iterator.hasNext() )
                scenarios.add( iterator.next() );

            return new DropDownChoice<Scenario>(
                    id, new PropertyModel<Scenario>( this, "target" ), scenarios ) {      // NON-NLS
                @Override
                protected void onSelectionChanged( Scenario newSelection ) {
                    setTarget( newSelection );
                }
            };
        }

        //------------------------------
        private BookmarkablePageLink<Scenario> createExportScenario(
                String id, Scenario scenario ) {

            return new BookmarkablePageLink<Scenario>(
                   id, ExportPage.class, getParameters( scenario, null ) );
        }

        public void setTarget( Scenario target ) {
            this.target = target;
        }

        public Scenario getTarget() {
            return target;
        }

        public Scenario getScenario() {
            return getModelObject();
        }

        //------------------------------
        @Override
        protected void onSubmit() {

            // TODO node deletion

            final FileUpload fileUpload = scenarioImport.getFileUpload();
            if ( fileUpload != null ) {
                // Import and switch to scenario
                final Importer importer = getProject().getImporter();
                try {
                    final InputStream inputStream = fileUpload.getInputStream();
                    final Scenario imported = importer.importScenario( inputStream );
                    setTarget( imported );
                } catch ( IOException e ) {
                    // TODO redirect to a proper error screen... user has to know...
                    final String s = "Import error";
                    LOG.error( s, e );
                    throw new RuntimeException( s, e );
                }
            }
            if ( deleteScenario.isSelected() ) {
                final ScenarioDao dao = getScenarioDao();
                final Scenario scenario = getScenario();
                dao.removeScenario( scenario );
                if ( LOG.isInfoEnabled() )
                    LOG.info( MessageFormat.format(
                            "Deleted scenario {0} - {1}",
                            scenario.getId(), scenario.getName() ) );
                setTarget( dao.getDefaultScenario() );
            }

            final Scenario t = getTarget();
            if ( t.getId() == getScenario().getId() )
                redirectHere();
            else
                redirectTo( t );
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
    private static final class DeleteBox extends CheckBox {

        /** The selection state of the checkbox. */
        private boolean selected;

        private DeleteBox( String id ) {
            super( id );
            setModel( new PropertyModel<Boolean>( this, "selected" ) );                   // NON-NLS
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected( boolean selected ) {
            this.selected = selected;
        }

    }
}
