package com.mindalliance.channels.pages;

import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.UserIssue;
import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.commands.RemovePart;
import com.mindalliance.channels.export.Importer;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.FlowDiagram;
import com.mindalliance.channels.pages.components.AttachmentPanel;
import com.mindalliance.channels.pages.components.FlowListPanel;
import com.mindalliance.channels.pages.components.IssuesPanel;
import com.mindalliance.channels.pages.components.PartPanel;
import com.mindalliance.channels.pages.components.ScenarioEditPanel;
import com.mindalliance.channels.pages.components.ScenarioLink;
import com.mindalliance.channels.pages.components.Updatable;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.menus.ScenarioActionsMenuPanel;
import com.mindalliance.channels.pages.components.menus.ScenarioPagesMenuPanel;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
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
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
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
 * The scenario editor page.
 */
public final class ScenarioPage extends WebPage implements Updatable {

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
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ScenarioPage.class );

    /*
     * Name property name...
     */
//    private static final String NAME_PROPERTY = "name";                                 // NON-NLS

    /**
     * Description property name.
     */
    private static final String DESC_PROPERTY = "description";                            // NON-NLS

    /**
     * Specialty field name.
     */
    private static final String SPECIALTY_FIELD = "specialty";                            // NON-NLS

    /**
     * Length a node title is abbreviated to
     */
    private static final int NODE_TITLE_MAX_LENGTH = 23;

    /**
     * Length a scenario title is abbreviated to
     */
    private static final int SCENARIO_TITLE_MAX_LENGTH = 25;

    /**
     * Length a scenario title is abbreviated to
     */
    private static final int SCENARIO_DESCRIPTION_MAX_LENGTH = 120;
    /**
     * Id of components that are expanded.
     */
    private Set<Long> expansions;

    /**
     * The current node.
     */
    private Node node;

    /**
     * The big form.
     */
    private ScenarioForm form;
    /**
     * The label showing node's title
     */
    private Label nodeTitle;
    /**
     * Scenario title label in header.
     */
    private Label scenarioNameLabel;
    /**
     * Scenario description label in header.
     */
    private Label scenarioDescriptionLabel;
    /**
     * Dropdown with scenario choides
     */
    private DropDownChoice<Scenario> scenarioDropDownChoice;
    /**
     * Part issues panel.
     */
    private IssuesPanel partIssuesPanel;
    /**
     * A scenario edit panel
     */
    private ScenarioEditPanel scenarioEditPanel;
    /**
     * Scenario actions menu.
     */
    private MenuPanel scenarioActionsMenu;
    /**
     * Scenario pages menu.
     */
    private MenuPanel scenarioPagesMenu;

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
    public ScenarioPage( Scenario scenario ) {
        this( scenario, scenario.getDefaultPart() );
    }

    /**
     * Utility constructor for tests.
     *
     * @param scenario a scenario
     * @param node     a node in the scenario
     */
    public ScenarioPage( Scenario scenario, Node node ) {
        Set<Long> expanded = Collections.emptySet();
        init( scenario, node, expanded );
    }

    /**
     * Utility constructor.
     *
     * @param node the node to display
     * @param id   a section to expand
     */
    public ScenarioPage( Node node, long id ) {
        Set<Long> expanded = new HashSet<Long>();
        expanded.add( id );
        init( node.getScenario(), node, expanded );
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

    private void init( Scenario scenario, Node n, Set<Long> expanded ) {
        node = n;
        expansions = expanded;

        setVersioned( false );
        setStatelessHint( true );
        add( new Label( "sc-title", new PropertyModel<String>( scenario, "name" ) ) );    // NON-NLS
        form = new ScenarioForm( "big-form", scenario, n );                               // NON-NLS
        add( form );

        LOG.debug( "Scenario page generated" );
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

    public Node getNode() {
        return node;
    }

    /**
     * A component was changed. An update signal is received.
     *
     * @param target the ajax target
     */
    public void update( AjaxRequestTarget target ) {
        if ( !getService().list( Scenario.class ).contains( node.getScenario() ) ) {
            redirectTo( getService().getDefaultScenario() );
        } else {
            updateExceptScenarioEditPanel( target );
            if ( scenarioEditPanel != null ) scenarioEditPanel.update( target );
        }
    }

    /**
     * Ajax update of all that may need updating, except scenario edit panel.
     * To avoid circularity.
     *
     * @param target the ajax target
     */
    public void updateExceptScenarioEditPanel( AjaxRequestTarget target ) {
        target.addComponent( partIssuesPanel );
        target.addComponent( form.getGraph() );
        target.addComponent( nodeTitle );
        target.addComponent( scenarioActionsMenu );
        target.addComponent( scenarioNameLabel );
        target.addComponent( scenarioDescriptionLabel );
        target.addComponent( scenarioDropDownChoice );
    }

    //==============================================================
    /**
     * The scenario form.
     */
    private final class ScenarioForm extends Form<Scenario> {

        /**
         * The nodeDeleted property name.
         */
        private static final String NODE_DELETED_PROPERTY = "nodeDeleted";                // NON-NLS

        /**
         * The scenario import field.
         */
        private FileUploadField scenarioImport;

        /**
         * The scenario to display after submit.
         * If null, redisplay current node.
         */
        private Scenario target;

        /**
         * The delete scenario check box.
         */
       // private DeleteBox deleteScenario;

        /**
         * The requirement list.
         */
        private FlowListPanel reqs;

        /**
         * The outcomes list.
         */
        private FlowListPanel outcomes;

        /**
         * True if current node will be deleted.
         */
        private boolean nodeDeleted;

        /**
         * The graph section.
         */
        private MarkupContainer graph;

        //------------------------------
        private ScenarioForm( String id, Scenario scenario, final Node node ) {
            super( id, new Model<Scenario>( scenario ) );
            target = scenario;
/*
            add( new Label( "node-title",                                                 // NON-NLS
                    new PropertyModel<String>( node, "title" ) ) );                       // NON-NLS
*/
            nodeTitle = new Label( "node-title",                                                 // NON-NLS
                    new AbstractReadOnlyModel<String>() {
                        @Override
                        public String getObject() {
                            return StringUtils.abbreviate( node.getTitle(), NODE_TITLE_MAX_LENGTH );
                        }
                    } );
            nodeTitle.setOutputMarkupId( true );
            add( nodeTitle );               // NON-NLS
            add( new TextArea<String>( "description",                                     // NON-NLS
                    new PropertyModel<String>( node, DESC_PROPERTY ) ) );
            add( new CheckBox( "node-del",                                                // NON-NLS
                    new PropertyModel<Boolean>( this, NODE_DELETED_PROPERTY ) ) );
            addGraph( scenario, node );
            Component panel = node.isPart() ?
                    new PartPanel( SPECIALTY_FIELD, (Part) node )
                    : new Label( SPECIALTY_FIELD, "" );
            panel.setRenderBodyOnly( true );
            add( panel );

            // TODO simplify whole page... only displays parts, now.
            add( new ExternalLink( "profile", MessageFormat.format(                       // NON-NLS
                    "resource.html?scenario={0,number,0}&part={1,number,0}",                                // NON-NLS
                    scenario.getId(), node.getId() ) ) );
            add( new Link( "add-part-issue" ) {                                           // NON-NLS

                @Override
                public void onClick() {
                    UserIssue newIssue = new UserIssue( node );
                    getService().add( newIssue );
                    expansions.add( newIssue.getId() );
                    redirectHere();
                }
            } );
            add( new AttachmentPanel( "attachments", new Model<Node>( node ) ) );                            // NON-NLS
            partIssuesPanel = new IssuesPanel( "issues",                                               // NON-NLS
                    new Model<ModelObject>( node ) );
            partIssuesPanel.setOutputMarkupId( true );
            add( partIssuesPanel );
            addScenarioFields( scenario );
            reqs = new FlowListPanel( "reqs", node, false );                  // NON-NLS
            add( reqs );
            outcomes = new FlowListPanel( "outcomes", node, true );           // NON-NLS
            add( outcomes );
        }

        //------------------------------
        private void addGraph( final Scenario scenario, final Node n ) {
            graph = new MarkupContainer( "graph" ) {                                      // NON-NLS

                @Override
                protected void onComponentTag( ComponentTag tag ) {
                    super.onComponentTag( tag );
                    tag.put( "src",                                                       // NON-NLS
                            MessageFormat.format(
                                    "scenario.png?scenario={0,number,0}&node={1,number,0}&amp;time={2,number,0}", // NON-NLS
                                    scenario.getId(),
                                    n.getId(),
                                    System.currentTimeMillis() ) );
                }

                @Override
                protected void onRender( MarkupStream markupStream ) {
                    super.onRender( markupStream );
                    try {
                        DiagramFactory diagramFactory = Project.diagramFactory();
                        FlowDiagram flowDiagram = diagramFactory.newFlowDiagram( scenario );
                        getResponse().write( flowDiagram.makeImageMap() );
                    } catch ( DiagramException e ) {
                        LOG.error( "Can't generate image map", e );
                    }
                }
            };

            graph.setOutputMarkupId( true );
            add( graph );
        }

        public MarkupContainer getGraph() {
            return graph;
        }

        //------------------------------
        /**
         * Add scenario-related components.
         *
         * @param scenario the underlying scenario
         */
        private void addScenarioFields( final Scenario scenario ) {
            addHeader( scenario );
            addMenubar( scenario );
            if ( expansions.contains( scenario.getId() ) ) {
                scenarioEditPanel = new ScenarioEditPanel( "sc-editor", scenario );
                add( scenarioEditPanel );
            } else {
                add( new Label( "sc-editor" ) );                                          // NON-NLS                
            }
            scenarioImport = new FileUploadField( "sc-import", new Model<FileUpload>() ); // NON-NLS
            add( scenarioImport );
            add( createSelectScenario( "sc-sel" ) );                                      // NON-NLS
            add( new Label( "user", Project.getUserName() ) );                            // NON-NLS
        }

        //------------------------------
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
            Analyst analyst = ( (Project) getApplication() ).getAnalyst();
            String issue = analyst.getIssuesSummary(
                    scenario, Analyst.INCLUDE_PROPERTY_SPECIFIC );
            if ( !issue.isEmpty() ) {
                scenarioNameLabel.add( new AttributeModifier(
                        "class", true, new Model<String>( "error" ) ) );                  // NON-NLS
                scenarioNameLabel.add( new AttributeModifier(
                        "title", true, new Model<String>( issue ) ) );                    // NON-NLS
            }

            add( scenarioNameLabel );
            scenarioDescriptionLabel = new Label( "sc-desc",                                                    // NON-NLS
                    //  new PropertyModel<String>( scenario, DESC_PROPERTY )
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
            add( scenarioDescriptionLabel );
        }

        private void addMenubar( Scenario scenario ) {
            scenarioActionsMenu = new ScenarioActionsMenuPanel( "actionsMenu", new Model<Scenario>( scenario ), expansions );
            scenarioActionsMenu.setOutputMarkupId( true );
            add( scenarioActionsMenu );
            scenarioPagesMenu = new ScenarioPagesMenuPanel( "pagesMenu", new Model<Scenario>( scenario ) );
            scenarioPagesMenu.setOutputMarkupId( true );
            add( scenarioPagesMenu );
        }

        //------------------------------
        private DropDownChoice<Scenario> createSelectScenario( String id ) {
            scenarioDropDownChoice = new DropDownChoice<Scenario>(
                    id, new PropertyModel<Scenario>( this, "target" ),                    // NON-NLS
                    // getService().list( Scenario.class )
                    new PropertyModel<List<? extends Scenario>>( this, "allScenarios" )
            ) {

                @Override
                protected boolean wantOnSelectionChangedNotifications() {
                    return true;
                }

                @Override
                protected void onSelectionChanged( Scenario newSelection ) {
                    redirectTo( newSelection );
                }
            };
            scenarioDropDownChoice.setOutputMarkupId( true );

            return scenarioDropDownChoice;
        }

        public List<Scenario> getAllScenarios() {
            return getService().list( Scenario.class );
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

            reqs.deleteSelectedFlows( expansions );
            outcomes.deleteSelectedFlows( expansions );
            importScenario();

       /*     if ( deleteScenario.isSelected() ) {
                Service service = getService();
                Scenario scenario = getScenario();
                service.remove( scenario );
                if ( LOG.isInfoEnabled() )
                    LOG.info( MessageFormat.format(
                            "Deleted scenario {0} - {1}",
                            scenario.getId(), scenario.getName() ) );
                setTarget( service.getDefaultScenario() );
            }*/

            if ( isNodeDeleted() ) {
                if ( getNode().isPart() )
                    try {
                        Project.commander().doCommand( new RemovePart( (Part) getNode() ) );
                    } catch ( CommandException e ) {
                        e.printStackTrace();
                    }
                else
                    throw new RuntimeException( "Not allowed to delete a connector via UI" );
                redirectTo( getScenario() );

            } else {
                Scenario t = getTarget();
                if ( t.getId() == getScenario().getId() )
                    redirectHere();
                else
                    redirectTo( t );
            }

        }

        private void importScenario() {
            FileUpload fileUpload = scenarioImport.getFileUpload();
            if ( fileUpload != null ) {
                // Import and switch to scenario
                Importer importer = getProject().getImporter();
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

        public boolean isNodeDeleted() {
            return nodeDeleted;
        }

        public void setNodeDeleted( boolean nodeDeleted ) {
            this.nodeDeleted = nodeDeleted;
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

/*
    //==============================================================
    */
/**
     * A check box that causes the current scenario to be deleted,
     * if selected and form is submitted.
     */
/*
    private static final class DeleteBox extends CheckBox {

        */
/**
         * The selection state of the checkbox.
         */
/*
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
*/


}
