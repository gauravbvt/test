package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.pages.PlanPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * PNG view of the flow graph.
 */
public class FlowMapPng extends DiagramPng {

    /**
     * {@inheritDoc}
     */
    protected Diagram makeDiagram( 
            double[] diagramSize, 
            String orientation,
            PageParameters parameters,
            CommunityService communityService,
            DiagramFactory diagramFactory ) throws DiagramException {
        PlanService planService = communityService.getPlanService();
        Segment segment = PlanPage.findSegment( planService, parameters );
        Node node = null;
        Flow flow = null;
        if ( segment == null ) {
            segment = planService.getDefaultSegment();
        } else {
            if ( parameters.getNamedKeys().contains( "node" )
                    && parameters.get( "node" ).toString().equals( "NONE" ) ) {
                node = null;
            } else {
                node = PlanPage.findPart( segment, parameters );
            }
            if ( parameters.getNamedKeys().contains( "flow" )
                    && parameters.get( "flow" ).toString().equals( "NONE" ) ) {
                flow = null;
            } else {
                flow = segment.getFlow( parameters.get( "flow" ).toLong() );
            }
        }
        boolean showingGoals = parameters.getNamedKeys().contains( "showingGoals" )
                && parameters.get( "showingGoals" ).toBoolean();
        boolean showingConnectors = parameters.getNamedKeys().contains( "showingConnectors" )
                && parameters.get( "showingConnectors" ).toBoolean();
        boolean hidingNoop = parameters.getNamedKeys().contains( "hidingNoop" )
                && parameters.get( "hidingNoop" ).toBoolean();
        boolean simplifying = parameters.getNamedKeys().contains( "simplifying" )
                && parameters.get( "simplifying" ).toBoolean();
        return diagramFactory.newFlowMapDiagram(
                segment,
                node,
                flow,
                diagramSize,
                orientation,
                showingGoals,
                showingConnectors,
                hidingNoop,
                simplifying );
    }

}
