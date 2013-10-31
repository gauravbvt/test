/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.EventTiming;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Goal;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.graph.AbstractDOTExporter;
import com.mindalliance.channels.graph.AbstractMetaProvider;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.MetaProvider;
import org.apache.commons.lang.StringUtils;
import org.jgrapht.Graph;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Flow map DOT exporter. Exports a Graph in DOT format.
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

    private static final int MAX_LABEL_LINE_LENGTH = 20;

    /**
     * Initiating, external parts.
     */
    private final Set<Part> initiators = new HashSet<Part>();

    /**
     * Terminating, internal parts.
     */
    private final Set<Part> terminators = new HashSet<Part>();

    /**
     * Parts that start with the segment.
     */
    private final Set<Part> autoStarters = new HashSet<Part>();

    /**
     * Parts that start events.
     */
    private final Set<Part> eventStarters = new HashSet<Part>();

    private final Map<EventTiming, Set<Part>> contextInitiators = new HashMap<EventTiming, Set<Part>>();

    public FlowMapDOTExporter( MetaProvider<Node, Flow> metaProvider ) {
        super( metaProvider );
    }

    @Override
    protected void beforeExport( CommunityService communityService, Graph<Node, Flow> g ) {
        super.beforeExport( communityService, g );
        Segment segment = getSegment();
        for ( Node node : g.vertexSet() ) {
            if ( node.isPart() ) {
                Part part = (Part) node;
                assert segment.getEvent() != null;
                if ( segment.isInitiatedBy( part ) )
                    initiators.add( part );
                else if ( segment.isTerminatedBy( part ) )
                    terminators.add( part );
                if ( part.getSegment().equals( segment ) && part.isAutoStarted() )
                    autoStarters.add( part );
                if ( part.getInitiatedEvent() != null && part.getSegment().equals( segment ) )
                    eventStarters.add( part );
            }
        }
        for ( EventTiming eventTiming : segment.getContext() ) {
            for ( Part part : communityService.getPlanService().findAllInitiators( eventTiming ) ) {
                Set<Part> parts = contextInitiators.get( eventTiming );
                if ( parts == null ) {
                    parts = new HashSet<Part>();
                    contextInitiators.put( eventTiming, parts );
                }
                parts.add( part );
            }
        }
    }

    @Override
    protected void exportVertices( CommunityService communityService, PrintWriter out, Graph<Node, Flow> g ) {
        FlowMapMetaProvider metaProvider = (FlowMapMetaProvider) getMetaProvider();
        if ( !( initiators.isEmpty() && autoStarters.isEmpty() ) )
            exportStart( out, metaProvider );
        Map<Segment, Set<Node>> segmentNodes = new HashMap<Segment, Set<Node>>();
        Set<Node> allNodes = new HashSet<Node>( g.vertexSet() );
        allNodes.addAll( bypassParts() );
        for ( Node node : allNodes ) {
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
                out.println( "subgraph cluster_" + segment.getName().replaceAll( "[^a-zA-Z0-9_]", "_" ) + " {" );
                List<DOTAttribute> attributes = new DOTAttribute( "label", "Segment: " + segment.getName() ).asList();
                if ( metaProvider.getDOTAttributeProvider() != null ) {
                    attributes.addAll( metaProvider.getDOTAttributeProvider().getSubgraphAttributes( false ) );
                }
                if ( metaProvider.getURLProvider() != null ) {
                    String url = metaProvider.getURLProvider().
                            getGraphURL( segmentNodes.get( segment ).iterator().next() );
                    if ( url != null )
                        attributes.add( new DOTAttribute( "URL", url ) );
                }
                out.print( asGraphAttributes( attributes ) );
                out.println();
                printoutVertices( communityService, out, segmentNodes.get( segment ) );
                if ( metaProvider.isShowingGoals() )
                    exportGoals( out, metaProvider, g, segment );
                out.println( "}" );
            } else {
                if ( metaProvider.isShowingGoals() )
                    exportGoals( out, metaProvider, g, segment );
                printoutVertices( communityService, out, segmentNodes.get( segment ) );
            }
        }
        if ( !terminators.isEmpty() )
            exportStop( out, metaProvider );
        if ( !eventStarters.isEmpty() )
            exportStartedEvents( out, metaProvider );
        if ( !contextInitiators.isEmpty() )
            exportContextInitiators( out, metaProvider );
    }

    private List<Part> bypassParts() {
        Set<Part> bypassParts = new HashSet<Part>();
        Segment segment = getSegment();
        for ( Flow sharing : segment.getAllSharingFlows() ) {
            bypassParts.addAll( sharing.intermediatedTargets() );
            bypassParts.addAll( sharing.intermediatedSources() );
        }
        return new ArrayList<Part>( bypassParts );
    }

    private void exportStart( PrintWriter out, AbstractMetaProvider<Node, Flow> metaProvider ) {
        List<DOTAttribute> attributes = DOTAttribute.emptyList();
        attributes.add( new DOTAttribute( "fontcolor", AbstractMetaProvider.FONTCOLOR ) );
        attributes.add( new DOTAttribute( "fontsize", AbstractFlowMetaProvider.NODE_FONT_SIZE ) );
        attributes.add( new DOTAttribute( "fontname", AbstractFlowMetaProvider.NODE_FONT ) );
        attributes.add( new DOTAttribute( "labelloc", "b" ) );
        Segment segment = getSegment();
        String name = sanitize( segment.getEventPhase().toString() );
        String label = ChannelsUtils.split( name, "|", 4, MAX_LABEL_LINE_LENGTH );
        attributes.add( new DOTAttribute( "label", label.replaceAll( "\\|", "\\\\n" ) ) );
        attributes.add( new DOTAttribute( "margin", "0.11,0.07" ) );
        attributes.add( new DOTAttribute( "shape", "none" ) );
        attributes.add( new DOTAttribute( "tooltip", name ) );
        String dirName;
        try {
            dirName = metaProvider.getImageDirectory().getFile().getAbsolutePath();
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to get image directory location", e );
        }
        attributes.add( new DOTAttribute( "image", getStartIcon( dirName, label ) ) );
        out.print( getIndent() );
        out.print( START );
        out.print( "[" );
        out.print( asElementAttributes( attributes ) );
        out.println( "];" );
    }

    private String getStartIcon( String dirName, String label ) {
        return dirName + "/" + "start" + ( StringUtils.countMatches( label, "|" ) + 1 ) + ".png";
    }

    private String getStopIcon( String dirName, String label ) {
        return dirName + "/" + "stop" + ( StringUtils.countMatches( label, "|" ) + 1 ) + ".png";
    }

    private void exportStartedEvents( PrintWriter out, FlowMapMetaProvider metaProvider ) {
        Set<Event> events = new HashSet<Event>();
        for ( Part part : eventStarters )
            events.add( part.getInitiatedEvent() );
        for ( Event event : events ) {
            List<DOTAttribute> attributes = DOTAttribute.emptyList();
            attributes.add( new DOTAttribute( "fontcolor", AbstractMetaProvider.FONTCOLOR ) );
            attributes.add( new DOTAttribute( "fontsize", AbstractFlowMetaProvider.NODE_FONT_SIZE ) );
            attributes.add( new DOTAttribute( "fontname", AbstractFlowMetaProvider.NODE_FONT ) );
            attributes.add( new DOTAttribute( "labelloc", "b" ) );
            String name = sanitize( event.getName() );
            String label = ChannelsUtils.split( name, "|", 4, MAX_LABEL_LINE_LENGTH );
            attributes.add( new DOTAttribute( "label", label.replaceAll( "\\|", "\\\\n" ) ) );
            attributes.add( new DOTAttribute( "label", label ) );
            attributes.add( new DOTAttribute( "shape", "none" ) );
            attributes.add( new DOTAttribute( "tooltip", name ) );
            String dirName;
            try {
                dirName = metaProvider.getImageDirectory().getFile().getAbsolutePath();
            } catch ( IOException e ) {
                throw new RuntimeException( "Unable to get image directory location", e );
            }
            attributes.add( new DOTAttribute( "image", getStartIcon( dirName, label ) ) );
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
            attributes.add( new DOTAttribute( "fontsize", AbstractFlowMetaProvider.NODE_FONT_SIZE ) );
            attributes.add( new DOTAttribute( "fontname", AbstractFlowMetaProvider.NODE_FONT ) );
            attributes.add( new DOTAttribute( "labelloc", "b" ) );
            String name = sanitize( eventTiming.getEvent().getName() );
            String label = ChannelsUtils.split( name, "|", 4, MAX_LABEL_LINE_LENGTH );
            attributes.add( new DOTAttribute( "label", label.replaceAll( "\\|", "\\\\n" ) ) );
            attributes.add( new DOTAttribute( "shape", "none" ) );
            attributes.add( new DOTAttribute( "tooltip", name ) );
            String dirName;
            try {
                dirName = metaProvider.getImageDirectory().getFile().getAbsolutePath();
            } catch ( IOException e ) {
                throw new RuntimeException( "Unable to get image directory location", e );
            }
            attributes.add( new DOTAttribute( "image",
                    dirName + "/" + ( eventTiming.isConcurrent() ?
                            getStartIcon( dirName, label ) :
                            getStopIcon( dirName, label ) ) ) );
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
        attributes.add( new DOTAttribute( "fontsize", AbstractFlowMetaProvider.NODE_FONT_SIZE ) );
        attributes.add( new DOTAttribute( "fontname", AbstractFlowMetaProvider.NODE_FONT ) );
        attributes.add( new DOTAttribute( "labelloc", "b" ) );
        Segment segment = getSegment();
        String name = segment.getEventPhase().toString();
        Phase phase = segment.getPhase();
        name += phase.isPreEvent() ? " succeeds" : " ends";
        String label = ChannelsUtils.split( sanitize( name ), "|", 4, MAX_LABEL_LINE_LENGTH );
        attributes.add( new DOTAttribute( "label", label.replaceAll( "\\|", "\\\\n" ) ) );
        attributes.add( new DOTAttribute( "shape", "none" ) );
        attributes.add( new DOTAttribute( "margin", "0.11,0.07" ) );
        attributes.add( new DOTAttribute( "tooltip", name ) );
        String dirName;
        try {
            dirName = metaProvider.getImageDirectory().getFile().getAbsolutePath();
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to get image directory location", e );
        }
        attributes.add( new DOTAttribute( "image", getStopIcon( dirName, label ) ) );
        out.print( getIndent() );
        out.print( STOP );
        out.print( "[" );
        out.print( asElementAttributes( attributes ) );
        out.println( "];" );
    }

    @Override
    protected void exportEdges(
            CommunityService communityService,
            PrintWriter out,
            Graph<Node, Flow> g ) throws InterruptedException {
        FlowMapMetaProvider metaProvider = (FlowMapMetaProvider) getMetaProvider();
        if ( !( initiators.isEmpty() && contextInitiators.isEmpty() ) ) exportInitiations( out, g );
        if ( !autoStarters.isEmpty() ) exportAutoStarts( out, g );
        super.exportEdges( communityService, out, g );
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
                attributes.add( new DOTAttribute( "label", eventTiming.isConcurrent() ? "causes" : "terminates" ) );
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
            attributes.add( new DOTAttribute(
                    "headlabel",
                    autoStarter.isOngoing() ? "(ongoing)" : "(starts)" ) );
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
            attributes.add( new DOTAttribute( "label", "causes" ) );
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
        List<String> drawn = new ArrayList<String>();
        for ( Flow sharing : segment.getAllSharingFlows() ) {
            List<DOTAttribute> attributes = getBypassEdgeAttributes( sharing );
            Part source = (Part) sharing.getSource();
            if ( isVisible( source ) ) {
                String starterId = getVertexID( source );
                for ( Part byPassedTo : sharing.intermediatedTargets() ) {
                    if ( isVisible( byPassedTo ) ) {
                        String enderId = getVertexID( byPassedTo );
                        String s = getIndent()
                                + starterId
                                + getArrow( g )
                                + enderId
                                + "["
                                + asElementAttributes( attributes )
                                + "]";
                        if ( !drawn.contains( s ) ) {
                            out.print( s );
                            drawn.add( s );
                        }
                    }
                }
            }
            Part target = (Part) sharing.getTarget();
            if ( isVisible( target ) ) {
                String enderId = getVertexID( target );
                for ( Part byPassedFrom : sharing.intermediatedSources() ) {
                    if ( isVisible( byPassedFrom ) ) {
                        String starterId = getVertexID( byPassedFrom );
                        String s = getIndent()
                                + starterId
                                + getArrow( g )
                                + enderId
                                + "["
                                + asElementAttributes( attributes )
                                + "]";
                        if ( !drawn.contains( s ) ) {
                            out.print( s );
                            drawn.add( s );
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
        list.add( new DOTAttribute( "penwidth", "1.0" ) );
        return list;
    }

    private List<DOTAttribute> getBypassEdgeAttributes( Flow flow ) {
        FlowMapMetaProvider flowMapMetaProvider = (FlowMapMetaProvider) getMetaProvider();
        List<DOTAttribute> list = DOTAttribute.emptyList();
        list.add( new DOTAttribute( "color", "gray" ) );
        list.add( new DOTAttribute( "arrowsize", "1.0" ) );
        list.add( new DOTAttribute( "arrowhead", "normal" ) );
        list.add( new DOTAttribute( "fontname", AbstractMetaProvider.EDGE_FONT ) );
        list.add( new DOTAttribute( "fontsize", AbstractMetaProvider.EDGE_FONT_SIZE ) );
        list.add( new DOTAttribute( "fontcolor", "dimgray" ) );
        list.add( new DOTAttribute( "len", "1.5" ) );
        list.add( new DOTAttribute( "weight", "2.0" ) );
        list.add( new DOTAttribute( "penwidth", "1.0" ) );
        if ( !isSimplified() ) {
            String label = flow.getName();
            list.add( new DOTAttribute( "label", label ) );
        }
        list.add( new DOTAttribute( "tooltip", "Intermediate is bypassed if unreachable" ) );
        flowMapMetaProvider.addTailArrowHead( flow, list );
        return list;
    }

    private boolean isSimplified() {
        return ( (AbstractFlowMetaProvider) getMetaProvider() ).isSimplified();
    }


    private boolean isVisible( Part part ) {
        FlowMapMetaProvider metaProvider = (FlowMapMetaProvider) getMetaProvider();
        return !metaProvider.isHidingNoop()
                || !metaProvider.getAnalyst().isEffectivelyConceptual( metaProvider.getQueryService(),
                part );
    }

    private String ifVisibleColor( Part part, String color ) {
        return part != null && !isVisible( part ) ? AbstractMetaProvider.INVISIBLE_COLOR : color;
    }

    private Segment getSegment() {
        return (Segment) getMetaProvider().getContext();
    }

    private void exportGoals( PrintWriter out, AbstractMetaProvider<Node, Flow> metaProvider, Graph<Node, Flow> g,
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

    private void exportGoal( String riskVertexId, Goal goal, Part part, PrintWriter out,
                             AbstractMetaProvider<Node, Flow> metaProvider ) {
        List<DOTAttribute> attributes = DOTAttribute.emptyList();
        attributes.add( new DOTAttribute( "fontcolor", ifVisibleColor( part, AbstractMetaProvider.FONTCOLOR ) ) );
        attributes.add( new DOTAttribute( "fontsize", AbstractFlowMetaProvider.NODE_FONT_SIZE ) );
        attributes.add( new DOTAttribute( "fontname", AbstractFlowMetaProvider.NODE_FONT ) );
        attributes.add( new DOTAttribute( "labelloc", "b" ) );
        attributes.add( new DOTAttribute( "margin", "0.11,0.07" ) );
        String label = ChannelsUtils.split( sanitize( goal.getSuccessLabel() ), "|", 4, MAX_LABEL_LINE_LENGTH );
        attributes.add( new DOTAttribute( "label", label.replaceAll( "\\|", "\\\\n" ) ) );
        attributes.add( new DOTAttribute( "shape", "none" ) );
        attributes.add( new DOTAttribute( "tooltip", goal.getFullTitle() ) );
        String dirName;
        try {
            dirName = metaProvider.getImageDirectory().getFile().getAbsolutePath();
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to get image directory location", e );
        }
        attributes.add( new DOTAttribute( "image", dirName + "/" + getGoalIcon( goal, part, StringUtils.countMatches( label, "|" ) + 1 ) ) );
        out.print( getIndent() );
        out.print( riskVertexId );
        out.print( "[" );
        out.print( asElementAttributes( attributes ) );
        out.println( "];" );
    }

    private String getGoalIcon( Goal goal, Part part, int numLabelLines ) {
        String name;
        if ( part != null && !isVisible( part ) ) {
            return "goal_blank.png";
        } else {
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
        list.add( new DOTAttribute( "penwidth", "1.0" ) );
        return list;
    }
}
