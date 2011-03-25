package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.commands.AddPart;
import com.mindalliance.channels.command.commands.UpdateSegmentObject;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.SegmentObject;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.PlanPage;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.MediaReferencesPanel;
import com.mindalliance.channels.pages.components.segment.menus.PartActionsMenuPanel;
import com.mindalliance.channels.pages.components.segment.menus.PartShowMenuPanel;
import com.mindalliance.channels.pages.components.social.SocialPanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.List;
import java.util.Set;

/**
 * Segment panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 14, 2009
 * Time: 8:52:20 AM
 */
public class SegmentPanel extends AbstractFlowMapContainingPanel {

    /**
     * Flow map panel DOM id.
     */
    static protected final String FLOWMAP_DOM_ID = "#graph";

    /**
     * CSS id.
     */
    private static final String SEGMENT_PANEL_ID = "#contents";
    /**
     * CSS id.
     */
    private static final String SOCIAL_PANEL_ID = "#social";

    /**
     * Part panel DOM id.
     */
    static private final String PART_DOM_ID = "#part";

    /**
     * Selected part model.
     */
    private IModel<Part> partModel;

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
    private Component partPanel;

    /**
     * Sends flow panel.
     */
    private FlowListPanel sendsFlowPanel;

    /**
     * Receives flow panel.
     */
    private FlowListPanel receivesFlowPanel;

    /**
     * The social panel.
     */
    private SocialPanel socialPanel;

    /**
     * Part panel css identifier.
     */
    private static final String PART_PANEL_ID = ".part-header";
    /**
     * Receives panel css identifier.
     */
    private static final String RECEIVE_PANEL_ID = ".receives";
    /**
     * Sends panel css identifier.
     */
    private static final String SEND_PANEL_ID = ".sends";
    /**
     * Overrides indicator.
     */
    private WebMarkupContainer overridesImage;
    /**
     * Quick access panel to part's media references.
     */
    private MediaReferencesPanel partMediaPanel;

    /**
     * Task title container.
     */
    private WebMarkupContainer taskTitleContainer;
    private boolean minimized = false;
    private boolean maximized = false;

    public SegmentPanel(
            String id,
            IModel<Segment> segmentModel,
            IModel<Part> partModel,
            Set<Long> expansions ) {
        super( id, segmentModel, partModel, expansions );
        this.partModel = partModel;
        init();
    }

    @Override
    protected String getFlowMapDomId() {
        return FLOWMAP_DOM_ID;
    }

    private void init() {
        setOutputMarkupId( true );
        addFlowMapViewingControls();
        addFlowDiagram();
        addPartMenuBar();
        addPartTitleContainer();
        addPartMediaPanel();
        addOverridesImage();
        addPartPanel();
        addReceivesFlowPanel();
        addSendsFlowPanel();
        addSocialPanel();
        adjustComponents();
    }

    private void addOverridesImage() {
        boolean overriding = getQueryService().isOverriding( getPart() );
        boolean overridden = getQueryService().isOverridden( getPart() );
        boolean overrides = overriding && overridden;
        String image = overrides
                ? "overridden-overriding.png"
                : overriding
                ? "overriding.png"
                : overridden
                ? "overridden.png"
                : "";
        String title = overrides
                ? "This task is overridden by and is overriding one or more tasks"
                : overriding
                ? "This task is overriding one or more tasks"
                : overridden
                ? "This task is overridden by one or more tasks"
                : "";
        overridesImage = new WebMarkupContainer( "overrides" );
        overridesImage.setOutputMarkupId( true );
        if ( overridden || overriding ) {
            overridesImage.add( new AttributeModifier(
                    "src",
                    true,
                    new Model<String>( "images/" + image )
            ) );
            overridesImage.add( new AttributeModifier(
                    "title",
                    true,
                    new Model<String>( title )
            ) );
        }
        makeVisible( overridesImage, overridden || overriding );
        addOrReplace( overridesImage );
    }


    private void addPartMediaPanel() {
        partMediaPanel = new MediaReferencesPanel(
                "partMedia",
                partModel,
                getExpansions()
        );
        partMediaPanel.setOutputMarkupId( true );
        addOrReplace( partMediaPanel );
    }

