package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.commands.AddPart;
import com.mindalliance.channels.command.commands.UpdateSegmentObject;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.SegmentObject;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.diagrams.FlowMapDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.Settings;
import com.mindalliance.channels.pages.components.segment.menus.PartActionsMenuPanel;
import com.mindalliance.channels.pages.components.segment.menus.PartShowMenuPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.ComponentTag;
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
public class SegmentPanel extends AbstractCommandablePanel {
    /**
     * Flow map panel DOM id.
     */
    static private final String FLOWMAP_DOM_ID = "#graph";

    /**
     * Part panel DOM id.
     */
    static private final String PART_DOM_ID = "#part";
    /**
     * Expected screen resolution.
     */
    static private double DPI = 96.0;

    /**
     * Flow diagram panel.
     */
    private FlowMapDiagramPanel flowMapDiagramPanel;

    /**
     * Segment model.
     */
    private IModel<Segment> segmentModel;

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
     * Width, height dimension contraints on the flow diagram.
     * In inches.
     * None if any is 0.
     */
    private double[] flowDiagramDim = new double[2];
    /**
     * Whether the flow map was resized to fit.
     */
    private boolean resizedToFit = false;

    public SegmentPanel(
            String id,
            IModel<Segment> segmentModel,
            IModel<Part> partModel,
            Set<Long> expansions ) {
        super( id, segmentModel, expansions );
        this.segmentModel = segmentModel;
        this.partModel = partModel;
        init();
    }

    private void init() {
        setOutputMarkupId( true );
        addFlowViewControls();
        addFlowDiagram();
        addPartMenuBar();
        addPartPanel();
        receivesFlowPanel = new FlowListPanel(
                "receives",
                partModel,
                false,
                getExpansions() );
        add( receivesFlowPanel );
        sendsFlowPanel = new FlowListPanel(
                "sends",
                partModel,
                true,
                getExpansions() );
        add( sendsFlowPanel );
        adjustComponents();
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
        partShowMenu = new PartShowMenuPanel( "partShowMenu", partModel, getExpansions() );
        partShowMenu.setOutputMarkupId( true );
        add( partShowMenu );
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
        makeVisible( partActionsMenu, getPlan().isDevelopment() && isExpanded( getPart() ) );
        addOrReplace( partActionsMenu );
    }

    private void addFlowViewControls() {
        WebMarkupContainer reduceToFit = new WebMarkupContainer( "fit" );
        reduceToFit.add( new AbstractDefaultAjaxBehavior() {
            @Override
            protected void onComponentTag( ComponentTag tag ) {
                super.onComponentTag( tag );
                String domIdentifier = FLOWMAP_DOM_ID;
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
                if ( !resizedToFit ) {
                    flowDiagramDim[0] = ( Double.parseDouble( swidth ) - 20 ) / DPI;
                    flowDiagramDim[1] = ( Double.parseDouble( sheight ) - 20 ) / DPI;
                } else {
                    flowDiagramDim = new double[2];
                }
                resizedToFit = !resizedToFit;
                addFlowDiagram();
                target.addComponent( flowMapDiagramPanel );
            }
        } );
        add( reduceToFit );
        WebMarkupContainer fullscreen = new WebMarkupContainer( "maximized" );
        fullscreen.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Maximized, getSegment() ) );
            }
        } );
        add( fullscreen );

        WebMarkupContainer legend = new WebMarkupContainer( "legend" );
        legend.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Explained, getSegment(), "legend" ) );
            }
        } );
        add( legend );

        WebMarkupContainer shrinkExpand = new WebMarkupContainer( "minimized" );
        /*final String script =
        "if ( __channels_flowmap_minimized__==undefined) {__channels_flowmap_minimized__ = false;}"
        + " if (__channels_flowmap_minimized__) { alert(\"minimized\"); bottom = \"49.5%\"; top = \"50.5%\"; }"
        + " else { alert(\"NOT minimized\"); bottom = \"90%\"; top = \"10%\"; } "
        + "$(\"#graph\").css(\"bottom\",bottom); $(\"#part\").css(\"top\",top);"
        + "__channels_flowmap_minimized__ = !__channels_flowmap_minimized__;";*/
        final String script = "if (! __channels_flowmap_minimized__) "
                + " {__graph_bottom = \"90%\"; __part_top = \"10%\"; }"
                + " else {__graph_bottom = \"49.5%\"; __part_top = \"50.5%\";}"
                + " $(\"" + FLOWMAP_DOM_ID + "\").css(\"bottom\",__graph_bottom); "
                + " $(\"" + PART_DOM_ID + "\").css(\"top\",__part_top);"
                + " __channels_flowmap_minimized__ = !__channels_flowmap_minimized__;";
        shrinkExpand.add( new AttributeModifier( "onClick", true, new Model<String>( script ) ) );
        add( shrinkExpand );
    }

    private void addFlowDiagram() {
        double[] dim = flowDiagramDim[0] <= 0.0 || flowDiagramDim[1] <= 0.0 ? null : flowDiagramDim;
        Settings settings = new Settings( FLOWMAP_DOM_ID, null, dim, true, true );

        flowMapDiagramPanel =
                new FlowMapDiagramPanel( "flow-map", segmentModel, partModel, settings );
        flowMapDiagramPanel.setOutputMarkupId( true );
        addOrReplace( flowMapDiagramPanel );
    }


    public Part getPart() {
        return partModel.getObject();
    }

    public Segment getSegment() {
        return segmentModel.getObject();
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        boolean stopUpdates = false;
        if ( !change.isNone() ) {
            Identifiable identifiable = change.getSubject();
            if ( identifiable == getPart() ) {
                if ( change.isUpdated() ) {
                    receivesFlowPanel.refresh( target );
                    sendsFlowPanel.refresh( target );
                }
                if ( change.isExpanded() || change.isCollapsed() ) {
                    addPartPanel();
                    target.addComponent( partPanel );
                    stopUpdates = true;
                }
            }
            if ( identifiable instanceof Issue || identifiable instanceof SegmentObject ) {
                if ( !( change.isUpdated() && isExpanded( change.getSubject() ) ) && !( change.isDisplay() ) ) {
                    target.addComponent( flowMapDiagramPanel );
                }
            }
            if ( change.isExists() && change.getSubject() instanceof Issue ) {
                addPartPanel();
                target.addComponent( partPanel );
            }
            refreshMenus( target );
            if ( !stopUpdates ) super.updateWith( target, change, updated );
        }
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
        Identifiable identifiable = change.getSubject();
        if ( change.isModified()
                || ( change.isDisplay()
                && identifiable instanceof SegmentObject )
                || ( change.isSelected()
                && ( identifiable instanceof Segment || identifiable instanceof SegmentObject ) ) ) {
            if ( identifiable instanceof Issue
                    && change.isExists()
                    && ( (Issue) identifiable ).getAbout().getId() == getSegment().getId() ) {
                expandSegmentEditPanel( target );
            }
            adjustComponents();
            refreshMenus( target );
            addPartPanel();
            target.addComponent( partPanel );
            receivesFlowPanel.refresh( target );
            sendsFlowPanel.refresh( target );
            if ( change.isModified() || change.isSelected() ) {
                addFlowDiagram();
                target.addComponent( flowMapDiagramPanel );
            }
        }
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
        target.addComponent( partShowMenu );
        target.addComponent( partActionsMenu );
        receivesFlowPanel.refreshMenus( target );
        sendsFlowPanel.refreshMenus( target );
    }
}
