package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Identifiable;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.ScenarioObject;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateScenarioObject;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.diagrams.FlowMapDiagramPanel;
import com.mindalliance.channels.pages.components.menus.PartActionsMenuPanel;
import com.mindalliance.channels.pages.components.menus.PartShowMenuPanel;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

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
     * Flow diagram panel.
     */
    private FlowMapDiagramPanel flowMapDiagramPanel;

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
     * Part actions menu.
     */
    private Component partActionsMenu;

    /**
     * Part pages menu.
     */
    private Component partShowMenu;

    /**
     * Part panel.
     */
    private PartPanel partPanel;

    /**
     * Part description.
     */
    private TextArea<String> partDescription;

    /**
     * Outcomes flow panel.
     */
    private FlowListPanel outcomesFlowPanel;

    /**
     * Requirements flow panel.
     */
    private FlowListPanel reqsFlowPanel;
    /**
     * Attachement panel.
     */
    private AttachmentPanel attachments;
    /**
     * Width, height dimension contraints on the flow diagram.
     * In inches.
     * None if any is 0.
     */
    private double[] flowDiagramDim = new double[2];

    public ScenarioPanel(
            String id,
            IModel<Scenario> scenarioModel,
            IModel<Part> partModel,
            Set<Long> expansions ) {
        super( id, scenarioModel, expansions );
        this.scenarioModel = scenarioModel;
        this.partModel = partModel;
        init();
    }

    private void init() {
        setOutputMarkupId( true );
        addScenarioEditPanel();
        addFlowSizing();
        addFlowDiagram();
        addPartContent();
        reqsFlowPanel = new FlowListPanel(
                "reqs",
                partModel,
                false,
                getExpansions() );
        add( reqsFlowPanel );
        outcomesFlowPanel = new FlowListPanel(
                "outcomes",
                partModel,
                true,
                getExpansions() );
        add( outcomesFlowPanel );
        adjustComponents();
    }

    private void addPartContent() {
        partTitle = new Label( "part-title",                                              // NON-NLS
                new AbstractReadOnlyModel<String>() {
                    @Override
                    public String getObject() {
                        return StringUtils.abbreviate(
                                getPart().getTitle(), PART_TITLE_MAX_LENGTH );
                    }
                } );
        partTitle.setOutputMarkupId( true );
        partTitle.add( new AttributeModifier( "title", true,                              // NON-NLS
                new PropertyModel<String>( partModel, "title" ) ) );                      // NON-NLS
        add( partTitle );

        addPartMenuBar();
        partPanel = new PartPanel( "specialty", partModel );                              // NON-NLS
        partPanel.setOutputMarkupId( true );
        add( partPanel );

        partDescription = new TextArea<String>( "description",                            // NON-NLS
                new PropertyModel<String>( this, "partDescription" ) );                   // NON-NLS
        partDescription.setOutputMarkupId( true );
        add( partDescription );

        attachments = new AttachmentPanel( "attachments", partModel );                    // NON-NLS
        add( attachments );

        partIssuesPanel = new IssuesPanel( "issues", partModel, getExpansions() );        // NON-NLS
        partIssuesPanel.setOutputMarkupId( true );
        add( partIssuesPanel );
    }

    private void adjustComponents() {
        partDescription.setEnabled( isLockedByUser( getPart() ) );
        boolean partHasIssues = Channels.analyst().hasIssues( getPart(), false );
        makeVisible( partIssuesPanel, partHasIssues );
        makeVisible( scenarioEditPanel, getExpansions().contains( getScenario().getId() ) );
    }

    private void addPartMenuBar() {
        addPartActionsMenu();
        partShowMenu = new PartShowMenuPanel( "partShowMenu", partModel );
        partShowMenu.setOutputMarkupId( true );
        add( partShowMenu );
    }

    private void addPartActionsMenu() {
        if ( isLockedByUser( getPart() ) ) {
            partActionsMenu = new PartActionsMenuPanel(
                    "partActionsMenu",
                    partModel );
        } else if ( getCommander().isTimedOut() ) {
            partActionsMenu = new Label(
                    "partActionsMenu", new Model<String>( "Timed out" ) );
            partActionsMenu.add(
                    new AttributeModifier( "class", true, new Model<String>( "locked" ) ) );

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

    private void addFlowSizing() {
        WebMarkupContainer reduceToFit = new WebMarkupContainer( "fit" );
        reduceToFit.add( new AbstractDefaultAjaxBehavior() {
            protected void onComponentTag( ComponentTag tag ) {
                super.onComponentTag( tag );
                String domIdentifier = "#graph";
                String script = "wicketAjaxGet('"
                        + getCallbackUrl( true )
                        + "&width='+$('" + domIdentifier + "').width()+'"
                        + "&height='+$('" + domIdentifier + "').height()";
                String onclick = ( "{" + generateCallbackScript( script ) + " return false;}" )
                        .replaceAll( "&amp;", "&" );
                tag.put( "onclick", onclick );
            }

            protected void respond( AjaxRequestTarget target ) {
                RequestCycle requestCycle = RequestCycle.get();
                String swidth = requestCycle.getRequest().getParameter( "width" );
                String sheight = requestCycle.getRequest().getParameter( "height" );
                flowDiagramDim[0] = ( Double.parseDouble( swidth ) - 20 ) / 96.0;
                flowDiagramDim[1] = ( Double.parseDouble( sheight ) - 20 ) / 96.0;
                addFlowDiagram();
                target.addComponent( flowMapDiagramPanel );
            }
        } );
        add( reduceToFit );
        WebMarkupContainer fullSize = new WebMarkupContainer( "full" );
        fullSize.add( new AjaxEventBehavior( "onclick" ) {
            protected void onEvent( AjaxRequestTarget target ) {
                flowDiagramDim = new double[2];
                addFlowDiagram();
                target.addComponent( flowMapDiagramPanel );
            }
        } );
        add( fullSize );
    }

    private void addFlowDiagram() {
        if ( flowDiagramDim[0] <= 0.0 || flowDiagramDim[0] <= 0.0 ) {
            flowMapDiagramPanel = new FlowMapDiagramPanel(
                    "flow-map",
                    scenarioModel,
                    partModel,
                    null,
                    "#graph" );
        } else {
            flowMapDiagramPanel = new FlowMapDiagramPanel(
                    "flow-map",
                    scenarioModel,
                    partModel,
                    flowDiagramDim,
                    "#graph" );
        }
        flowMapDiagramPanel.setOutputMarkupId( true );
        addOrReplace( flowMapDiagramPanel );
    }

    /**
     * Add scenario-related components.
     */
    private void addScenarioEditPanel() {
        scenarioEditPanel = new ScenarioEditPanel(
                "sc-editor",                                                              // NON-NLS
                scenarioModel,
                getExpansions() );
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
    @Override
    public void updateWith( AjaxRequestTarget target, Change change ) {
        if ( !change.isNone() ) {
            Identifiable identifiable = change.getSubject();
            if ( identifiable == getScenario() && change.isDisplay() ) {
                scenarioEditPanel.setVisibility(
                        target, getExpansions().contains( getScenario().getId() ) );
                target.addComponent( scenarioEditPanel );

            } else if ( identifiable == getPart() && change.isUpdated() ) {
                target.addComponent( partTitle );
                reqsFlowPanel.refresh( target );
                outcomesFlowPanel.refresh( target );
            }

            if ( identifiable instanceof Issue || identifiable instanceof ScenarioObject ) {
                if ( !change.isDisplay() ) {
                    target.addComponent( flowMapDiagramPanel );
                    makeVisible( target, partIssuesPanel,
                            Channels.analyst().hasIssues( getPart(), false ) );
                    target.addComponent( partIssuesPanel );
                    // target.addComponent( attachments );
                }
            }
        }

        addPartActionsMenu();
        target.addComponent( partShowMenu );
        target.addComponent( partActionsMenu );
        super.updateWith( target, change );
    }

    /**
     * Refresh part panel and flow diagram.
     *
     * @param target ajax request target
     */
    public void refresh( AjaxRequestTarget target ) {
        // this.target = target;
        adjustComponents();
        addPartActionsMenu();
        target.addComponent( partShowMenu );
        target.addComponent( partActionsMenu );
        partPanel.refresh( target );
        addFlowDiagram();
        target.addComponent( flowMapDiagramPanel );
        adjustComponents();
        target.addComponent( scenarioEditPanel );
        target.addComponent( partDescription );
        target.addComponent( partIssuesPanel );
        target.addComponent( partIssuesPanel );
    }

    public void refreshScenarioEditPanel( AjaxRequestTarget target ) {
        scenarioEditPanel.refresh( target );
        makeVisible( scenarioEditPanel, getExpansions().contains( getScenario().getId() ) );
        target.addComponent( scenarioEditPanel );
    }

    public void refreshFlowMapImage( AjaxRequestTarget target ) {
        flowMapDiagramPanel.refreshImage( target );
    }
}