    private void addPartTitleContainer() {
        taskTitleContainer = new WebMarkupContainer(
                "task-title" );
        taskTitleContainer.setOutputMarkupId( true );
        taskTitleContainer.add( new AttributeModifier(
                "class",
                true,
                new Model<String>( cssClasses() ) ) );
        addOrReplace( taskTitleContainer );
    }

    private String cssClasses() {
        return getPart().isOperational() ? "task-title" : "task-title-noop";
    }

    private void addReceivesFlowPanel() {
        receivesFlowPanel = new FlowListPanel(
                "receives",
                partModel,
                false,
                getExpansions() );
        receivesFlowPanel.setOutputMarkupId( true );
        addOrReplace( receivesFlowPanel );

    }

    private void addSendsFlowPanel() {
        sendsFlowPanel = new FlowListPanel(
                "sends",
                partModel,
                true,
                getExpansions() );
        sendsFlowPanel.setOutputMarkupId( true );
        addOrReplace( sendsFlowPanel );
    }

    private void addPartPanel() {
        if ( isExpanded( getPart() ) ) {
            partPanel = new ExpandedPartPanel(
                    "part",
                    new PropertyModel<Part>( this, "part" ),
                    getExpansions() );
        } else {
            partPanel = new CollapsedPartPanel(
                    "part",
                    new PropertyModel<Part>( this, "part" ),
                    getExpansions() );
        }
        addOrReplace( partPanel );
    }

    private void addPartMenuBar() {
        addPartActionsMenu();
        addPartShowMenu();
        AjaxFallbackLink addPartLink = new AjaxFallbackLink( "addPart" ) {
            public void onClick( AjaxRequestTarget target ) {
                Command command = new AddPart( getSegment() );
                Change change = doCommand( command );
                update( target, change );
            }
        };
        addPartLink.setVisible( getPlan().isDevelopment() );
        add( addPartLink );
    }

    private void addPartShowMenu() {
        partShowMenu = new PartShowMenuPanel( "partShowMenu", partModel, getExpansions() );
        partShowMenu.setOutputMarkupId( true );
        addOrReplace( partShowMenu );
    }

    private void addPartActionsMenu() {
        if ( isCollapsed( getPart() ) ) {
            partActionsMenu = new Label(
                    "partActionsMenu", new Model<String>( "" ) );
        } else if ( isLockedByUser( getPart() ) ) {
            partActionsMenu = new PartActionsMenuPanel(
                    "partActionsMenu",
                    partModel,
                    getExpansions() );
        } else if ( getCommander().isTimedOut()
                || getLockOwner( getPart() ) == null ) {
            partActionsMenu = timeOutLabel( "partActionsMenu" );

        } else {
            String otherUser = getLockOwner( getPart() );
            partActionsMenu = editedByLabel( "partActionsMenu", getPart(), otherUser );
        }
        partActionsMenu.setOutputMarkupId( true );
        makeVisible( partActionsMenu, getPlan().isDevelopment() && isExpanded( getPart() ) );
        addOrReplace( partActionsMenu );
    }

    protected void addFlowMapViewingControls() {
        super.addFlowMapViewingControls();
        addMaximizeControl();
        addMinimizeFlowMapControl();
    }


