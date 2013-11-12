/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.commands.AddCapability;
import com.mindalliance.channels.core.command.commands.AddNeed;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.pages.PlanPage;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.segment.menus.FlowActionsMenuPanel;
import com.mindalliance.channels.pages.components.segment.menus.FlowShowMenuPanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A list of flows from a node, either receives or sends.
 */
public class FlowListPanel extends AbstractCommandablePanel {

    private ListView<Flow> flowPanelsListView;
    /**
     * Flows list container.
     */
    private WebMarkupContainer flowsDiv;

    /**
     * The node for which flows are listed.
     */
    private IModel<Part> model;

    /**
     * True if sends are listed; false if receives are listed.
     */
    private boolean sends;
    /**
     * The currently selected flow in the list.
     */
    private Flow selectedFlow;
    /**
     * Whether selected flow was updated.
     */
    private boolean selectedFlowUpdated;
    /**
     * Show menu for selected flow, if any.
     */
    private Component flowShowMenu;
    /**
     * Actions menu for expanded flow, if any.
     */
    private Component flowActionsMenu;
    /**
     * Title container.
     */
    private WebMarkupContainer titleContainer;

    //-------------------------------
    public FlowListPanel( String id, IModel<Part> model, boolean sends, Set<Long> expansions ) {
        super( id, model, expansions );
        this.model = model;
        this.sends = sends;
        init();
    }

    private void init() {
        setSends( sends );
        setDefaultModel( new CompoundPropertyModel( this ) );
        addTitle();
        addFlowsDiv();
    }

