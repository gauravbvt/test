/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.commands.AddPart;
import com.mindalliance.channels.core.command.commands.UpdateSegmentObject;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.ModelPage;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.MediaReferencesPanel;
import com.mindalliance.channels.pages.components.segment.menus.PartActionsMenuPanel;
import com.mindalliance.channels.pages.components.segment.menus.PartShowMenuPanel;
import com.mindalliance.channels.pages.components.social.SocialPanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.List;
import java.util.Set;

/**
 * Segment panel.
 */
public class SegmentPanel extends AbstractFlowMapContainingPanel {

    /**
     * Flow map panel DOM id.
     */
    static protected final String FLOW_MAP_DOM_ID = "#graph";

    /**
     * Part panel DOM id.
     */
    static private final String PART_DOM_ID = "#part";

    /**
     * Flow map issues panel DOM id.
     */
    static private final String ISSUES_DOM_ID = "#flowMapIssues";

    /**
     * Part panel css identifier.
     */
    private static final String PART_PANEL_ID = ".part-header";

    /**
     * Receives panel css identifier.
     */
    private static final String RECEIVE_PANEL_ID = ".receives";

    /**
     * CSS id.
     */
    private static final String SEGMENT_PANEL_ID = "#contents";

    /**
     * Sends panel css identifier.
     */
    private static final String SEND_PANEL_ID = ".sends";

    /**
     * CSS id.
     */
    private static final String SOCIAL_PANEL_ID = "#social";
    private static final int SOCIAL_WIDTH = 20;

    private boolean maximized;

    private boolean minimized;

    /**
     * Whether to show flow map issues.
     */
    private boolean showingIssues = false;


    /**
     * Overrides indicator.
     */
    private WebMarkupContainer overridesImage;

    /**
     * Part actions menu.
     */
    private Component partActionsMenu;

    /**
     * Quick access panel to part's media references.
     */
    private MediaReferencesPanel partMediaPanel;

    /**
     * Selected part model.
     */
    private IModel<Part> partModel;

    private boolean partOrFlowUpdated;

    /**
     * Part panel.
     */
    private Component partPanel;

    /**
     * Part pages menu.
     */
    private Component partShowMenu;

    /**
     * Receives flow panel.
     */
    private FlowListPanel receivesFlowPanel;

    /**
     * Sends flow panel.
     */
    private FlowListPanel sendsFlowPanel;

    /**
     * The social panel.
     */
    private SocialPanel socialPanel;

    /**
     * Task title container.
     */
    private WebMarkupContainer taskTitleContainer;
    private WebMarkupContainer checklistIcon;
    private WebMarkupContainer partAndFlowsContainer;
    private WebMarkupContainer flowMapIssuesContainer;
    private AjaxLink addPartLink;

    //-------------------------------
    public SegmentPanel( String id, IModel<Segment> segmentModel, IModel<Part> partModel, Set<Long> expansions ) {
        super( id, segmentModel, partModel, expansions );
        this.partModel = partModel;
        init();
    }

    private void init() {
        setOutputMarkupId( true );
        addFlowMapViewingControls();
        addFlowDiagram();
        addPartAndFlows();
        addFlowMapIssues();
        addSocialPanel();
    }

    public boolean isShowingIssues() {
        return showingIssues;
    }

    public void setShowingIssues( boolean showingIssues ) {
        this.showingIssues = showingIssues;
    }

    private void addFlowMapIssues() {
        flowMapIssuesContainer = new WebMarkupContainer( "flowMapIssuesContainer" );
        flowMapIssuesContainer.setOutputMarkupId( true );
        makeVisible( flowMapIssuesContainer, isShowingIssues() );
        addOrReplace( flowMapIssuesContainer );
        addFlowMapIssuesPanel();
    }

    private void addFlowMapIssuesPanel() {
        FlowMapIssuesPanel flowMapIssuesPanel = new FlowMapIssuesPanel(
                "flowMapIssues",
                partModel,
                new PropertyModel<Flow>( this, "selectedFlow" )
        );
        flowMapIssuesContainer.addOrReplace( flowMapIssuesPanel );
    }

