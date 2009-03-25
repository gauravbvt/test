package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Identifiable;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.ScenarioObject;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateScenarioObject;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.FlowDiagram;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.pages.components.menus.PartActionsMenuPanel;
import com.mindalliance.channels.pages.components.menus.PartShowMenuPanel;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
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
     * Length a part title is abbreviated to
     */
    private static final int PART_TITLE_MAX_LENGTH = 30;

    /**
     * Part issues panel.
     */
    private IssuesPanel partIssuesPanel;
    /**
     * Scenario edit panel.
     */
    private ScenarioEditPanel scenarioEditPanel;
    /**
     * Flow diagram container.
     */
    private MarkupContainer flowDiagram;
    /**
     * Expansions.
     */
    private Set<Long> expansions;
    /**
     * Scenario model.
     */
    private IModel<Scenario> scenarioModel;
    /**
     * Selected part model.
     */
    private IModel<Part> partModel;
    /**
     * Part's title.
     */
    private Label partTitle;
    /**
     * Part actions menu
     */
    private Component partActionsMenu;
    /**
     * Part pages menu
     */
    private Component partShowMenu;
    /**
     * Outcomes flow panel.
     */
    private FlowListPanel outcomesFlowPanel;
    /**
     * Requirements flow panel.
     */
    private FlowListPanel reqsFlowPanel;

    public ScenarioPanel(
            String id,
            IModel<Scenario> scenarioModel,
            IModel<Part> partModel,
            Set<Long> expansions ) {
        super( id, scenarioModel );
        this.scenarioModel = scenarioModel;
        this.partModel = partModel;
        this.expansions = expansions;
        init();
    }

    private void init() {
        setOutputMarkupId( true );
        addScenarioEditPanel( getScenario() );
        addFlowDiagram( getScenario(), partModel );
        addPartContent();
        reqsFlowPanel = new FlowListPanel(
                "reqs",
                new PropertyModel<Part>( this, "part" ),
                false,
                expansions );
        add( reqsFlowPanel );
        outcomesFlowPanel = new FlowListPanel(
                "outcomes",
                new PropertyModel<Part>( this, "part" ),
                true,
                expansions );
        add( outcomesFlowPanel );
        adjustComponents();
    }

    private void addPartContent() {
        partTitle = new Label( "part-title",                      // NON-NLS
                new AbstractReadOnlyModel<String>() {
                    @Override
                    public String getObject() {
                        return StringUtils.abbreviate(
                                getPart().getTitle(), PART_TITLE_MAX_LENGTH );
                    }
                } );
        partTitle.setOutputMarkupId( true );
        add( partTitle );               // NON-NLS
        addPartMenuBar();
        PartPanel panel = new PartPanel( "specialty", new PropertyModel<Part>( this, "part" ) );
        panel.setRenderBodyOnly( true );
        add( panel );
        add( new TextArea<String>( "description",                           // NON-NLS
                new PropertyModel<String>( this, "partDescription" ) ) );
        add( new AttachmentPanel( "attachments", new Model<Part>( getPart() ) ) );// NON-NLS
        partIssuesPanel = new IssuesPanel( "issues",                       // NON-NLS
                new PropertyModel<ModelObject>( this, "part" ),
                expansions );
        partIssuesPanel.setOutputMarkupId( true );
        add( partIssuesPanel );
    }

    private void adjustComponents() {
        boolean partHasIssues = Project.analyst().hasIssues( getPart(), false );
        makeVisible( partIssuesPanel, partHasIssues );
        makeVisible( scenarioEditPanel, expansions.contains( getScenario().getId() ) );
    }

    private void addPartMenuBar() {
        addPartActionsMenu();
        partShowMenu = new PartShowMenuPanel(
                "partShowMenu",
                new PropertyModel<Part>( this, "part" )
        );
        partShowMenu.setOutputMarkupId( true );
        add( partShowMenu );
    }

    private void addPartActionsMenu() {
        if ( isLockedByUser( getPart() ) ) {
            partActionsMenu = new PartActionsMenuPanel(
                    "partActionsMenu",
                    new PropertyModel<Part>( this, "part" ) );
        } else {
            String otherUser = getLockOwner( getPart() );
            partActionsMenu = new Label(
                    "partActionsMenu", new Model<String>( "Edited by " + otherUser ) );
            partActionsMenu.add(
                    new AttributeModifier( "class", true, new Model<String>( "locked" ) ) );
        }
        partActionsMenu.setOutputMarkupId( true );
        addOrReplace( partActionsMenu );
    }

    private void addFlowDiagram( final Scenario scenario, final IModel<Part> partModel ) {
        flowDiagram = new MarkupContainer( "graph" ) {                        // NON-NLS

            @Override
            protected void onComponentTag( ComponentTag tag ) {
                super.onComponentTag( tag );
                tag.put( "src",                                                       // NON-NLS
                        MessageFormat.format( // NON-NLS
                                "scenario.png?"
                                        + "scenario={0,number,0}"
                                        + "&node={1,number,0}"
                                        + "&time={2,number,0}",
                                scenario.getId(),
                                partModel.getObject().getId(),
                                System.currentTimeMillis() ) );
            }

            @Override
            protected void onRender( MarkupStream markupStream ) {
                super.onRender( markupStream );
                try {
                    DiagramFactory diagramFactory = Project.diagramFactory();
                    FlowDiagram diagram = diagramFactory.newFlowDiagram( scenario );
                    getResponse().write( diagram.makeImageMap() );
                } catch ( DiagramException e ) {
                    LOG.error( "Can't generate image map", e );
                }
            }
        };

        flowDiagram.setOutputMarkupId( true );
        add( flowDiagram );
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


    public Part getPart() {
        return partModel.getObject();
    }

    public Scenario getScenario() {
        return scenarioModel.getObject();
    }

    public String getPartDescription() {
        return getPart().getDescription();
    }

    /**
     * Set part description.
     *
     * @param description a string
     */
    public void setPartDescription( String description ) {
        doCommand( new UpdateScenarioObject( getPart(), "description", description ) );
    }

    /**
     * Force scenario edit panel to expand.
     *
     * @param target an ajax request target
     */
    public void expandScenarioEditPanel( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Expanded, getScenario() );
        update( target, change );
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change ) {
        Identifiable identifiable = change.getSubject();
        if ( identifiable == getScenario() && change.isDisplay() ) {
            scenarioEditPanel.setVisibility( target, expansions.contains( getScenario().getId() ) );
            target.addComponent( scenarioEditPanel );
        }
        if ( identifiable == getPart() && change.isUpdated() ) {
            target.addComponent( partTitle );
            reqsFlowPanel.refresh( target );
            outcomesFlowPanel.refresh( target );
        }
        if ( identifiable instanceof Issue || identifiable instanceof ScenarioObject ) {
            if ( !change.isDisplay() ) target.addComponent( flowDiagram );
            target.addComponent( partIssuesPanel );
        }
        makeVisible( target, partIssuesPanel, Project.analyst().hasIssues( getPart(), false ) );
        addPartActionsMenu();
        target.addComponent( partShowMenu );
        target.addComponent( partActionsMenu );
        super.updateWith( target, change );
    }

}
