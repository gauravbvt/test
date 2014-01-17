package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.InternalFlow;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.GraphBuilder;
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
    private final boolean assumeAlternatesFail;

    private final QueryService queryService;

    /**
     * Plan segment object presumed to fail.
     */
    private final SegmentObject segmentObject;

    //-------------------------------
    public FailureImpactsGraphBuilder( SegmentObject segmentObject, boolean assumeAlternatesFail,
                                       QueryService queryService ) {
        this.segmentObject = segmentObject;
        this.assumeAlternatesFail = assumeAlternatesFail;
        this.queryService = queryService;
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
        List<Flow> essentialFlows = getEssentialFlows();

        if ( segmentObject instanceof Flow ) {
            Flow flow = (Flow) segmentObject;
            if ( flow.isImportant()
                    && ( assumeAlternatesFail
                    || ( queryService.getAlternates( flow ).isEmpty()
                    && flow.intermediatedSources().isEmpty() ) ) )
                essentialFlows.add( flow );
        } else
            graph.addVertex( (Part) segmentObject );

        for ( Flow flow : essentialFlows ) {
            Part source = (Part) flow.getSource();
            Part target = (Part) flow.getTarget();
            if ( !source.isProhibited() && !target.isProhibited() ) {
                graph.addVertex( source );
                graph.addVertex( target );
                graph.addEdge( source, target, flow );
            }
        }
    }

    private List<Flow> getEssentialFlows() {
        List<Flow> essentialFlows = new ArrayList<Flow>();
        if ( segmentObject instanceof Part )
            essentialFlows.addAll( queryService.findEssentialFlowsFrom(
                    (Part) segmentObject,
                    assumeAlternatesFail ) );

        if ( segmentObject instanceof Flow ) {
            Flow flow = (Flow) segmentObject;
            if ( queryService.isEssential( flow, assumeAlternatesFail ) )
                essentialFlows.addAll( queryService.findEssentialFlowsFrom(
                        (Part) flow.getTarget(),
                        assumeAlternatesFail ) );
        }
        return essentialFlows;
    }
}