    public Flow getSelectedFlow() {
        Segment segment = getSegment();
        for ( Long id : getExpansions() ) {
            Flow flow = segment.getFlow( id );
            if ( flow != null )
                return flow;
        }
        return null;
    }

    private void addPartAndFlows() {
        partAndFlowsContainer = new WebMarkupContainer( "partAndFlows" );
        partAndFlowsContainer.setOutputMarkupId( true );
        makeVisible( partAndFlowsContainer, !isShowingIssues() );
        addOrReplace( partAndFlowsContainer );
        addPartMenuBar();
        addPartTitleContainer();
        addPartMediaPanel();
        addPartHelp();
        addChecklistIcon();
        addOverridesImage();
        addPartPanel();
        addReceivesFlowPanel();
        addSendsFlowPanel();
    }

    private void addPartMenuBar() {
        addPartActionsMenu();
        addPartShowMenu();
        addAddPartButton();
    }

    private void addAddPartButton() {
        addPartLink = new AjaxLink( "addPart" ) {
            public void onClick( AjaxRequestTarget target ) {
                Command command = new AddPart( getUser().getUsername(), getSegment() );
                Change change = doCommand( command );
                update( target, change );
            }
        };
        addTipTitle( addPartLink, "Click to add a new task" );
        addPartLink.setOutputMarkupId( true );
        makeVisible( addPartLink, getCollaborationModel().isDevelopment()
                && getSegment().isModifiabledBy( getUsername(), getCommunityService() ) );
        partAndFlowsContainer.addOrReplace( addPartLink );
    }

    private void addPartActionsMenu() {
        if ( isCollapsed( getPart() ) ) {
            partActionsMenu = new Label( "partActionsMenu", new Model<String>( "" ) );
        } else if ( isLockedByUser( getPart() ) || getUser().isParticipant( getCollaborationModel().getUri() ) ) {
            partActionsMenu = new PartActionsMenuPanel( "partActionsMenu", partModel, getExpansions() );
        } else if ( getCommander().isTimedOut( getUser().getUsername() ) || getLockOwner( getPart() ) == null ) {
            partActionsMenu = timeOutLabel( "partActionsMenu" );
        } else {
            String otherUser = getLockOwner( getPart() );
            partActionsMenu = editedByLabel( "partActionsMenu", getPart(), otherUser );
        }
        partActionsMenu.setOutputMarkupId( true );
        makeVisible( partActionsMenu, getCollaborationModel().isDevelopment() && isExpanded( getPart() ) );
        partAndFlowsContainer.addOrReplace( partActionsMenu );
    }

    private void addPartShowMenu() {
        partShowMenu = new PartShowMenuPanel( "partShowMenu", partModel, getExpansions() );
        partShowMenu.setOutputMarkupId( true );
        partAndFlowsContainer.addOrReplace( partShowMenu );
    }

    private void addPartTitleContainer() {
        taskTitleContainer = new WebMarkupContainer( "task-title" );
        taskTitleContainer.setOutputMarkupId( true );
        taskTitleContainer.add( new AttributeModifier( "class", new Model<String>( cssClasses() ) ) );
        List<String> conceptualCauses = getAnalyst().findConceptualCausesInPlan( getCommunityService(), getPart() );
        if ( !conceptualCauses.isEmpty() ) {
            addTipTitle( taskTitleContainer, new Model<String>(
                    "Not executable: " + StringUtils.capitalize(
                            ChannelsUtils.listToString( conceptualCauses,
                                    ", and " ) ) ) );
        }
        partAndFlowsContainer.addOrReplace( taskTitleContainer );
    }

    private String cssClasses() {
        return getAnalyst().isEffectivelyConceptualInPlan( getCommunityService(), getPart() ) ? "task-title-noop" : "task-title";
    }

