package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.EventTiming;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Goal;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.engine.query.QueryService;
import com.mindalliance.channels.graph.AbstractDOTExporter;
import com.mindalliance.channels.graph.AbstractMetaProvider;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.MetaProvider;
import org.jgrapht.Graph;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Flow map DOT exporter.
 * Exports a Graph in DOT format.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 19, 2008
 * Time: 4:15:11 PM
 */
public class FlowMapDOTExporter extends AbstractDOTExporter<Node, Flow> {

    /**
     * Start vertex.
     */
    private static final String START = "__start__";
    /**
     * Stop vertex.
     */
    private static final String STOP = "__stop__";
    /**
     * Initiating, external parts.
     */
    Set<Part> initiators = new HashSet<Part>();
    /**
     * Terminating, internal parts.
     */
    Set<Part> terminators = new HashSet<Part>();
    /**
     * Parts that start with the segment.
     */
    Set<Part> autoStarters = new HashSet<Part>();
    /**
     * Parts that start events.
     */
    Set<Part> eventStarters = new HashSet<Part>();

    Map<EventTiming, Set<Part>> contextInitiators = new HashMap<EventTiming, Set<Part>>();

    public FlowMapDOTExporter( MetaProvider<Node, Flow> metaProvider ) {
        super( metaProvider );
    }

