package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Node;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.CompoundPropertyModel;

import java.util.Iterator;
import java.util.Set;

/**
 * A list of flows from a node, either requirements or outcomes.
 */
public class FlowListPanel extends Panel {

    /** The node for which flows are listed. */
    private Node node;

    /** True if outcomes are listed; false if requirements are listed. */
    private boolean outcomes;

    public FlowListPanel( String id, Node node, boolean outcomes, Set<String> expansions ) {
        super( id );
        setNode( node );
        setOutcomes( outcomes );
        setDefaultModel( new CompoundPropertyModel( this ) );

        add( new Label( "title" ) );                                                      // NON-NLS

        add( new Link( "new" ) {                                                          // NON-NLS

            @Override
            public void onClick() {
            }
        } );

        final RepeatingView flowList = new RepeatingView( "flows" );                      // NON-NLS
        final Iterator<Flow> flows = outcomes ? node.outcomes() : node.requirements();
        while ( flows.hasNext() ) {
            final Flow flow = flows.next();
            final String flowId = Long.toString( flow.getId() );
            flowList.add( expansions.contains( flowId ) ?
                            new ExpandedFlowPanel( flowId, flow, outcomes )
                          : new CollapsedFlowPanel( flowId, flow, outcomes ) );
        }

        add( flowList );
    }

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
}
