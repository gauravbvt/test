package com.mindalliance.channels.pages.components;

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
import com.mindalliance.channels.pages.components.diagrams.FlowMapDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.Settings;
import com.mindalliance.channels.pages.components.menus.PartActionsMenuPanel;
import com.mindalliance.channels.pages.components.menus.PartShowMenuPanel;
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
    /**
     * Diagram container dom identifier.
     */
    private static final String DOM_IDENTIFIER = "#graph";

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
        addFlowSizing();
        addFlowDiagram();
        addPartMenuBar();
        addPartPanel();
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
        partActionsMenu.setVisible( getPlan().isDevelopment() && isExpanded( getPart() ) );
        addOrReplace( partActionsMenu );
    }

    private void addFlowSizing() {
        WebMarkupContainer reduceToFit = new WebMarkupContainer( "fit" );
        reduceToFit.add( new AbstractDefaultAjaxBehavior() {
            @Override
            protected void onComponentTag( ComponentTag tag ) {
                super.onComponentTag( tag );
                String domIdentifier = DOM_IDENTIFIER;
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
        WebMarkupContainer fullscreen = new WebMarkupContainer( "maximized" );
        fullscreen.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Maximized, getSegment() ) );
            }
        } );
        add( fullscreen );

    }

    private void addFlowDiagram() {
        double[] dim = flowDiagramDim[0] <= 0.0 || flowDiagramDim[1] <= 0.0 ? null : flowDiagramDim;
        Settings settings = new Settings( DOM_IDENTIFIER, null, dim, true, true );

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
        if ( !change.isNone() ) {
            Identifiable identifiable = change.getSubject();
            if ( identifiable == getPart() ) {
                if ( change.isUpdated() ) {
                    reqsFlowPanel.refresh( target );
                    outcomesFlowPanel.refresh( target );
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
        }
        refreshMenus( target );
        super.updateWith( target, change, updated );
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
            reqsFlowPanel.refresh( target );
            outcomesFlowPanel.refresh( target );
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
        reqsFlowPanel.refreshMenus( target );
        outcomesFlowPanel.refreshMenus( target );
    }
}