    private void addPartMediaPanel() {
        partMediaPanel = new MediaReferencesPanel( "partMedia", partModel, getExpansions() );
        partMediaPanel.setOutputMarkupId( true );
        partAndFlowsContainer.addOrReplace( partMediaPanel );
    }

    private void addChecklistIcon() {
        checklistIcon = new WebMarkupContainer( "checklist" );
        checklistIcon.setOutputMarkupId( true );
        partAndFlowsContainer.addOrReplace( checklistIcon );
        checklistIcon.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Expanded, getPart() ) );
                update( target, new Change( Change.Type.AspectViewed, getPart(), "checklist" ) );
            }
        } );
        int issueCount = getPart().countChecklistIssues( getAnalyst(), getCommunityService() );
        checklistIcon.add( new AttributeModifier(
                "src",
                issueCount == 0
                        ? "images/checklist.png"
                        : "images/checklist_issues.png") );
        addTipTitle(
                checklistIcon,
                "Open the checklist and show the details of the task if hidden"
                + ( issueCount > 0
                        ? (" - " + issueCount + (issueCount > 1 ? " checklist issues" : " checklist issue") )
                        : "")
        );
    }

    private void addPartHelp() {
        partAndFlowsContainer.add( makeHelpIcon( "help", "info-sharing", "add-task" ) );
    }

    private void addOverridesImage() {
        boolean overriding = getQueryService().isOverriding( getPart() );
        boolean overridden = getQueryService().isOverridden( getPart() );
        boolean overrides = overriding && overridden;
        String image = overrides ?
                "overridden-overriding.png" :
                overriding ? "overriding.png" : overridden ? "overridden.png" : "";
        String title = overrides ?
                "This task is overridden by and is overriding one or more tasks" :
                overriding ?
                        "This task is overriding one or more tasks" :
                        overridden ? "This task is overridden by one or more tasks" : "";
        overridesImage = new WebMarkupContainer( "overrides" );
        overridesImage.setOutputMarkupId( true );
        if ( overridden || overriding ) {
            overridesImage.add( new AttributeModifier( "src", new Model<String>( "images/" + image ) ) );
            addTipTitle( overridesImage, new Model<String>( title ) );
        }
        makeVisible( overridesImage, overridden || overriding );
        partAndFlowsContainer.addOrReplace( overridesImage );
    }

    private void addPartPanel() {
        partPanel = isExpanded( getPart() ) ?
                new ExpandedPartPanel( "part",
                        new PropertyModel<Part>( this, "part" ),
                        getExpansions(),
                        modelPage() ) :
                new CollapsedPartPanel( "part", new PropertyModel<Part>( this, "part" ), getExpansions() );
        partAndFlowsContainer.addOrReplace( partPanel );
    }

    private void addReceivesFlowPanel() {
        receivesFlowPanel = new FlowListPanel( "receives", partModel, false, getExpansions() );
        receivesFlowPanel.setOutputMarkupId( true );
        partAndFlowsContainer.addOrReplace( receivesFlowPanel );
    }

    private void addSendsFlowPanel() {
        sendsFlowPanel = new FlowListPanel( "sends", partModel, true, getExpansions() );
        sendsFlowPanel.setOutputMarkupId( true );
        partAndFlowsContainer.addOrReplace( sendsFlowPanel );
    }

    private void addSocialPanel() {
        String[] tabsShown = {SocialPanel.PRESENCE, SocialPanel.ACTIVITIES, SocialPanel.MESSAGES};
        socialPanel = new SocialPanel( "social", tabsShown );
        add( socialPanel );
    }

    public void resizeSocialAndGuidePanels( AjaxRequestTarget target, Change change ) {
        if ( change.isUnknown() || change.isCommunicated()
                || change.getId() == Channels.SOCIAL_ID && change.isDisplay() ) {
            boolean socialExpanded = getExpansions().contains( Channels.SOCIAL_ID );
            int socialPanelWidth = socialExpanded ? SOCIAL_WIDTH : 0;
            int segmentPanelWidth = 100 - socialPanelWidth;
            int segmentPanelLeft = socialExpanded ? SOCIAL_WIDTH : 0;
            final String script =
                    "$(\"" + SEGMENT_PANEL_ID + "\")" + ".css(\"width\",\"" + segmentPanelWidth + "%" + "\");"
                            + "$(\"" + SEGMENT_PANEL_ID + "\")" + ".css(\"left\",\"" + segmentPanelLeft + "%" + "\");"
                            + "$(\"" + SOCIAL_PANEL_ID + "\")" + ".css(\"width\",\"" + socialPanelWidth + "%" + "\");";
            target.prependJavaScript( script );
            target.add( flowMapDiagramPanel );
        }
    }

    /**
     * Update social panel.
     *
     * @param target an ajax request target
     */
    public void updateSocialPanel( AjaxRequestTarget target ) {
        socialPanel.refresh( target, new Change( Change.Type.Unknown ) );
    }


 /*   public void updateGuidePanel( AjaxRequestTarget target ) {
        boolean guideExpanded = getExpansions().contains( Channels.GUIDE_ID );
        makeVisible( guidePanel, guideExpanded );
        guidePanel.refresh( target, new Change( Change.Type.Refresh ) );
        resizeSocialAndGuidePanels( target, new Change( Change.Type.Unknown ) );
    }
*/

    //-------------------------------
    protected void addFlowMapViewingControls() {
        super.addFlowMapViewingControls();
        addMaximizeControl();
        addMinimizeFlowMapControl();
        addIssuesControl();
        addSimplifyControl();
    }

    private void addMaximizeControl() {
        // Maximize
        WebMarkupContainer fullscreen = new WebMarkupContainer( "maximized" );
        addTipTitle( fullscreen, "Maximize" );
        fullscreen.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                String props = isShowingGoals() ? "showGoals" : "";
                props += isShowingConnectors() ? " showConnectors" : "";
                props += isShowingAssets() ? " showAssets" : "";
                props += isHidingNoop() ? " hideNoop" : "";
                props += isSimplified() ? " simplify" : "";
                props += isTopBottom() ? "" : " leftRight";
                maximized = !maximized;
                addFlowMapViewingControls();
                target.add( getControlsContainer() );
                update( target, new Change( Change.Type.Maximized, getSegment(), props ) );
            }
        } );
        getControlsContainer().add( fullscreen );
    }

    private void addMinimizeFlowMapControl() {
        WebMarkupContainer shrinkExpand = new WebMarkupContainer( "minimized" );
        final String script =
                "if (! __channels_flowmap_minimized__) " + " {__graph_bottom = \"90%\"; __part_top = \"10%\"; }"
                        + " else {__graph_bottom = \"49.5%\"; __part_top = \"50.5%\";}" + " $(\"" + FLOW_MAP_DOM_ID
                        + "\").css(\"bottom\",__graph_bottom); "
                        + " $(\"" + PART_DOM_ID + "\").css(\"top\",__part_top);"
                        + " $(\"" + ISSUES_DOM_ID + "\").css(\"top\",__part_top);"
                        + " __channels_flowmap_minimized__ = !__channels_flowmap_minimized__;";
        shrinkExpand.add( new AttributeModifier( "onMouseUp", new Model<String>( script ) ) );
        shrinkExpand.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                minimized = !minimized;
                addFlowMapViewingControls();
                target.add( getControlsContainer() );
            }
        } );
        getControlsContainer().add( shrinkExpand );
        // icon
        WebMarkupContainer icon = new WebMarkupContainer( "split_icon" );
        icon.add( new AttributeModifier( "src",
                new Model<String>( minimized ? "images/split_on.png" : "images/split.png" ) ) );
        addTipTitle( icon, new Model<String>( minimized ? "Unsqueeze flow map" : "Squeeze flow map" ) );
        shrinkExpand.add( icon );
    }

    private void addIssuesControl() {
        // Simplify
        WebMarkupContainer displayFlowMapIssues = new WebMarkupContainer( "issues" );
        displayFlowMapIssues.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                setShowingIssues( !isShowingIssues() );
/*
                addFlowDiagram(); // todo - only display issues in red when showing issues?
                target.add( flowMapDiagramPanel );
*/
                makeVisible( partAndFlowsContainer, !isShowingIssues() );
                target.add( partAndFlowsContainer );
                makeVisible( flowMapIssuesContainer, isShowingIssues() );
                target.add( flowMapIssuesContainer );
                addFlowMapViewingControls();
                target.add( getControlsContainer() );
            }
        } );
        getControlsContainer().add( displayFlowMapIssues );
        // icon
        WebMarkupContainer icon = new WebMarkupContainer( "issues_icon" );
        icon.add( new AttributeModifier(
                "src",
                new Model<String>( isShowingIssues()
                        ? "images/hide_issues.png"
                        : "images/show_issues.png" ) ) );
        addTipTitle(
                icon,
                new Model<String>( isShowingIssues()
                        ? "Hide issues"
                        : "Show issues" ) );
        displayFlowMapIssues.add( icon );
    }




    public void changed( Change change ) {
        if ( change.isUpdated() && change.isForInstanceOf( SegmentObject.class ) ) {
            setPartOrFlowUpdated( true );
        }
        super.changed( change );
    }

    /**
     * Refresh.
     *
     * @param target the ajax target
     * @param change a change
     */
    public void doRefresh( AjaxRequestTarget target, Change change ) {
        refresh( target, change, "" );
    }

    /**
     * Force segment edit panel to expand.
     *
     * @param target an ajax request target
     */
    public void expandSegmentEditPanel( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Expanded, getSegment() );
        update( target, change );
    }

    @Override
    protected String getFlowMapDomId() {
        return FLOW_MAP_DOM_ID;
    }

    public Part getPart() {
        return partModel.getObject();
    }

    public String getPartDescription() {
        return getPart().getDescription();
    }

    /**
     * Have social panel create a new message.
     *
     * @param target an ajax request target
     * @param change a change referencing what the communication is about
     */
    public void newMessage( AjaxRequestTarget target, Change change ) {
        socialPanel.newMessage( target, change );
    }

    /**
     * {@inheritDoc}
     */
    protected void refresh( AjaxRequestTarget target, Change change, String aspect ) {
        resizeSocialAndGuidePanels( target, change );
        refreshAddPartButton( target );
        refreshMenus( target );
        Identifiable identifiable = change.getSubject( getCommunityService() );
        boolean stopUpdates = false;
        if ( identifiable instanceof Issue && change.isExists()
                && ( (Issue) identifiable ).getAbout().getId() == getSegment().getId() ) {
            expandSegmentEditPanel( target );
        } else {
            if ( change.isForInstanceOf( Flow.class ) && !change.isRemoved() ) {
                receivesFlowPanel.refresh( target );
                sendsFlowPanel.refresh( target );
                if ( change.isForProperty( "eois" ) && change.hasQualifier( "updated" ) ) {
                    if ( (Boolean) change.getQualifier( "updated" ) ) {
                        addFlowDiagram();
                        setPartOrFlowUpdated( false );
                        target.add( flowMapDiagramPanel );
                    }
                    stopUpdates = true;
                }
            } else {
                addPartPanel();
                receivesFlowPanel.refresh( target );
                sendsFlowPanel.refresh( target );
                resizePartPanels( target );
            }
            if ( !stopUpdates ) {
                addPartMediaPanel();
                addOverridesImage();
                addPartTitleContainer();
                target.add( taskTitleContainer );
                target.add( partMediaPanel );
                target.add( overridesImage );
                addFlowMapViewingControls();
                addFlowDiagram();
                addFlowMapIssues();
                setPartOrFlowUpdated( false );
                target.appendJavaScript( ModelPage.IE7CompatibilityScript );
                resizePartPanels( target );
                target.add( getControlsContainer() );
                target.add( flowMapDiagramPanel );
                target.add( flowMapIssuesContainer );
                target.add( taskTitleContainer );
                target.add( partMediaPanel );
                target.add( overridesImage );
                target.add( partActionsMenu );
                target.add( partShowMenu );
            }
        }
    }

    /**
     * Refresh the flow map
     *
     * @param target an ajax request target
     */
    public void refreshFlowMapImage( AjaxRequestTarget target ) {
        flowMapDiagramPanel.refreshImage( target );
    }

    private void refreshAddPartButton( AjaxRequestTarget target ) {
        addAddPartButton();
        target.add( addPartLink );
    }

    /**
     * Refresh all menus.
     *
     * @param target an ajax request target
     */
    public void refreshMenus( AjaxRequestTarget target ) {
        addPartActionsMenu();
        addPartShowMenu();
        addChecklistIcon();
        target.add( partShowMenu );
        target.add( partActionsMenu );
        target.add( checklistIcon );
    }

    /**
     * Refresh social panel.
     *
     * @param target an ajax request target
     * @param change a change referencing what the communication is about
     */
    public void refreshSocialPanel( AjaxRequestTarget target, Change change ) {
        resizeSocialAndGuidePanels( target, change );
        updateSocialPanel( target );
    }

    public void resizePartPanels( AjaxRequestTarget target ) {
        addPartPanel();
        target.add( partPanel );
        adjustPartPanelSizes( target, getPartPanelSizes() );
    }

    private void adjustPartPanelSizes( AjaxRequestTarget target, String[] sizes ) {
        String pl = sizes[0];
        String pr = sizes[1];
        String rl = sizes[2];
        String rr = sizes[3];
        String sl = sizes[4];
        String sr = sizes[5];
        final String script =
                "$(\"" + PART_PANEL_ID + "\")" + ".css(\"left\",\"" + pl + "\")" + ".css(\"right\",\"" + pr + "\");"
                        + "$(\"" + RECEIVE_PANEL_ID + "\")" + ".css(\"left\",\"" + rl + "\")" + ".css(\"right\",\"" + rr
                        + "\");" + "$(\"" + SEND_PANEL_ID + "\")" + ".css(\"left\",\"" + sl + "\")" + ".css(\"right\",\"" + sr
                        + "\");";
        target.prependJavaScript( script );
    }

    private String[] getPartPanelSizes() {
        String[] sizes = new String[6];
        boolean partExpanded = isPartExpanded();
        boolean receiveExpanded = isReceiveExpanded();
        boolean sendExpanded = isSendExpanded();
        String pl = "0";
        String pr = "66.66%";
        String rl = " 33.33%";
        String rr = "33.33%";
        String sl = "66.66%";
        String sr = "0";
        if ( partExpanded ) {
            if ( !receiveExpanded && !sendExpanded ) {
                pr = "50%";
                rl = "50%";
                rr = "25%";
                sl = "75%";
            } else if ( receiveExpanded && !sendExpanded ) {
                pr = "60%";
                rl = "40%";
                rr = "20%";
                sl = "80%";
            } else if ( !receiveExpanded && sendExpanded ) {
                pr = "60%";
                rl = "40%";
                rr = "40%";
                sl = "60%";
            }
        } else {
            if ( receiveExpanded && !sendExpanded ) {
                pr = "75%";
                rl = "25%";
                rr = "25%";
                sl = "75%";
            } else if ( !receiveExpanded && sendExpanded ) {
                pr = "75%";
                rl = "25%";
                rr = "50%";
                sl = "50%";
            } else if ( receiveExpanded && sendExpanded ) {
                pr = "80%";
                rl = "20%";
                rr = "40%";
                sl = "60%";
            }
        }
        sizes[0] = pl;
        sizes[1] = pr;
        sizes[2] = rl;
        sizes[3] = rr;
        sizes[4] = sl;
        sizes[5] = sr;
        return sizes;
    }

    private boolean isPartExpanded() {
        return getExpansions().contains( getPart().getId() );
    }

    private boolean isReceiveExpanded() {
        return CollectionUtils.exists( IteratorUtils.toList( getPart().receives() ), new Predicate() {
            public boolean evaluate( Object object ) {
                return getExpansions().contains( ( (Flow) object ).getId() );
            }
        } );
    }

    private boolean isSendExpanded() {
        return CollectionUtils.exists( IteratorUtils.toList( getPart().sends() ), new Predicate() {
            public boolean evaluate( Object object ) {
                return getExpansions().contains( ( (Flow) object ).getId() );
            }
        } );
    }

    /**
     * Set part description.
     *
     * @param description a string
     */
    public void setPartDescription( String description ) {
        doCommand( new UpdateSegmentObject( getUser().getUsername(), getPart(), "description", description ) );
    }

    /**
     * Update flow map diagram on "minimize".
     *
     * @param target an ajax request target
     * @param change a change
     */
    public void updateFlowMapOnMinimize( AjaxRequestTarget target, Change change ) {
        String property = change.getProperty();
        if ( property != null ) {
            setShowingGoals( property.contains( "showGoals" ) );
            setShowingConnectors( property.contains( "showConnectors" ) );
            setShowingAssets( property.contains( "showAssets" ) );
            setHidingNoop( property.contains( "hideNoop" ) );
            setSimplified( property.contains( "simplify" ) );
            setTopBottom( !property.contains( "leftRight" ) );
        }
        addFlowDiagram();
        setPartOrFlowUpdated( false );
        target.add( flowMapDiagramPanel );
        target.add( flowMapIssuesContainer );
        addFlowMapViewingControls();
        target.add( getControlsContainer() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        resizeSocialAndGuidePanels( target, change );
        boolean stopUpdates = change.isForInstanceOf(SegmentObject.class)
                && ( change.isExpanded() || change.isCollapsed() )
                && !isPartOrFlowUpdated();
        setPartOrFlowUpdated( isPartOrFlowUpdated()
                || change.hasQualifier( "updated" ) && (Boolean) change.getQualifier( "updated" ) );
        if ( !change.isNone() ) {
            Identifiable identifiable = change.getSubject( getCommunityService() );
            if ( identifiable == getPart() ) {
                if ( change.isUpdated() || change.isSelected() ) {
                    addPartMediaPanel();
                    addOverridesImage();
                    target.add( partMediaPanel );
                    target.add( overridesImage );
                    addPartTitleContainer();
                    target.add( taskTitleContainer );
                    if ( partPanel instanceof ExpandedPartPanel ) {
                        ( (ExpandedPartPanel) partPanel ).refresh( target, change, updated );
                    }
                    receivesFlowPanel.refresh( target );
                    sendsFlowPanel.refresh( target );
                }
            }
            if ( change.isExists() && identifiable instanceof Issue ) {
                addPartPanel();
                target.add( partPanel );
            }
            if ( identifiable instanceof Flow && change.isDisplay() ) {
                receivesFlowPanel.refresh( target );
                sendsFlowPanel.refresh( target );
            }
            if ( identifiable instanceof SegmentObject && change.isDisplay() ) {
                resizePartPanels( target );
                if ( identifiable instanceof Flow && ( change.isExpanded() || change.isCollapsed() ) ) {
                    addFlowDiagram();
                    target.add( flowMapDiagramPanel );
                }
                if ( change.isCollapsed() && isPartOrFlowUpdated() ) {
                    addFlowDiagram();
                    setPartOrFlowUpdated( false );
                    target.add( flowMapDiagramPanel );
                }
            }
            if ( !change.isExists() )
                refreshMenus( target );
            if ( !stopUpdates )
                super.updateWith( target, change, updated );
        } else {
            super.updateWith( target, change, updated );
        }
    }

    //-------------------------------
    public boolean isPartOrFlowUpdated() {
        return partOrFlowUpdated;
    }

    public void setPartOrFlowUpdated( boolean partOrFlowUpdated ) {
        this.partOrFlowUpdated = partOrFlowUpdated;
    }

}
