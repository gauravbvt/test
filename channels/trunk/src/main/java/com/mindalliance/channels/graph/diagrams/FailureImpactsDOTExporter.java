package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Goal;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.graph.AbstractDOTExporter;
import com.mindalliance.channels.graph.AbstractMetaProvider;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.MetaProvider;
import org.apache.commons.lang.StringUtils;
import org.jgrapht.Graph;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Essential flow map exporter.
 * Exports a Graph in DOT format.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 19, 2010
 * Time: 11:30:31 AM
 */
public class FailureImpactsDOTExporter extends AbstractDOTExporter<Node, Flow> {

    private static final int MAX_LABEL_LINE_LENGTH = 20;

    /**
     * Stop vertex.
     */
    private static final String STOP = "stop";
    /**
     * Terminating, internal parts.
     */
    Set<Part> terminators = new HashSet<Part>();


    public FailureImpactsDOTExporter( MetaProvider<Node, Flow> metaProvider ) {
        super( metaProvider );
    }

    /**
     * {@inheritDoc}
     */
    protected void beforeExport( CommunityService communityService, Graph<Node, Flow> g ) {
        super.beforeExport( communityService, g );
        for ( Node node : g.vertexSet() ) {
            if ( node.isPart() ) {
                Part part = (Part) node;
                if ( part.getSegment().isTerminatedBy( part ) &&
                        !isOnlyTheSourceOfFailedFlow( part, g ) ) {
                    terminators.add( part );
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void exportVertices( CommunityService communityService, PrintWriter out, Graph<Node, Flow> g ) {
        AbstractMetaProvider<Node, Flow> metaProvider = (AbstractMetaProvider<Node, Flow>) getMetaProvider();
        Map<Segment, Set<Node>> segmentNodes = new HashMap<Segment, Set<Node>>();
        for ( Node node : g.vertexSet() ) {
            Set<Node> nodesInSegment = segmentNodes.get( node.getSegment() );
            if ( nodesInSegment == null ) {
                nodesInSegment = new HashSet<Node>();
                segmentNodes.put( node.getSegment(), nodesInSegment );
            }
            nodesInSegment.add( node );
        }
        for ( Segment segment : segmentNodes.keySet() ) {
            if ( !segment.equals( getSegment() ) ) {
                out.println( "subgraph cluster_"
                        + segment.getName().replaceAll( "[^a-zA-Z0-9_]", "_" )
                        + " {" );
                List<DOTAttribute> attributes = new DOTAttribute( "label",
                        "Segment: " + segment.getName() ).asList();
                if ( metaProvider.getDOTAttributeProvider() != null ) {
                    attributes.addAll(
                            metaProvider.getDOTAttributeProvider().getSubgraphAttributes( false ) );
                }
                if ( metaProvider.getURLProvider() != null ) {
                    String url = metaProvider.getURLProvider().
                            getGraphURL( segmentNodes.get( segment ).iterator().next() );
                    if ( url != null ) attributes.add( new DOTAttribute( "URL", url ) );
                }
                out.print( asGraphAttributes( attributes ) );
                out.println();
                printoutVertices( communityService, out, segmentNodes.get( segment ) );
                exportStop( out, metaProvider, segment );
                exportGoals( out, metaProvider, g, segment );
                out.println( "}" );
            } else {
                printoutVertices( communityService, out, segmentNodes.get( segment ) );
                exportStop( out, metaProvider, segment );
                exportGoals( out, metaProvider, g, segment );
            }
        }
    }

    private void exportGoals(
            PrintWriter out,
            AbstractMetaProvider<Node, Flow> metaProvider,
            Graph<Node, Flow> g,
            Segment segment ) {
        for ( Node node : g.vertexSet() ) {
            Part part = (Part) node;
            if ( !isOnlyTheSourceOfFailedFlow( part, g ) ) {
                if ( part.getSegment().equals( segment ) )
                    for ( Goal goal : part.getGoals() ) {
                        exportGoal( getGoalVertexId( part, goal ), goal, out, metaProvider );
                    }
            }
        }
        if ( getTerminatedSegments().contains( segment ) ) {
            for ( Goal goal : segment.getGoals() ) {
                if ( goal.isEndsWithSegment() ) {
                    exportGoal( getGoalVertexId( segment, goal ), goal, out, metaProvider );
                }
            }
        }
    }

    private void exportGoal(
            String riskVertexId,
            Goal goal,
            PrintWriter out,
            AbstractMetaProvider<Node, Flow> metaProvider ) {
        List<DOTAttribute> attributes = DOTAttribute.emptyList();
        attributes.add( new DOTAttribute( "fontcolor", AbstractMetaProvider.FONTCOLOR ) );
        attributes.add( new DOTAttribute( "fontsize", FlowMapMetaProvider.NODE_FONT_SIZE ) );
        attributes.add( new DOTAttribute( "fontname", FlowMapMetaProvider.NODE_FONT ) );
        attributes.add( new DOTAttribute( "labelloc", "b" ) );
        String label = ChannelsUtils.split( sanitize( goal.getFailureLabel( ) ), "|", 4, MAX_LABEL_LINE_LENGTH );
        attributes.add( new DOTAttribute( "label", label.replaceAll( "\\|", "\\\\n" ) ) );
        attributes.add( new DOTAttribute( "shape", "none" ) );
        attributes.add( new DOTAttribute( "tooltip", goal.getFullTitle() ) );
        String dirName;
        try {
            dirName = metaProvider.getImageDirectory().getFile().getAbsolutePath();
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to get image directory location", e );
        }
        attributes.add(
                new DOTAttribute(
                        "image",
                        dirName + "/" + getGoalIcon( goal, StringUtils.countMatches( label, "|" ) + 1 ) ) );
        out.print( getIndent() );
        out.print( riskVertexId );
        out.print( "[" );
        out.print( asElementAttributes( attributes ) );
        out.println( "];" );
    }

    private String getGoalIcon( Goal goal, int numLabelLines ) {
        String name;
        if ( goal.isRiskMitigation() ) {
            switch ( goal.getLevel() ) {
                case Low:
                    name = "risk_minor";
                    break;
                case Medium:
                    name = "risk_major";
                    break;
                case High:
                    name = "risk_severe";
                    break;
                case Highest:
                    name = "risk_extreme";
                    break;
                default:
                    throw new RuntimeException( "Unknown risk level" );
            }
        } else {
            switch ( goal.getLevel() ) {
                case Low:
                    name = "gain_low";
                    break;
                case Medium:
                    name = "gain_medium";
                    break;
                case High:
                    name = "gain_high";
                    break;
                case Highest:
                    name = "gain_highest";
                    break;
                default:
                    throw new RuntimeException( "Unknown gain level" );
            }
        }
        return name + Integer.toString( numLabelLines ) + ".png";
    }

    private String getGoalVertexId( Part part, Goal goal ) {
        return "goal" + +part.getGoals().indexOf( goal ) + "_" + part.getId();
    }

    private String getGoalVertexId( Segment segment, Goal goal ) {
        return "goal" + segment.getGoals().indexOf( goal ) + "_" + segment.getId();
    }


    private void exportStop(
            PrintWriter out,
            AbstractMetaProvider<Node, Flow> metaProvider,
            Segment segment ) {
        if ( getTerminatedSegments().contains( segment ) ) {
            List<DOTAttribute> attributes = DOTAttribute.emptyList();
            attributes.add( new DOTAttribute( "fontcolor", AbstractMetaProvider.FONTCOLOR ) );
            attributes.add( new DOTAttribute( "fontsize", FlowMapMetaProvider.NODE_FONT_SIZE ) );
            attributes.add( new DOTAttribute( "fontname", FlowMapMetaProvider.NODE_FONT ) );
            attributes.add( new DOTAttribute( "labelloc", "b" ) );
            String label = sanitize( segment.getPhaseEventTitle() + " ends" );
            attributes.add( new DOTAttribute( "label", label ) );
            attributes.add( new DOTAttribute( "shape", "none" ) );
            attributes.add( new DOTAttribute( "tooltip", label ) );
            String dirName;
            try {
                dirName = metaProvider.getImageDirectory().getFile().getAbsolutePath();
            } catch ( IOException e ) {
                throw new RuntimeException( "Unable to get image directory location", e );
            }
            attributes.add( new DOTAttribute( "image", dirName + "/" + "stop.png" ) );
            out.print( getIndent() );
            out.print( getSegmentStopId( segment ) );
            out.print( "[" );
            out.print( asElementAttributes( attributes ) );
            out.println( "];" );
        }
    }

    private String getSegmentStopId( Segment segment ) {
        return STOP + segment.getId();
    }


    protected void exportEdges(
            CommunityService communityService,
            PrintWriter out,
            Graph<Node, Flow> g ) throws InterruptedException {
        super.exportEdges( communityService, out, g );
        if ( !terminators.isEmpty() )
            exportTerminations( out, g );
        exportGoalEdges( out, g );
    }

    private void exportGoalEdges( PrintWriter out, Graph<Node, Flow> g ) {
        for ( Node node : g.vertexSet() ) {
            Part part = (Part) node;
            if ( !isOnlyTheSourceOfFailedFlow( part, g ) ) {
                for ( Goal goal : part.getGoals() ) {
                    exportGoalEdge( part, goal, out, g );
                }
            }
        }
        for ( Segment segment : getTerminatedSegments() ) {
            for ( Goal goal : segment.getGoals() ) {
                if ( goal.isEndsWithSegment() ) {
                    exportGoalEdge( segment, goal, out, g );
                }
            }
        }
    }

    private void exportGoalEdge( Part part, Goal goal, PrintWriter out, Graph<Node, Flow> g ) {
        List<DOTAttribute> attributes = getNonFlowEdgeAttributes();
        attributes.add( new DOTAttribute( "label", "impact" ) );
        String goalId = getGoalVertexId( part, goal );
        String partId = getMetaProvider().getVertexIDProvider().getVertexName( part );
        out.print( getIndent() + partId + getArrow( g ) + goalId );
        out.print( "[" );
        if ( !attributes.isEmpty() ) {
            out.print( asElementAttributes( attributes ) );
        }
        out.println( "];" );
    }

    private void exportGoalEdge( Segment segment, Goal goal, PrintWriter out, Graph<Node, Flow> g ) {
        List<DOTAttribute> attributes = getNonFlowEdgeAttributes();
        attributes.add( new DOTAttribute( "label", "terminates" ) );
        String goalId = getGoalVertexId( segment, goal );
        out.print( getIndent() + getSegmentStopId( segment ) + getArrow( g ) + goalId );
        out.print( "[" );
        if ( !attributes.isEmpty() ) {
            out.print( asElementAttributes( attributes ) );
        }
        out.println( "];" );
    }

    private void exportTerminations( PrintWriter out, Graph<Node, Flow> g ) {
        for ( Part terminator : terminators ) {
            Segment segment = terminator.getSegment();
            List<DOTAttribute> attributes = getNonFlowEdgeAttributes();
            attributes.add( new DOTAttribute( "label", makeLabel( segment.terminationCause( terminator ) ) ) );
            String terminatorId = getVertexID( terminator );
            out.print( getIndent() + terminatorId + getArrow( g ) + getSegmentStopId( segment ) );
            out.print( "[" );
            if ( !attributes.isEmpty() ) {
                out.print( asElementAttributes( attributes ) );
            }
            out.println( "];" );
        }
    }

    private Set<Segment> getTerminatedSegments() {
        Set<Segment> segments = new HashSet<Segment>();
        for ( Part part : terminators ) {
            segments.add( part.getSegment() );
        }
        return segments;
    }

    private List<DOTAttribute> getNonFlowEdgeAttributes() {
        List<DOTAttribute> list = DOTAttribute.emptyList();
        list.add( new DOTAttribute( "color", "gray" ) );
        list.add( new DOTAttribute( "arrowhead", "none" ) );
        list.add( new DOTAttribute( "fontname", AbstractMetaProvider.EDGE_FONT ) );
        list.add( new DOTAttribute( "fontsize", AbstractMetaProvider.EDGE_FONT_SIZE ) );
        list.add( new DOTAttribute( "fontcolor", "dimgray" ) );
        list.add( new DOTAttribute( "len", "1.5" ) );
        list.add( new DOTAttribute( "weight", "2.0" ) );
        return list;
    }

    private SegmentObject getFailure() {
        return (SegmentObject) getMetaProvider().getContext();
    }

    private Segment getSegment() {
        return getFailure().getSegment();
    }

    private boolean isOnlyTheSourceOfFailedFlow( Part part, Graph<Node, Flow> g ) {
        SegmentObject failure = getFailure();
        return failure instanceof Flow
                && ( (Flow) failure ).getSource().equals( part )
                && g.edgesOf( part ).size() == 1;
    }

}
