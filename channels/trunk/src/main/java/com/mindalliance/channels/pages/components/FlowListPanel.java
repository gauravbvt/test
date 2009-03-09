package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Service;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.command.commands.BreakUpFlow;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.pages.ScenarioPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.CompoundPropertyModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A list of flows from a node, either requirements or outcomes.
 */
public class FlowListPanel extends Panel {

    /**
     * The node for which flows are listed.
     */
    private Node node;

    /**
     * True if outcomes are listed; false if requirements are listed.
     */
    private boolean outcomes;

    /**
     * Panels of expanded flows.
     */
    private List<DeletableFlow> deletableFlows;

    public FlowListPanel( String id, Node node, boolean outcomes ) {
        super( id );
        setNode( node );
        setOutcomes( outcomes );
        setDefaultModel( new CompoundPropertyModel( this ) );

        add( new Label( "title" ) );                                                      // NON-NLS

        add( new Link( "new" ) {                                                          // NON-NLS

            @Override
            public void onClick() {
                final Node n = getNode();
                final Flow f = isOutcomes() ? n.createOutcome( getService() )
                        : n.createRequirement( getService() );
                final Scenario s = n.getScenario();
                final Set<Long> newExpansions = new HashSet<Long>( ( (ScenarioPage) getPage() ).findExpansions() );
                newExpansions.add( f.getId() );
                newExpansions.remove( f.getScenario().getId() ); // TODO - Denis : FIX PROBLEM AND REMOVE PATCH
                setResponsePage( ScenarioPage.class,
                        ScenarioPage.getParameters( s, n, newExpansions ) );
            }
        } );

        // add( createFlowPanels( node, outcomes ) );
    }

    protected void onBeforeRender() {
        super.onBeforeRender();
        add( createFlowPanels( node, outcomes ) );
    }

    private RepeatingView createFlowPanels( Node node, boolean outcomes ) {

        final RepeatingView flowList = new RepeatingView( "flows" );                      // NON-NLS
        final Iterator<Flow> flows = outcomes ? node.outcomes() : node.requirements();
        deletableFlows = new ArrayList<DeletableFlow>();
        while ( flows.hasNext() ) {
            final Flow flow = flows.next();
            final long flowId = flow.getId();
            final Panel panel;
            Set<Long> expansions = ( (ScenarioPage) getPage() ).findExpansions();
            if ( expansions.contains( flowId ) ) {
                final ExpandedFlowPanel flowPanel = outcomes ?
                        new ExpandedOutPanel( Long.toString( flowId ), flow )
                        : new ExpandedReqPanel( Long.toString( flowId ), flow );
                panel = flowPanel;
                deletableFlows.add( flowPanel );
            } else {
                final CollapsedFlowPanel dp =
                        new CollapsedFlowPanel( Long.toString( flowId ), flow, outcomes );
                panel = dp;
                deletableFlows.add( dp );
            }
            flowList.add( panel );
        }
        return flowList;
    }

    private Service getService() {
        return ( (Project) getApplication() ).getService();
    }

    /**
     * @return the title of this panel.
     */
    public String getTitle() {
        return isOutcomes() ? "Send" : "Receive";
    }

    public final Node getNode() {
        return node;
    }

    public final void setNode( Node node ) {
        this.node = node;
    }

    public final boolean isOutcomes() {
        return outcomes;
    }

    public final void setOutcomes( boolean outcomes ) {
        this.outcomes = outcomes;
    }

    /**
     * Delete flows that are marked for deletion.
     *
     * @param expansions the component expansion list to modify on deletions
     */
    public void deleteSelectedFlows( Set<Long> expansions ) {
        for ( DeletableFlow p : deletableFlows )
            if ( p.isMarkedForDeletion() ) {
                final Flow flow = p.getFlow();
                expansions.remove( flow.getId() );
                try {
                    Project.commander().doCommand(  new BreakUpFlow( flow ) );
                } catch ( CommandException e ) {
                    e.printStackTrace();
                }
            }
    }
}
