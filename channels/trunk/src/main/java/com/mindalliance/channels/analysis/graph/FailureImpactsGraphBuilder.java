package com.mindalliance.channels.analysis.graph;

import com.mindalliance.channels.analysis.GraphBuilder;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.InternalFlow;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.SegmentObject;
import com.mindalliance.channels.query.QueryService;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Critical flow map graph builder.
 */
public class FailureImpactsGraphBuilder implements GraphBuilder<Node, Flow> {

    /**
     * Whether all alternates to downstream sharing flows are presumed to also fail.
     */
    boolean assumeAlternatesFail;

    private QueryService queryService;

    /**
     * Plan segment object presumed to fail.
     */
    private SegmentObject segmentObject;

    //-------------------------------
    public FailureImpactsGraphBuilder( SegmentObject segmentObject, boolean assumeAlternatesFail ) {
        this.segmentObject = segmentObject;
        this.assumeAlternatesFail = assumeAlternatesFail;
    }

    //-------------------------------
    @Override
    public DirectedGraph<Node, Flow> buildDirectedGraph() {
        DirectedGraph<Node, Flow> digraph = new DirectedMultigraph<Node, Flow>( new EdgeFactory<Node, Flow>() {
            /**
             * Separate id generator for diagram-based flows.
             */
            private long IdCounter = 1L;

            @Override
            public Flow createEdge( Node sourceVertex, Node targetVertex ) {
                InternalFlow flow = new InternalFlow( sourceVertex, targetVertex, "" );
                flow.setId( IdCounter++ );
                return flow;
            }
        } );
        populateGraph( digraph );
        return digraph;
    }

    private void populateGraph( DirectedGraph<Node, Flow> graph ) {
        List<Flow> essentialFlows = getEssentialFlows( segmentObject );

        if ( segmentObject instanceof Flow ) {
            Flow flow = (Flow) segmentObject;
            if ( flow.isImportant() && ( assumeAlternatesFail || queryService.getAlternates( flow ).isEmpty() ) )
                essentialFlows.add( flow );
        } else
            graph.addVertex( (Part) segmentObject );

        for ( Flow flow : essentialFlows ) {
            Node source = flow.getSource();
            Node target = flow.getTarget();
            graph.addVertex( source );
            graph.addVertex( target );
            graph.addEdge( source, target, flow );
        }
    }

    private List<Flow> getEssentialFlows( SegmentObject segmentObject ) {
        if ( segmentObject instanceof Part )
            return queryService.findEssentialFlowsFrom( (Part) segmentObject, assumeAlternatesFail );

        if ( segmentObject instanceof Flow ) {
            Flow flow = (Flow) segmentObject;
            if ( queryService.isEssential( flow, assumeAlternatesFail ) )
                return getEssentialFlows( flow.getTarget() );
        }

        return new ArrayList<Flow>();
    }

    //-------------------------------
    public QueryService getQueryService() {
        return queryService;
    }

    public void setQueryService( QueryService queryService ) {
        this.queryService = queryService;
    }
}