    public Part getPart() {
        return partModel.getObject();
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
        doCommand( new UpdateSegmentObject( getPart(), "description", description ) );
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

    private void addSocialPanel() {
        socialPanel = new SocialPanel( "social" );
        add( socialPanel );
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        resizeSocialPanel( target, change );
        boolean stopUpdates = false;
        if ( !change.isNone() ) {
            Identifiable identifiable = change.getSubject( getQueryService() );
            if ( identifiable == getPart() ) {
                if ( change.isUpdated() ) {
                    addPartMediaPanel();
                    addOverridesImage();
                    target.addComponent( partMediaPanel );
                    target.addComponent( overridesImage );
                    addPartTitleContainer();
                    target.addComponent( taskTitleContainer );
                    if ( partPanel instanceof ExpandedPartPanel ) {
                        ( (ExpandedPartPanel) partPanel ).refresh( target, change, updated );
                    }
                    receivesFlowPanel.refresh( target );
                    sendsFlowPanel.refresh( target );
                }
                if ( change.isSelected() || change.isUndoing() || change.isUnknown() ) {
                    addReceivesFlowPanel();
                    addSendsFlowPanel();
                    target.addComponent( receivesFlowPanel );
                    target.addComponent( sendsFlowPanel );
                }
            }
            if ( change.isForInstanceOf( Flow.class ) && change.isSelected() ) {
                receivesFlowPanel.refresh( target );
                sendsFlowPanel.refresh( target );
            }
            if ( identifiable instanceof Issue || identifiable instanceof SegmentObject ) {
                if ( !( change.isUpdated() && isExpanded( identifiable ) ) && !( change.isDisplay() ) ) {
                    target.addComponent( flowMapDiagramPanel );
                }
            }
            if ( change.isExists() && identifiable instanceof Issue ) {
                addPartPanel();
                target.addComponent( partPanel );
            }
            if ( identifiable instanceof SegmentObject
                    && ( change.isExpanded() || change.isCollapsed() ) ) {
                resizePartPanels( target );
                stopUpdates = change.isExpanded();
            }
            refreshMenus( target );
            if ( !stopUpdates )
                super.updateWith( target, change, updated );
        } else {
            super.updateWith( target, change, updated );
        }
    }

    public void resizePartPanels( AjaxRequestTarget target ) {
        String[] sizes = getPartPanelSizes();
        addPartPanel();
        adjustPartPanelSizes( target, sizes );
        target.addComponent( partPanel );
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
     * {@inheritDoc}
     */
    protected void refresh( AjaxRequestTarget target, Change change, String aspect ) {
        resizeSocialPanel( target, change );
        Identifiable identifiable = change.getSubject( getQueryService() );
        if ( change.isModified()
                || change.isMinimized()
                || ( change.isDisplay()
                && identifiable instanceof SegmentObject )
                || ( change.isSelected()
                && ( identifiable instanceof Segment || identifiable instanceof SegmentObject ) ) ) {
            if ( identifiable instanceof Issue
                    && change.isExists()
                    && ( (Issue) identifiable ).getAbout().getId() == getSegment().getId() ) {
                expandSegmentEditPanel( target );
            }
            refreshMenus( target );
            // receivesFlowPanel.refresh( target );
            // sendsFlowPanel.refresh( target );
            if ( change.isMinimized() || change.isModified() || change.isSelected() ) {
                String props = change.getProperty();
                if ( props != null ) {
                    setShowingGoals( props.contains( "showGoals" ) );
                    setShowingConnectors( props.contains( "showConnectors" ) );
                    // TODO - what about operationals?
                }
                addFlowDiagram();
                target.addComponent( flowMapDiagramPanel );
            }
            target.appendJavascript( PlanPage.IE7CompatibilityScript );
            addPartTitleContainer();
            target.addComponent( taskTitleContainer );
            addPartMediaPanel();
            addOverridesImage();
            target.addComponent( partMediaPanel );
            target.addComponent( overridesImage );
            resizePartPanels( target );
            adjustComponents();
        }
        addFlowMapViewingControls();
        target.addComponent( getControlsContainer() );
    }

    private void addMaximizeControl() {
        // Maximize
        WebMarkupContainer fullscreen = new WebMarkupContainer( "maximized" );
        fullscreen.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                String props = isShowingGoals() ? "showGoals" : "";
                props += isShowingConnectors() ? " showConnectors" : "";
                props += isHidingNoop() ? " hideNoop" : "";
                maximized = !maximized;
                addFlowMapViewingControls();
                target.addComponent( getControlsContainer() );
                update( target, new Change( Change.Type.Maximized, getSegment(), props ) );
            }
        } );
        getControlsContainer().add( fullscreen );
    }


    private void addMinimizeFlowMapControl() {
        WebMarkupContainer shrinkExpand = new WebMarkupContainer( "minimized" );
        final String script = "if (! __channels_flowmap_minimized__) "
                + " {__graph_bottom = \"90%\"; __part_top = \"10%\"; }"
                + " else {__graph_bottom = \"49.5%\"; __part_top = \"50.5%\";}"
                + " $(\"" + FLOWMAP_DOM_ID + "\").css(\"bottom\",__graph_bottom); "
                + " $(\"" + PART_DOM_ID + "\").css(\"top\",__part_top);"
                + " __channels_flowmap_minimized__ = !__channels_flowmap_minimized__;";
        shrinkExpand.add( new AttributeModifier( "onMouseUp", true, new Model<String>( script ) ) );
        shrinkExpand.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                minimized = !minimized;
                addFlowMapViewingControls();
                target.addComponent( getControlsContainer() );
            }
        } );
        getControlsContainer().add( shrinkExpand );
        // icon
        WebMarkupContainer icon = new WebMarkupContainer( "split_icon" );
        icon.add( new AttributeModifier(
                "src",
                true,
                new Model<String>( minimized
                        ? "images/split_on.png"
                        : "images/split.png" ) ) );
        icon.add( new AttributeModifier(
                "title",
                true,
                new Model<String>( minimized
                        ? "Shrink back forms"
                        : "Stretch up forms" ) ) );
        shrinkExpand.add( icon );
    }


    public void resizeSocialPanel( AjaxRequestTarget target, Change change ) {
        if ( change.isUnknown()
                || change.isCommunicated()
                || change.getId() == Channels.SOCIAL_ID && change.isDisplay() ) {
            String segmentPanelWidth;
            String socialPanelWidth;
            if ( getExpansions().contains( Channels.SOCIAL_ID ) ) {
                segmentPanelWidth = "80%";
                socialPanelWidth = "20%";
            } else {
                segmentPanelWidth = "100%";
                socialPanelWidth = "0%";
            }
            final String script = "$(\"" + SEGMENT_PANEL_ID + "\")"
                    + ".css(\"width\",\"" + segmentPanelWidth + "\");"
                    + "$(\"" + SOCIAL_PANEL_ID + "\")"
                    + ".css(\"width\",\"" + socialPanelWidth + "\");";
            target.prependJavascript( script );
            target.addComponent( flowMapDiagramPanel );
        }
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

    private void adjustPartPanelSizes( AjaxRequestTarget target, String[] sizes ) {
        String pl = sizes[0];
        String pr = sizes[1];
        String rl = sizes[2];
        String rr = sizes[3];
        String sl = sizes[4];
        String sr = sizes[5];
        final String script = "$(\"" + PART_PANEL_ID + "\")"
                + ".css(\"left\",\"" + pl + "\")"
                + ".css(\"right\",\"" + pr + "\");"
                + "$(\"" + RECEIVE_PANEL_ID + "\")"
                + ".css(\"left\",\"" + rl + "\")"
                + ".css(\"right\",\"" + rr + "\");"
                + "$(\"" + SEND_PANEL_ID + "\")"
                + ".css(\"left\",\"" + sl + "\")"
                + ".css(\"right\",\"" + sr + "\");";
        target.prependJavascript( script );
    }

    private boolean isPartExpanded() {
        return getExpansions().contains( getPart().getId() );
    }

    private boolean isReceiveExpanded() {
        return CollectionUtils.exists(
                IteratorUtils.toList( getPart().receives() ),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return getExpansions().contains( ( (Flow) object ).getId() );
                    }
                }
        );
    }

    private boolean isSendExpanded() {
        return CollectionUtils.exists(
                IteratorUtils.toList( getPart().sends() ),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return getExpansions().contains( ( (Flow) object ).getId() );
                    }
                }
        );
    }

    private void adjustComponents() {
        // do nothing
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
        addPartShowMenu();
        target.addComponent( partShowMenu );
        target.addComponent( partActionsMenu );
        receivesFlowPanel.refreshMenus( target );
        sendsFlowPanel.refreshMenus( target );
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
            setHidingNoop( property.contains( "hideNoop" ) );
        }
        addFlowDiagram();
        target.addComponent( flowMapDiagramPanel );
        addFlowMapViewingControls();
        target.addComponent( getControlsContainer() );
    }

    /**
     * Update social panel.
     *
     * @param target an ajax request target
     */
    public void updateSocialPanel( AjaxRequestTarget target ) {
        socialPanel.refresh( target, new Change( Change.Type.Unknown ) );
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
     * Refresh social panel.
     *
     * @param target an ajax request target
     * @param change a change referencing what the communication is about
     */
    public void refreshSocialPanel( AjaxRequestTarget target, Change change ) {
        resizeSocialPanel( target, change );
        updateSocialPanel( target );
    }
}
