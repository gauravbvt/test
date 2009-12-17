package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateScenarioObject;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.ScenarioObject;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.diagrams.FlowMapDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.Settings;
import com.mindalliance.channels.pages.components.menus.PartActionsMenuPanel;
import com.mindalliance.channels.pages.components.menus.PartShowMenuPanel;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.List;
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
                new PropertyModel<String>( this, "partDescription" ) );
        partDescription.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPart(), "description" ) );
            }
        } );
        partDescription.setOutputMarkupId( true );
        add( partDescription );

        AttachmentPanel attachments = new AttachmentPanel( "attachments", partModel );
        add( attachments );

        partIssuesPanel = new IssuesPanel( "issues", partModel, getExpansions() );        // NON-NLS
        partIssuesPanel.setOutputMarkupId( true );
        add( partIssuesPanel );
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
        } else if ( getCommander().isTimedOut() || getLockOwner( getPart() ) == null ) {
            partActionsMenu = new Label(
                    "partActionsMenu", new Model<String>( "Timed out" ) );

        } else {
            String otherUser = getLockOwner( getPart() );
            partActionsMenu = new Label(
                    "partActionsMenu", new Model<String>( "Edited by " + otherUser ) );
            partActionsMenu.add(
                    new AttributeModifier( "class", true, new Model<String>( "locked" ) ) );
        }
        partActionsMenu.setOutputMarkupId( true );
        partActionsMenu.setVisible( getPlan().isDevelopment() );
        addOrReplace( partActionsMenu );
    }

    private void addFlowSizing() {
        WebMarkupContainer reduceToFit = new WebMarkupContainer( "fit" );
        reduceToFit.add( new AbstractDefaultAjaxBehavior() {
            @Override
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

            @Override
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
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                flowDiagramDim = new double[2];
                addFlowDiagram();
                target.addComponent( flowMapDiagramPanel );
            }
        } );
        add( fullSize );
    }

    private void addFlowDiagram() {
        double[] dim = flowDiagramDim[0] <= 0.0 || flowDiagramDim[1] <= 0.0 ? null : flowDiagramDim;
        Settings settings = new Settings( "#graph", null, dim, true, true );

        flowMapDiagramPanel =
                new FlowMapDiagramPanel( "flow-map", scenarioModel, partModel, settings );
        flowMapDiagramPanel.setOutputMarkupId( true );
        addOrReplace( flowMapDiagramPanel );
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
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( !change.isNone() ) {
            Identifiable identifiable = change.getSubject();
            if ( identifiable == getPart() ) {
                if ( change.isUpdated() ) {
                    target.addComponent( partTitle );
                    reqsFlowPanel.refresh( target );
                    outcomesFlowPanel.refresh( target );
                }
            }
            if ( identifiable instanceof Issue || identifiable instanceof ScenarioObject ) {
                if ( !change.isDisplay() ) {
                    target.addComponent( flowMapDiagramPanel );
                    makeVisible( target, partIssuesPanel,
                            getAnalyst().hasIssues( getPart(), false ) );
                    target.addComponent( partIssuesPanel );
                }
            }
        }
        refreshMenus( target );
        super.updateWith( target, change, updated );
    }

    /**
     * Refresh.
     * @param target the ajax target
     * @param change a change
     */
    public void doRefresh( AjaxRequestTarget target, Change change ) {
        refresh( target, change, "" );
    }

    /**
     * {@inheritDoc}
     */
    protected void refresh( AjaxRequestTarget target, Change change, String aspect ) {
        Identifiable identifiable = change.getSubject();
        if ( change.isModified()
                || ( change.isDisplay()
                && identifiable instanceof ScenarioObject )
                || ( change.isSelected()
                && ( identifiable instanceof Scenario || identifiable instanceof ScenarioObject ) ) ) {
            if ( identifiable instanceof Issue
                    && change.isExists()
                    && ( (Issue) identifiable ).getAbout().getId() == getScenario().getId() ) {
                expandScenarioEditPanel( target );
            }
            adjustComponents();
            refreshMenus( target );
            partPanel.refresh( target );
            reqsFlowPanel.refresh( target );
            outcomesFlowPanel.refresh( target );
            target.addComponent( partDescription );
            target.addComponent( partIssuesPanel );
            if ( change.isModified() || change.isSelected() ) {
                addFlowDiagram();
                target.addComponent( flowMapDiagramPanel );
            }
        }
    }

    private void adjustComponents() {
        partDescription.setEnabled( isLockedByUser( getPart() ) );
        boolean partHasIssues = getAnalyst().hasIssues( getPart(), false );
        makeVisible( partIssuesPanel, partHasIssues );
    }

    /**
     * Refresh the flow map
     *
     * @param target an ajax request target
     */
    public void refreshFlowMapImage( AjaxRequestTarget target ) {
        flowMapDiagramPanel.refreshImage( target );
    }

    /**
     * Refresh all menus.
     *
     * @param target an ajax request target
     */
    public void refreshMenus( AjaxRequestTarget target ) {
        addPartActionsMenu();
        target.addComponent( partShowMenu );
        target.addComponent( partActionsMenu );
        reqsFlowPanel.refreshMenus( target );
        outcomesFlowPanel.refreshMenus( target );
    }
}
