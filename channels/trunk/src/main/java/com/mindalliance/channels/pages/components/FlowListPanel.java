package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;
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

    /** The node for which flows are listed. */
    private Node node;

    /** True if outcomes are listed; false if requirements are listed. */
    private boolean outcomes;

    /** Panels of expanded flows. */
    private List<ExpandedFlowPanel> expandedFlows;

    public FlowListPanel( String id, Node node, boolean outcomes, final Set<Long> expansions ) {
        super( id );
        setNode( node );
        setOutcomes( outcomes );
        setDefaultModel( new CompoundPropertyModel( this ) );

        add( new Label( "title" ) );                                                      // NON-NLS

        add( new Link( "new" ) {                                                          // NON-NLS
            @Override
            public void onClick() {
                final Node n = getNode();
                final Flow f = isOutcomes() ? n.createOutcome()
                                            : n.createRequirement();
                final Scenario s = n.getScenario();
                final Set<Long> newExpansions = new HashSet<Long>( expansions );
                newExpansions.add( f.getId() );
                setResponsePage( ScenarioPage.class,
                                 ScenarioPage.getParameters( s, n, newExpansions ) );
            }
        } );

        final RepeatingView flowList = new RepeatingView( "flows" );                      // NON-NLS
        final Iterator<Flow> flows = outcomes ? node.outcomes() : node.requirements();
        expandedFlows = new ArrayList<ExpandedFlowPanel>();
        while ( flows.hasNext() ) {
            final Flow flow = flows.next();
            final long flowId = flow.getId();
            final Panel panel;
            if ( expansions.contains( flowId ) ) {
                final ExpandedFlowPanel flowPanel =
                        new ExpandedFlowPanel( Long.toString( flowId ), flow, outcomes );
                panel = flowPanel;
                expandedFlows.add( flowPanel );
            }
            else
                panel = new CollapsedFlowPanel( Long.toString( flowId ), flow, outcomes );
            flowList.add( panel );
        }

        add( flowList );
    }

    /**
     * @return the title of this panel.
     */
    public String getTitle() {
        return isOutcomes() ?  "Outcomes" : "Requirements" ;
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
     * @param expansions the component expansion list to modify on deletions
     */
    public void deleteSelectedFlows( Set<Long> expansions ) {
        for ( ExpandedFlowPanel p : expandedFlows )
            if ( p.isMarkedForDeletion() ) {
                final Flow flow = p.getFlow();
                expansions.remove( flow.getId() );
                flow.disconnect();
            }
    }
}
