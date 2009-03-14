package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.command.commands.UpdateScenarioObject;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.FlowDiagram;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.menus.PartActionsMenuPanel;
import com.mindalliance.channels.pages.components.menus.PartPagesMenuPanel;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 14, 2009
 * Time: 8:52:20 AM
 */
public class ScenarioPanel extends AbstractCommandablePanel {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ScenarioPanel.class );

    /**
     * Length a node title is abbreviated to
     */
    private static final int NODE_TITLE_MAX_LENGTH = 30;

    /**
     * The nodeDeleted property name.
     */
    // private static final String NODE_DELETED_PROPERTY = "nodeDeleted";                // NON-NLS


     /**
     * Node edited.
     */
    private Part part;

    /**
     * Part issues panel.
     */
    private IssuesPanel partIssuesPanel;
    /**
     * Scenario edit panel.
     */
    private ScenarioEditPanel scenarioEditPanel;

    private Set<Long> expansions;

    private IModel<Scenario> model;


    public ScenarioPanel( String id, IModel<Scenario> model, Part part, Set<Long> expansions ) {
        super( id, model );
        this.model = model;
        this.part = part;
        this.expansions = expansions;
        init();
    }

    private void init() {
        setOutputMarkupId( true );
        addScenarioEditPanel( getScenario() );
        addGraph( getScenario(), getPart() );
        addPartContent();
        FlowListPanel reqs = new FlowListPanel( "reqs", new PropertyModel<Node>( this, "part" ), false, expansions );
        add( reqs );
        FlowListPanel outcomes = new FlowListPanel( "outcomes", new PropertyModel<Node>( this, "part" ), true, expansions );
        add( outcomes );
        adjustComponents();
    }

    private void addPartContent() {
        Label nodeTitle = new Label( "part-title",                                                 // NON-NLS
                new AbstractReadOnlyModel<String>() {
                    @Override
                    public String getObject() {
                        return StringUtils.abbreviate( getPart().getTitle(), NODE_TITLE_MAX_LENGTH );
                    }
                } );
        nodeTitle.setOutputMarkupId( true );
        add( nodeTitle );               // NON-NLS
        addPartMenuBar( (Part) getPart() );
        PartPanel panel = new PartPanel( "specialty", new PropertyModel<Part>( this, "part" ));
        panel.setRenderBodyOnly( true );
        add( panel );
        add( new TextArea<String>( "description",                                     // NON-NLS
                new PropertyModel<String>( this, "nodeDescription" ) ) );
        add( new AttachmentPanel( "attachments", new Model<Node>( getPart() ) ) );                            // NON-NLS
        partIssuesPanel = new IssuesPanel( "issues",                                               // NON-NLS
                new PropertyModel<ModelObject>( this, "part" ),
                expansions );
        partIssuesPanel.setOutputMarkupId( true );
        add( partIssuesPanel );
    }

    private void adjustComponents() {
        boolean partHasIssues = Project.analyst().hasIssues( getPart(), false );
        partIssuesPanel.setVisible( partHasIssues );
        scenarioEditPanel.setVisible( expansions.contains( getScenario().getId() ) );
    }

    public void updateWith( AjaxRequestTarget target, Object context ) {
        adjustComponents();
        super.updateWith( target, context );
        target.addComponent( this );
    }

    private void addPartMenuBar( Part part ) {
        PartActionsMenuPanel partActionsMenu = new PartActionsMenuPanel(
                "partActionsMenu",
                new Model<Part>( part ) );
        partActionsMenu.setOutputMarkupId( true );
        add( partActionsMenu );
        MenuPanel partPagesMenu = new PartPagesMenuPanel(
                "partPagesMenu",
                new Model<Part>( part )
        );
        partPagesMenu.setOutputMarkupId( true );
        add( partPagesMenu );
    }

    public String getNodeDescription() {
        return getPart().getDescription();
    }

    public void setNodeDescription( String description ) {
        doCommand( new UpdateScenarioObject( getPart(), "description", description ) );
    }

    private void addGraph( final Scenario scenario, final Node n ) {
        MarkupContainer graph = new MarkupContainer( "graph" ) {                                      // NON-NLS

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

    /**
     * Add scenario-related components.
     *
     * @param scenario the underlying scenario
     */
    private void addScenarioEditPanel( final Scenario scenario ) {
        scenarioEditPanel = new ScenarioEditPanel(
                "sc-editor",
                new Model<Scenario>( scenario ),
                expansions );
        add( scenarioEditPanel );
    }


    public Node getPart() {
        return part;
    }

    public Scenario getScenario() {
        return model.getObject();
    }


}