    /**
     * {@inheritDoc}
     */
    protected void beforeExport( Graph<Node, Flow> g ) {
        super.beforeExport( g );
        Segment segment = getSegment();
        for ( Node node : g.vertexSet() ) {
            if ( node.isPart() ) {
                Part part = (Part) node;
                assert segment.getEvent() != null;
                if ( segment.isInitiatedBy( part ) ) {
                    initiators.add( part );
                } else if ( segment.isTerminatedBy( part ) ) {
                    terminators.add( part );
                }
                if ( part.getSegment().equals( segment ) && part.isStartsWithSegment() ) {
                    autoStarters.add( part );
                }
                if ( part.getInitiatedEvent() != null && part.getSegment().equals( segment ) ) {
                    eventStarters.add( part );
                }
            }
        }
        QueryService queryService = ( (FlowMapMetaProvider) getMetaProvider() ).getAnalyst().getQueryService();
        for ( EventTiming eventTiming : segment.getContext() ) {
            for ( Part part : queryService.findAllInitiators( eventTiming ) ) {
                Set<Part> parts = contextInitiators.get( eventTiming );
                if ( parts == null ) {
                    parts = new HashSet<Part>();
                    contextInitiators.put( eventTiming, parts );
                }
                parts.add( part );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void exportVertices( PrintWriter out, Graph<Node, Flow> g ) {
        FlowMapMetaProvider metaProvider = (FlowMapMetaProvider) getMetaProvider();
        if ( !( initiators.isEmpty() && autoStarters.isEmpty() ) ) exportStart( out, metaProvider );
        Map<Segment, Set<Node>> segmentNodes = new HashMap<Segment, Set<Node>>();
        for ( Node node : g.vertexSet() ) {
            Segment segment = node.getSegment();
            Set<Node> nodesInSegment = segmentNodes.get( segment );
            if ( nodesInSegment == null ) {
                nodesInSegment = new HashSet<Node>();
                segmentNodes.put( segment, nodesInSegment );
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
                printoutVertices( out, segmentNodes.get( segment ) );
                if ( metaProvider.isShowingGoals() ) exportGoals( out, metaProvider, g, segment );
                out.println( "}" );
            } else {
                if ( metaProvider.isShowingGoals() ) exportGoals( out, metaProvider, g, segment );
                printoutVertices( out, segmentNodes.get( segment ) );
            }
        }
        if ( !terminators.isEmpty() ) exportStop( out, metaProvider );
        if ( !eventStarters.isEmpty() ) exportStartedEvents( out, metaProvider );
        if ( !contextInitiators.isEmpty() ) exportContextInitiators( out, metaProvider );
    }


    private void exportStart( PrintWriter out, AbstractMetaProvider<Node, Flow> metaProvider ) {
        List<DOTAttribute> attributes = DOTAttribute.emptyList();
        attributes.add( new DOTAttribute( "fontcolor", AbstractMetaProvider.FONTCOLOR ) );
        attributes.add( new DOTAttribute( "fontsize", FlowMapMetaProvider.NODE_FONT_SIZE ) );
        attributes.add( new DOTAttribute( "fontname", FlowMapMetaProvider.NODE_FONT ) );
        attributes.add( new DOTAttribute( "labelloc", "b" ) );
        Segment segment = getSegment();
        String label = sanitize( segment.getEventPhase().toString() )/* + " starts"*/;
        attributes.add( new DOTAttribute( "label", label ) );
        attributes.add( new DOTAttribute( "shape", "none" ) );
        attributes.add( new DOTAttribute( "tooltip", label ) );
        String dirName;
        try {
            dirName = metaProvider.getImageDirectory().getFile().getAbsolutePath();
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to get image directory location", e );
        }
        attributes.add( new DOTAttribute( "image", dirName + "/" + "start.png" ) );
        out.print( getIndent() );
        out.print( START );
        out.print( "[" );
        out.print( asElementAttributes( attributes ) );
        out.println( "];" );
    }

    private void exportStartedEvents( PrintWriter out, FlowMapMetaProvider metaProvider ) {
        Set<Event> events = new HashSet<Event>();
        for ( Part part : eventStarters ) {
            events.add( part.getInitiatedEvent() );
        }
        for ( Event event : events ) {
            List<DOTAttribute> attributes = DOTAttribute.emptyList();
            attributes.add( new DOTAttribute( "fontcolor", AbstractMetaProvider.FONTCOLOR ) );
            attributes.add( new DOTAttribute( "fontsize", FlowMapMetaProvider.NODE_FONT_SIZE ) );
            attributes.add( new DOTAttribute( "fontname", FlowMapMetaProvider.NODE_FONT ) );
            attributes.add( new DOTAttribute( "labelloc", "b" ) );
            String label = sanitize( event.getName() );
            attributes.add( new DOTAttribute( "label", label ) );
            attributes.add( new DOTAttribute( "shape", "none" ) );
            attributes.add( new DOTAttribute( "tooltip", label ) );
            String dirName;
            try {
                dirName = metaProvider.getImageDirectory().getFile().getAbsolutePath();
            } catch ( IOException e ) {
                throw new RuntimeException( "Unable to get image directory location", e );
            }
            attributes.add( new DOTAttribute( "image", dirName + "/" + "start.png" ) );
            out.print( getIndent() );
            out.print( "" + event.getId() );
            out.print( "[" );
            out.print( asElementAttributes( attributes ) );
            out.println( "];" );
        }
    }

    private void exportContextInitiators( PrintWriter out, FlowMapMetaProvider metaProvider ) {
        for ( EventTiming eventTiming : contextInitiators.keySet() ) {
            List<DOTAttribute> attributes = DOTAttribute.emptyList();
            attributes.add( new DOTAttribute( "fontcolor", AbstractMetaProvider.FONTCOLOR ) );
            attributes.add( new DOTAttribute( "fontsize", FlowMapMetaProvider.NODE_FONT_SIZE ) );
            attributes.add( new DOTAttribute( "fontname", FlowMapMetaProvider.NODE_FONT ) );
            attributes.add( new DOTAttribute( "labelloc", "b" ) );
            String label = sanitize( eventTiming.getEvent().getName() );
            attributes.add( new DOTAttribute( "label", label ) );
            attributes.add( new DOTAttribute( "shape", "none" ) );
            attributes.add( new DOTAttribute( "tooltip", label ) );
            String dirName;
            try {
                dirName = metaProvider.getImageDirectory().getFile().getAbsolutePath();
            } catch ( IOException e ) {
                throw new RuntimeException( "Unable to get image directory location", e );
            }
            attributes.add( new DOTAttribute(
                    "image",
                    dirName
                            + "/"
                            + ( eventTiming.isConcurrent() ? "start.png" : "stop.png" ) ) );
            out.print( getIndent() );
            out.print( getEventTimingID( eventTiming ) );
            out.print( "[" );
            out.print( asElementAttributes( attributes ) );
            out.println( "];" );
        }
    }

    private String getEventTimingID( EventTiming eventTiming ) {
        return eventTiming.getTiming().name() + "_" + eventTiming.getEvent().getId();
    }


    private void exportStop( PrintWriter out, AbstractMetaProvider<Node, Flow> metaProvider ) {
        List<DOTAttribute> attributes = DOTAttribute.emptyList();
        attributes.add( new DOTAttribute( "fontcolor", AbstractMetaProvider.FONTCOLOR ) );
        attributes.add( new DOTAttribute( "fontsize", FlowMapMetaProvider.NODE_FONT_SIZE ) );
        attributes.add( new DOTAttribute( "fontname", FlowMapMetaProvider.NODE_FONT ) );
        attributes.add( new DOTAttribute( "labelloc", "b" ) );
        Segment segment = getSegment();
        String label = segment.getEventPhase().toString();
        Phase phase = segment.getPhase();
        label += phase.isPreEvent()
                ? " succeeds"
                : " ends";
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
        out.print( STOP );
        out.print( "[" );
        out.print( asElementAttributes( attributes ) );
        out.println( "];" );
    }


    protected void exportEdges( PrintWriter out, Graph<Node, Flow> g ) {
        FlowMapMetaProvider metaProvider = (FlowMapMetaProvider) getMetaProvider();
        if ( !( initiators.isEmpty() && contextInitiators.isEmpty() ) ) exportInitiations( out, g );
        if ( !autoStarters.isEmpty() ) exportAutoStarts( out, g );
        super.exportEdges( out, g );
        if ( !eventStarters.isEmpty() ) exportEventStarts( out, g );
        if ( !terminators.isEmpty() ) exportTerminations( out, g );
        if ( metaProvider.isShowingGoals() ) exportGoalEdges( out, g );
        exportBypasses( out, g );
    }

    private void exportInitiations( PrintWriter out, Graph<Node, Flow> g ) {
        Segment segment = getSegment();
        for ( Part initiator : initiators ) {
            List<DOTAttribute> attributes = getTimingEdgeAttributes( initiator );
            attributes.add( new DOTAttribute( "label", makeLabel( segment.initiationCause( initiator ) ) ) );
            /*attributes.add( new DOTAttribute(
                    "tooltip",
                    sanitize( segment.initiationCause( initiator ) ) ) );*/
            String initiatorId = getVertexID( initiator );
            out.print( getIndent() + initiatorId + getArrow( g ) + START );
            out.print( "[" );
            if ( !attributes.isEmpty() ) {
                out.print( asElementAttributes( attributes ) );
            }
            out.println( "];" );
        }
        for ( EventTiming eventTiming : contextInitiators.keySet() ) {
            for ( Part initiator : contextInitiators.get( eventTiming ) ) {
                List<DOTAttribute> attributes = getTimingEdgeAttributes( initiator );
                attributes.add( new DOTAttribute(
                        "label",
                        eventTiming.isConcurrent() ? "causes" : "terminates"
                ) );
                /*attributes.add( new DOTAttribute(
          "tooltip",
          sanitize( segment.initiationCause( initiator ) ) ) );*/
                String initiatorId = getVertexID( initiator );
                out.print( getIndent() + initiatorId + getArrow( g ) + getEventTimingID( eventTiming ) );
                out.print( "[" );
                if ( !attributes.isEmpty() ) {
                    out.print( asElementAttributes( attributes ) );
                }
                out.println( "];" );
            }
        }
    }

    private void exportAutoStarts( PrintWriter out, Graph<Node, Flow> g ) {
        for ( Part autoStarter : autoStarters ) {
            List<DOTAttribute> attributes = getTimingEdgeAttributes( autoStarter );
            attributes.add( new DOTAttribute( "headlabel", "(starts)" ) );
            String autoStarterId = getVertexID( autoStarter );
            out.print( getIndent() + START + getArrow( g ) + autoStarterId );
            out.print( "[" );
            if ( !attributes.isEmpty() ) {
                out.print( asElementAttributes( attributes ) );
            }
            out.println( "];" );
        }
    }

    private void exportTerminations( PrintWriter out, Graph<Node, Flow> g ) {
        Segment segment = getSegment();
        for ( Part terminator : terminators ) {
            List<DOTAttribute> attributes = getTimingEdgeAttributes( terminator );
            attributes.add( new DOTAttribute( "label", makeLabel( segment.terminationCause( terminator ) ) ) );
            /*attributes.add( new DOTAttribute(
                    "tooltip",
                    sanitize( segment.terminationCause( terminator ) ) ) );*/
            String terminatorId = getVertexID( terminator );
            out.print( getIndent() + terminatorId + getArrow( g ) + STOP );
            out.print( "[" );
            if ( !attributes.isEmpty() ) {
                out.print( asElementAttributes( attributes ) );
            }
            out.println( "];" );
        }
    }

    private void exportEventStarts( PrintWriter out, Graph<Node, Flow> g ) {
        for ( Part part : eventStarters ) {
            List<DOTAttribute> attributes = getTimingEdgeAttributes( part );
            attributes.add( new DOTAttribute( "headlabel", "(start)" ) );
            /*attributes.add( new DOTAttribute(
                    "tooltip",
                    sanitize( segment.terminationCause( terminator ) ) ) );*/
            String starterId = getVertexID( part );
            out.print( getIndent() + starterId + getArrow( g ) + part.getInitiatedEvent().getId() );
            out.print( "[" );
            if ( !attributes.isEmpty() ) {
                out.print( asElementAttributes( attributes ) );
            }
            out.println( "];" );
        }
    }

    private void exportBypasses( PrintWriter out, Graph<Node, Flow> g ) {
        Segment segment = getSegment();
        for ( Flow sharing : segment.getAllSharingFlows() ) {
            if ( sharing.isCanBypassIntermediate() ) {
                Part source = (Part) sharing.getSource();
                String starterId = getVertexID( source );
                if ( isVisible( source ) ) {
                    List<DOTAttribute> attributes = getBypassEdgeAttributes( sharing );
                    for ( Part byPassedTo : sharing.intermediatedParts() ) {
                        if ( isVisible( byPassedTo ) ) {
                            String enderId = getVertexID( byPassedTo );
                            out.print( getIndent() + starterId + getArrow( g ) + enderId );
                            out.print( "[" );
                            if ( !attributes.isEmpty() ) {
                                out.print( asElementAttributes( attributes ) );
                            }
                            out.println( "];" );
                        }
                    }
                }
            }
        }
    }


    private List<DOTAttribute> getTimingEdgeAttributes( Part part ) {
        List<DOTAttribute> list = DOTAttribute.emptyList();
        list.add( new DOTAttribute( "color", ifVisibleColor( part, "gray" ) ) );
        list.add( new DOTAttribute( "arrowhead", "none" ) );
        list.add( new DOTAttribute( "fontname", AbstractMetaProvider.EDGE_FONT ) );
        list.add( new DOTAttribute( "fontsize", AbstractMetaProvider.EDGE_FONT_SIZE ) );
        list.add( new DOTAttribute( "fontcolor", ifVisibleColor( part, "dimgray" ) ) );
        list.add( new DOTAttribute( "len", "1.5" ) );
        list.add( new DOTAttribute( "weight", "2.0" ) );
        return list;
    }

    private List<DOTAttribute> getBypassEdgeAttributes( Flow flow ) {
        List<DOTAttribute> list = DOTAttribute.emptyList();
        list.add( new DOTAttribute( "color",  "gray" ) );
        list.add( new DOTAttribute( "arrowhead", "normal" ) );
        list.add( new DOTAttribute( "fontname", AbstractMetaProvider.EDGE_FONT ) );
        list.add( new DOTAttribute( "fontsize", AbstractMetaProvider.EDGE_FONT_SIZE ) );
        list.add( new DOTAttribute( "fontcolor", "dimgray" ) );
        list.add( new DOTAttribute( "len", "1.5" ) );
        list.add( new DOTAttribute( "weight", "2.0" ) );
        String label = ((FlowMapMetaProvider)getMetaProvider()).getEdgeLabelProvider().getEdgeName( flow );
        list.add( new DOTAttribute( "label", label ) );
        list.add( new DOTAttribute( "tooltip", "Intermediate is bypassed if unreachable" ) );
        return list;
    }


    private boolean isVisible( Part part ) {
        FlowMapMetaProvider metaProvider = (FlowMapMetaProvider) getMetaProvider();
        return !metaProvider.isHidingNoop() || !metaProvider.getAnalyst().isEffectivelyConceptual( part );
    }

    private String ifVisibleColor( Part part, String color ) {
        return part != null && !isVisible( part )
                ? AbstractMetaProvider.INVISIBLE_COLOR : color;
    }


    private Segment getSegment() {
        return (Segment) getMetaProvider().getContext();
    }

    private void exportGoals(
            PrintWriter out,
            AbstractMetaProvider<Node, Flow> metaProvider,
            Graph<Node, Flow> g,
            Segment segment ) {
        for ( Node node : g.vertexSet() ) {
            if ( node.isPart() ) {
                Part part = (Part) node;
                if ( part.getSegment().equals( segment ) )
                    for ( Goal goal : part.getGoals() ) {
                        exportGoal( getGoalVertexId( part, goal ), goal, part, out, metaProvider );
                    }
            }
        }
        if ( getTerminatedSegments().contains( segment ) ) {
            for ( Goal goal : segment.getGoals() ) {
                if ( goal.isEndsWithSegment() ) {
                    exportGoal( getGoalVertexId( segment, goal ), goal, null, out, metaProvider );
                }
            }
        }
    }

    private void exportGoal(
            String riskVertexId,
            Goal goal,
            Part part,
            PrintWriter out,
            AbstractMetaProvider<Node, Flow> metaProvider ) {
        List<DOTAttribute> attributes = DOTAttribute.emptyList();
        attributes.add( new DOTAttribute( "fontcolor", ifVisibleColor( part, AbstractMetaProvider.FONTCOLOR ) ) );
        attributes.add( new DOTAttribute( "fontsize", FlowMapMetaProvider.NODE_FONT_SIZE ) );
        attributes.add( new DOTAttribute( "fontname", FlowMapMetaProvider.NODE_FONT ) );
        attributes.add( new DOTAttribute( "labelloc", "b" ) );
        String label = sanitize( goal.getSuccessLabel( "|" ).replaceAll( "\\|", "\\\\n" ) );
        attributes.add( new DOTAttribute( "label", label ) );
        attributes.add( new DOTAttribute( "shape", "none" ) );
        attributes.add( new DOTAttribute( "tooltip", goal.getDescription() ) );
        String dirName;
        try {
            dirName = metaProvider.getImageDirectory().getFile().getAbsolutePath();
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to get image directory location", e );
        }
        attributes.add( new DOTAttribute( "image", dirName + "/" + getGoalIcon( goal, part ) ) );
        out.print( getIndent() );
        out.print( riskVertexId );
        out.print( "[" );
        out.print( asElementAttributes( attributes ) );
        out.println( "];" );
    }

    private String getGoalIcon( Goal goal, Part part ) {
        if ( part != null && !isVisible( part ) ) return "goal_blank.png";
        if ( goal.isRiskMitigation() ) {
            switch ( goal.getLevel() ) {
                case Low:
                    return "risk_minor.png";
                case Medium:
                    return "risk_major.png";
                case High:
                    return "risk_severe.png";
                case Highest:
                    return "risk_extreme.png";
                default:
                    throw new RuntimeException( "Unknown risk level" );
            }
        } else {
            switch ( goal.getLevel() ) {
                case Low:
                    return "gain_low.png";
                case Medium:
                    return "gain_medium.png";
                case High:
                    return "gain_high.png";
                case Highest:
                    return "gain_highest.png";
                default:
                    throw new RuntimeException( "Unknown gain level" );
            }
        }
    }

    private String getGoalVertexId( Part part, Goal goal ) {
        return "goal" + +part.getGoals().indexOf( goal ) + "_" + part.getId();
    }

    private String getGoalVertexId( Segment segment, Goal goal ) {
        return "goal" + segment.getGoals().indexOf( goal ) + "_" + segment.getId();
    }

    private void exportGoalEdges( PrintWriter out, Graph<Node, Flow> g ) {
        for ( Node node : g.vertexSet() ) {
            if ( node.isPart() ) {
                Part part = (Part) node;
                for ( Goal goal : part.getGoals() ) {
                    exportGoalEdge( part, goal, out, g );
                }
            }
        }
        if ( !terminators.isEmpty() ) {
            for ( Goal goal : getSegment().getGoals() ) {
                if ( goal.isEndsWithSegment() ) {
                    exportStopGoalEdge( goal, null, out, g );
                }
            }
        }
    }

    private void exportGoalEdge( Part part, Goal goal, PrintWriter out, Graph<Node, Flow> g ) {
        List<DOTAttribute> attributes = getNonFlowEdgeAttributes( part );
        attributes.add( new DOTAttribute( "label", goal.isRiskMitigation() ? "mitigates" : "achieves" ) );
        String goalId = getGoalVertexId( part, goal );
        String partId = getMetaProvider().getVertexIDProvider().getVertexName( part );
        out.print( getIndent() + partId + getArrow( g ) + goalId );
        out.print( "[" );
        if ( !attributes.isEmpty() ) {
            out.print( asElementAttributes( attributes ) );
        }
        out.println( "];" );
    }

    private void exportStopGoalEdge( Goal goal, Part part, PrintWriter out, Graph<Node, Flow> g ) {
        List<DOTAttribute> attributes = getNonFlowEdgeAttributes( part );
        attributes.add( new DOTAttribute( "label", "terminates" ) );
        String goalId = getGoalVertexId( getSegment(), goal );
        out.print( getIndent() + STOP + getArrow( g ) + goalId );
        out.print( "[" );
        if ( !attributes.isEmpty() ) {
            out.print( asElementAttributes( attributes ) );
        }
        out.println( "];" );
    }

    private Set<Segment> getTerminatedSegments() {
        Set<Segment> segments = new HashSet<Segment>();
        for ( Part part : terminators ) {
            segments.add( part.getSegment() );
        }
        return segments;
    }

    private List<DOTAttribute> getNonFlowEdgeAttributes( Part part ) {
        List<DOTAttribute> list = DOTAttribute.emptyList();
        list.add( new DOTAttribute( "color", ifVisibleColor( part, "gray" ) ) );
        list.add( new DOTAttribute( "arrowhead", "none" ) );
        list.add( new DOTAttribute( "fontname", AbstractMetaProvider.EDGE_FONT ) );
        list.add( new DOTAttribute( "fontsize", AbstractMetaProvider.EDGE_FONT_SIZE ) );
        list.add( new DOTAttribute( "fontcolor", ifVisibleColor( part, "dimgray" ) ) );
        list.add( new DOTAttribute( "len", "1.5" ) );
        list.add( new DOTAttribute( "weight", "2.0" ) );
        return list;
    }


}