    private void addTitle() {
        titleContainer = new WebMarkupContainer( "title" );
        titleContainer.setOutputMarkupId( true );
        addOrReplace( titleContainer );
        titleContainer.add( new Label( "title" ) );
        addShowMenu();
        addActionsMenu();
        AjaxLink newLink = new AjaxLink( "new" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                Part n = (Part) getNode();
                Command command = isSends()
                        ? new AddCapability( getUser().getUsername(), n )
                        : new AddNeed( getUser().getUsername(), n );
                Change change = doCommand( command );
                update( target, change );
            }
        };
        newLink.setVisible( getPlan().isDevelopment() );
        addTipTitle( newLink, "Click to add a new " + ( isSends() ? "info capability" : "info need" ) );
        titleContainer.add( newLink );
        newLink.add( new Label( "addFlow", "Add +" ) );
        titleContainer.add( makeHelpIcon( "help", "info-sharing", isSends() ? "add-capability" : "add-need" ) );
    }

    private void addShowMenu() {
        if ( selectedFlow != null  && selectedFlow.isConnected() ) {
            flowShowMenu = new FlowShowMenuPanel(
                    "flowShowMenu",
                    new Model<Flow>( selectedFlow ),
                    isSends(),
                    !isExpanded( selectedFlow.getId() ) );
        } else {
            flowShowMenu = new Label( "flowShowMenu", "Show" );
        }
        makeVisible( flowShowMenu, selectedFlow != null );
        titleContainer.add( flowShowMenu );

    }

    private void addActionsMenu() {
        boolean visible = false;
        if ( selectedFlow != null && isExpanded( selectedFlow.getId() ) && selectedFlow.isConnected() ) {
            visible = true;
            flowActionsMenu = new FlowActionsMenuPanel( "flowActionsMenu",
                    new Model<Flow>( selectedFlow ),
                    isSends() );
        } else {
            flowActionsMenu = new Label( "flowActionsMenu", "Actions" );
        }
        makeVisible( flowActionsMenu, visible );
        titleContainer.add( flowActionsMenu );
    }

    private void addFlowsDiv() {
        flowsDiv = new WebMarkupContainer( "flows-div" );
        flowsDiv.setOutputMarkupId( true );
        addOrReplace( flowsDiv );
        flowPanelsListView = createFlowPanels( sends );
        flowsDiv.add( flowPanelsListView );
    }

    private ListView<Flow> createFlowPanels( final boolean areSends ) {
        return new ListView<Flow>( "flows", new PropertyModel<List<Flow>>( this, "flows" ) ) {
            @Override
            protected void populateItem( ListItem<Flow> item ) {
                Flow flow = item.getModelObject();
                AbstractFlowPanel flowPanel;
                if ( isExpanded( flow ) ) {
                    flowPanel = areSends ?
                            new ExpandedSendPanel(
                                    "flow",
                                    new Model<Flow>( flow ),
                                    getExpansions(),
                                    item.getIndex(),
                                    planPage() )
                            : new ExpandedReceivePanel(
                            "flow",
                            new Model<Flow>( flow ),
                            getExpansions(),
                            item.getIndex(),
                            planPage() );
                } else {
                    flowPanel = new CollapsedFlowPanel(
                            "flow",
                            new Model<Flow>( flow ),
                            areSends,
                            item.getIndex() );
                }
                flowPanel.add( new AttributeModifier(
                        "class",
                        new Model<String>( getCssClasses( item ) ) ) );
                item.add( flowPanel );
            }
        };
    }

    //-------------------------------
    String getCssClasses( ListItem<Flow> item ) {
        Flow flow = item.getModelObject();
        String evenOdd = ( item.getIndex() % 2 == 0 ? "even" : "odd" );
        String priority = getPriorityCssClass( flow );
        String selected = getSelectedCssClass( flow );
        return evenOdd + " " + priority + " " + selected;
    }

    private String getSelectedCssClass( Flow flow ) {
        return ( selectedFlow != null && flow.equals( selectedFlow ) )
                ? " selected"
                : "";
    }

    /**
     * Get CSS class for flow priority.
     *
     * @param flow the flow
     * @return a string
     */
    private String getPriorityCssClass( Flow flow ) {
        if ( flow.isSharing() ) {
            Level priority = getQueryService().computeSharingPriority( flow );
            return priority.getNegativeLabel().toLowerCase();
        } else
            return "none";
    }

    public final Node getNode() {
        return model.getObject();
    }

    /**
     * @return the title of this panel.
     */
    public String getTitle() {
        return isSends() ? "Sends" : "Receives";
    }

    public boolean isSelectedFlowUpdated() {
        return selectedFlowUpdated;
    }

    public void setSelectedFlowUpdated( boolean selectedFlowUpdated ) {
        this.selectedFlowUpdated = selectedFlowUpdated;
    }

    /**
     * Refresh list of flows.
     *
     * @param target an ajax request target
     */
    public void refresh( AjaxRequestTarget target ) {
        for ( Flow flow : getFlows() ) {
            if ( getExpansions().contains( flow.getId() ) ) {
                if ( selectedFlow != null && !selectedFlow.equals( flow ) ) {
                    selectedFlowUpdated = false;
                }
                selectedFlow = flow;
            }
        }
        if ( selectedFlow != null && !getFlows().contains( selectedFlow ) ) {
            selectedFlow = null;
            selectedFlowUpdated = false;
        }
        refreshMenus( target );
        target.appendJavaScript( PlanPage.IE7CompatibilityScript );
        target.add( flowsDiv );
    }

    /**
     * Refresh menus.
     *
     * @param target ajax request target
     */
    public void refreshMenus( AjaxRequestTarget target ) {
        addTitle();
        target.add( titleContainer );
    }

    public void changed( Change change ) {
        if ( change.isUpdated() ) {
            setSelectedFlowUpdated( true );
            if ( change.isForInstanceOf( Flow.class ) ) {
                selectedFlow = (Flow) change.getSubject( getCommunityService() );
            }
        } else {
            if ( change.isForInstanceOf( Flow.class ) ) {
                Flow flow = (Flow) change.getSubject( getCommunityService() );
                if ( flow != null ) { // the flow might have been deleted by another planner
                    if ( change.isSelected() ) {
                        if ( selectedFlow != null && flow.equals( selectedFlow ) ) {
                            change.setType( Change.Type.Expanded );
                            super.changed( change );
                        } else {
                            selectedFlow = flow;
                            Flow toCollapse = (Flow) CollectionUtils.find(
                                    getFlows(),
                                    new Predicate() {
                                        @Override
                                        public boolean evaluate( Object object ) {
                                            return getExpansions().contains( ( (Flow) object ).getId() );
                                        }
                                    }
                            );
                            if ( toCollapse != null ) {
                                Change collapse = new Change( Change.Type.Collapsed, toCollapse );
                                super.changed( collapse );
                            }
                        }
                    } else {
                        if ( change.isRemoved() ) {
                            selectedFlow = null;
                            setSelectedFlowUpdated( false );
                        } else if ( change.isAdded() ) {
                            selectedFlow = (Flow) change.getSubject( getCommunityService() );
                        }
                        super.changed( change );
                    }
                }
            } else {
                super.changed( change );
            }
        }
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        change.addQualifier( "updated", isSelectedFlowUpdated() );
        target.appendJavaScript( PlanPage.IE7CompatibilityScript );
        if ( change.isSelected() || change.isDisplay() || change.isAdded() || change.isUpdated() ) {
            refreshMenus( target );
            if ( !change.isUpdated() ) target.add( flowsDiv );
        }
        if ( !change.isSelected() )
            super.updateWith( target, change, updated );
        if ( change.isDisplay() ) {
            setSelectedFlowUpdated( false );
        }
    }

    //-------------------------------
    List<AbstractFlowPanel> getFlowPanels() {
        final List<AbstractFlowPanel> flowPanels = new ArrayList<AbstractFlowPanel>();
        Iterator<Component> listItems = flowPanelsListView.iterator();
        while ( listItems.hasNext() ) {
            ( (MarkupContainer) listItems.next() ).visitChildren(
                    Component.class,
                    new IVisitor<Component, Void>() {
                        @Override
                        public void component( Component component, final IVisit<Void> visit ) {
                            if ( component instanceof AbstractFlowPanel ) {
                                flowPanels.add( (AbstractFlowPanel) component );
                                visit.stop();
                            } else {
                                visit.dontGoDeeper();
                            }
                        }
                    } );
        }
        return flowPanels;
    }


    /**
     * Get flows sorted: sharing > not sharing, by priority, by title..
     *
     * @return a list of flows
     */
    public List<Flow> getFlows() {
        List<Flow> flows = new ArrayList<Flow>();
        Iterator<Flow> iterator = sends ? getNode().sends() : getNode().receives();
        while ( iterator.hasNext() ) flows.add( iterator.next() );
        Collections.sort( flows, new Comparator<Flow>() {
            @Override
            public int compare( Flow flow, Flow other ) {
//                if ( expansions.contains( flow.getId() ) ) return -1;
                if ( flow.isSharing() && !other.isSharing() ) return -1;
                if ( other.isSharing() && !flow.isSharing() ) return 1;
                int comparison = 0;
                if ( flow.isSharing() ) {
                    Level impact = getQueryService().computeSharingPriority( flow );
                    Level otherImpact = getQueryService().computeSharingPriority( other );
                    // reverse order
                    comparison = otherImpact.compareTo( impact );
                }
                if ( comparison == 0 ) {
                    String title = sends ? flow.getSendTitle() : flow.getReceiveTitle();
                    String otherTitle = sends ? other.getSendTitle() : other.getReceiveTitle();
                    comparison = Collator.getInstance().compare( title.toLowerCase(), otherTitle.toLowerCase() );
                }
                return comparison;
            }
        } );
        return flows;
    }

    public final boolean isSends() {
        return sends;
    }

    public final void setSends( boolean sends ) {
        this.sends = sends;
    }
}
