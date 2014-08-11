/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.EventPhase;
import com.mindalliance.channels.core.model.Goal;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Phase.Timing;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.graph.AbstractDOTExporter;
import com.mindalliance.channels.graph.AbstractMetaProvider;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.MetaProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
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
 * Procedures DOT exporter.
 */
public class ChecklistsMapDOTExporter extends AbstractDOTExporter<Assignment, Commitment> {

    /**
     * Start vertex.
     */
    private static final String START = "start";

    /**
     * Stop vertex.
     */
    private static final String STOP = "stop";

    /**
     * Event phase initiating assignments.
     */
    private Map<EventPhase, Set<Assignment>> initiators = new HashMap<EventPhase, Set<Assignment>>();

    /**
     * Event phase terminating assignments.
     */
    private Map<EventPhase, Set<Assignment>> terminators = new HashMap<EventPhase, Set<Assignment>>();

    /**
     * Assignments that start with an event phase.
     */
    private Map<EventPhase, Set<Assignment>> autoStarters = new HashMap<EventPhase, Set<Assignment>>();

    public ChecklistsMapDOTExporter( MetaProvider<Assignment, Commitment> metaProvider ) {
        super( metaProvider );
    }

    @Override
    protected void beforeExport( CommunityService communityService, Graph<Assignment, Commitment> g ) {
        super.beforeExport( communityService, g );
        for ( Assignment assignment : g.vertexSet() ) {
            Part part = assignment.getPart();
            if ( part.isTerminatesEventPhase() )
                put( terminators, part.getSegment().getEventPhase(), assignment );
            if ( part.isAutoStarted() )
                put( autoStarters, part.getSegment().getEventPhase(), assignment );
            if ( part.getInitiatedEvent() != null )
                for ( EventPhase eventPhase : findEventPhases( communityService.getModelService(),
                                                               part.getInitiatedEvent(),
                                                               Timing.Concurrent ) )
                    put( initiators, eventPhase, assignment );
        }
    }

    @SuppressWarnings( "unchecked" )
    private List<EventPhase> findEventPhases( QueryService queryService, Event event, final Timing timing ) {
        List<EventPhase> eventPhases = new ArrayList<EventPhase>();
        List<Phase> phases = (List<Phase>) CollectionUtils.select(
                queryService.listReferencedEntities( Phase.class ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Phase) object ).getTiming() == timing;
                    }
                } );
        for ( Phase phase : phases )
            eventPhases.add( new EventPhase( event, phase, null ) );
        return eventPhases;
    }

    private void put( Map<EventPhase, Set<Assignment>> map, EventPhase eventPhase, Assignment assignment ) {
        Set<Assignment> assignments = map.get( eventPhase );
        if ( assignments == null ) {
            assignments = new HashSet<Assignment>();
            map.put( eventPhase, assignments );
        }
        assignments.add( assignment );
    }

    private Set<EventPhase> getEventPhaseStarts( QueryService queryService ) {
        Set<EventPhase> startedEventPhases = new HashSet<EventPhase>();
        startedEventPhases.addAll( initiators.keySet() );
        startedEventPhases.addAll( autoStarters.keySet() );
        startedEventPhases.addAll( findAllStartedPostEventPhases( queryService ) );
        return startedEventPhases;
    }

    private Set<EventPhase> getEventPhaseStops() {
        return terminators.keySet();
    }

    private Set<EventPhase> findAllStartedPostEventPhases( QueryService queryService ) {
        Set<EventPhase> startedByTermination = new HashSet<EventPhase>();
        for ( EventPhase terminated : terminators.keySet() )
            if ( terminated.getPhase().isConcurrent() )
                startedByTermination.addAll( findEventPhases( queryService, terminated.getEvent(), Timing.PostEvent ) );
        return startedByTermination;
    }

    @Override
    protected void exportVertices( CommunityService communityService, PrintWriter out, Graph<Assignment, Commitment> g ) {
        ChecklistsMapMetaProvider metaProvider = (ChecklistsMapMetaProvider) getMetaProvider();
        if ( !getEventPhaseStarts( communityService.getModelService() ).isEmpty() )
            exportStarts( communityService, out, metaProvider );
        Map<Segment, Set<Assignment>> segmentAssignments = new HashMap<Segment, Set<Assignment>>();
        for ( Assignment assignment : g.vertexSet() ) {
            Segment segment = assignment.getPart().getSegment();
            Set<Assignment> assignmentsInSegment = segmentAssignments.get( segment );
            if ( assignmentsInSegment == null ) {
                assignmentsInSegment = new HashSet<Assignment>();
                segmentAssignments.put( segment, assignmentsInSegment );
            }
            assignmentsInSegment.add( assignment );
        }
        for ( Segment segment : segmentAssignments.keySet() ) {
            if ( isForEntirePlan() || segment.equals( getSegment() ) ) {
                exportGoals( segment, out, metaProvider, g );
                printoutVertices( communityService, out, segmentAssignments.get( segment ) );
            } else {
                out.println( "subgraph cluster_" + segment.getName().replaceAll( "[^a-zA-Z0-9_]", "_" ) + " {" );
                List<DOTAttribute> attributes = new DOTAttribute( "label", "Segment: " + segment.getName() ).asList();
                if ( metaProvider.getDOTAttributeProvider() != null ) {
                    attributes.addAll( metaProvider.getDOTAttributeProvider().getSubgraphAttributes( false ) );
                }
                if ( metaProvider.getURLProvider() != null ) {
                    String url = metaProvider.getURLProvider().
                            getGraphURL( segmentAssignments.get( segment ).iterator().next() );
                    if ( url != null )
                        attributes.add( new DOTAttribute( "URL", url ) );
                }
                out.print( asGraphAttributes( attributes ) );
                out.println();
                printoutVertices( communityService, out, segmentAssignments.get( segment ) );
                exportGoals( segment, out, metaProvider, g );
                out.println( "}" );
            }
        }
        if ( !getEventPhaseStops().isEmpty() )
            exportStops( out, metaProvider );
    }

    private void exportStarts( CommunityService communityService, PrintWriter out,
                               AbstractMetaProvider<Assignment, Commitment> metaProvider ) {
        for ( EventPhase eventPhase : getEventPhaseStarts( communityService.getModelService() ) ) {
            out.print( getIndent() );
            out.print( getStartId( eventPhase ) );
            out.print( "[" );
            out.print( asElementAttributes( getStartAttributes( eventPhase, metaProvider ) ) );
            out.println( "];" );
        }
    }

    private void exportStops( PrintWriter out, AbstractMetaProvider<Assignment, Commitment> metaProvider ) {
        for ( EventPhase eventPhase : getEventPhaseStops() ) {
            out.print( getIndent() );
            out.print( getStopId( eventPhase ) );
            out.print( "[" );
            out.print( asElementAttributes( getStopAttributes( eventPhase, metaProvider ) ) );
            out.println( "];" );
        }
    }

    private String getStartId( EventPhase eventPhase ) {
        return START + eventPhase.getPhase().getId() + "X" + eventPhase.getEvent().getId();
    }

    private String getStopId( EventPhase eventPhase ) {
        return STOP + eventPhase.getPhase().getId() + "X" + eventPhase.getEvent().getId();
    }

    private List<DOTAttribute> getStartAttributes( EventPhase eventPhase,
                                                   AbstractMetaProvider<Assignment, Commitment> metaProvider ) {
        List<DOTAttribute> attributes = DOTAttribute.emptyList();
        attributes.add( new DOTAttribute( "fontcolor", AbstractMetaProvider.FONTCOLOR ) );
        attributes.add( new DOTAttribute( "fontsize", ChecklistsMapMetaProvider.NODE_FONT_SIZE ) );
        attributes.add( new DOTAttribute( "fontname", ChecklistsMapMetaProvider.NODE_FONT ) );
        attributes.add( new DOTAttribute( "labelloc", "b" ) );
        String label = sanitize( eventPhase.toString() + " starts" );
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
        return attributes;
    }

    private List<DOTAttribute> getStopAttributes( EventPhase eventPhase,
                                                  AbstractMetaProvider<Assignment, Commitment> metaProvider ) {
        List<DOTAttribute> attributes = DOTAttribute.emptyList();
        attributes.add( new DOTAttribute( "fontcolor", AbstractMetaProvider.FONTCOLOR ) );
        attributes.add( new DOTAttribute( "fontsize", ChecklistsMapMetaProvider.NODE_FONT_SIZE ) );
        attributes.add( new DOTAttribute( "fontname", ChecklistsMapMetaProvider.NODE_FONT ) );
        attributes.add( new DOTAttribute( "labelloc", "b" ) );
        String label = sanitize( eventPhase.toString() + " stops" );
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
        return attributes;
    }

    @Override
    protected void exportEdges(
            CommunityService communityService,
            PrintWriter out,
            Graph<Assignment, Commitment> g ) throws InterruptedException {
        if ( !initiators.isEmpty() )
            exportInitiations( out, g );
        if ( !autoStarters.isEmpty() )
            exportAutoStarts( out, g );
        super.exportEdges( communityService, out, g );
        if ( !terminators.isEmpty() )
            exportTerminations( out, g );
        exportStopToStartEdges( communityService, out, g );
        exportGoalEdges( out, g );
    }

    private void exportInitiations( PrintWriter out, Graph<Assignment, Commitment> g ) {
        for ( EventPhase eventPhase : initiators.keySet() ) {
            for ( Assignment assignment : initiators.get( eventPhase ) ) {
                Part part = assignment.getPart();
                List<DOTAttribute> attributes = getTimingEdgeAttributes( part );
                String label = sanitize( "causes event \"" + eventPhase.getEvent().getName().toLowerCase() );
                attributes.add( new DOTAttribute( "label", label ) );
                String initiatorId = getVertexID( assignment );
                out.print( getIndent() + initiatorId + getArrow( g ) + getStartId( eventPhase ) );
                out.print( "[" );
                if ( !attributes.isEmpty() )
                    out.print( asElementAttributes( attributes ) );
                out.println( "];" );
            }
        }
    }

    private void exportAutoStarts( PrintWriter out, Graph<Assignment, Commitment> g ) {
        for ( EventPhase eventPhase : autoStarters.keySet() ) {
            for ( Assignment assignment : autoStarters.get( eventPhase ) ) {
                List<DOTAttribute> attributes = getTimingEdgeAttributes( assignment.getPart() );
                Part part = assignment.getPart();
                attributes.add( new DOTAttribute(
                        "headlabel",
                        part.isOngoing()
                                ? "(ongoing)"
                                : part.isRepeating()
                                ? "(recurs)"
                                : "(starts)" ) );
                String autoStarterId = getVertexID( assignment );
                out.print( getIndent() + getStartId( eventPhase ) + getArrow( g ) + autoStarterId );
                out.print( "[" );
                if ( !attributes.isEmpty() )
                    out.print( asElementAttributes( attributes ) );
                out.println( "];" );
            }
        }
    }

    private void exportTerminations( PrintWriter out, Graph<Assignment, Commitment> g ) {
        for ( EventPhase eventPhase : terminators.keySet() ) {
            for ( Assignment assignment : terminators.get( eventPhase ) ) {
                String label = sanitize( "terminates " + eventPhase.toString() );
                List<DOTAttribute> attributes = getTimingEdgeAttributes( assignment.getPart() );
                attributes.add( new DOTAttribute( "label", label ) );
                String terminatorId = getVertexID( assignment );
                out.print( getIndent() + terminatorId + getArrow( g ) + getStopId( eventPhase ) );
                out.print( "[" );
                if ( !attributes.isEmpty() )
                    out.print( asElementAttributes( attributes ) );
                out.println( "];" );
            }
        }
    }

    private void exportStopToStartEdges( CommunityService communityService, PrintWriter out, Graph<Assignment, Commitment> g ) {
        for ( EventPhase stopped : terminators.keySet() ) {
            if ( stopped.getPhase().isConcurrent() ) {
                for ( EventPhase started : findEventPhases( communityService.getModelService(), stopped.getEvent(), Timing.PostEvent ) ) {
                    String label = sanitize( "starts " + started.toString() );
                    List<DOTAttribute> attributes = getTimingEdgeAttributes();
                    attributes.add( new DOTAttribute( "label", label ) );
                    out.print( getIndent() + getStopId( stopped ) + getArrow( g ) + getStartId( started ) );
                    out.print( "[" );
                    if ( !attributes.isEmpty() )
                        out.print( asElementAttributes( attributes ) );
                    out.println( "];" );
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

    private List<DOTAttribute> getTimingEdgeAttributes() {
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

    private boolean isVisible( Part part ) {
        return true;
    }

    private String ifVisibleColor( Part part, String color ) {
        return part != null && !isVisible( part ) ? AbstractMetaProvider.INVISIBLE_COLOR : color;
    }

    private List<Segment> getSegments( CollaborationModel collaborationModel ) {
        List<Segment> segments = new ArrayList<Segment>();
        Segment segment = getSegment();
        if ( segment == null )
            segments.addAll( collaborationModel.getSegments() );
        else
            segments.add( segment );
        return segments;
    }

    private Segment getSegment() {
        return (Segment) getMetaProvider().getContext();
    }

    private void exportGoals( Segment segment, PrintWriter out,
                              AbstractMetaProvider<Assignment, Commitment> metaProvider,
                              Graph<Assignment, Commitment> g ) {
        for ( Assignment assignment : g.vertexSet() ) {
            Part part = assignment.getPart();
            if ( part.getSegment().equals( segment ) )
                for ( Goal goal : part.getGoals() )
                    exportGoal( getGoalVertexId( assignment, goal ), goal, assignment, out, metaProvider );
        }
        for ( EventPhase eventPhase : getEventPhaseStops() ) {
            if ( segment.getEventPhase().equals( eventPhase ) ) {
                for ( Goal goal : segment.getGoals() )
                    if ( goal.isEndsWithSegment() )
                        exportGoal( getGoalVertexId( segment, goal ), goal, null, out, metaProvider );
            }
        }
    }

    private boolean isForEntirePlan() {
        return getSegment() == null;
    }

    private void exportGoal( String goalVertexId, Goal goal, Assignment assignment, PrintWriter out,
                             AbstractMetaProvider<Assignment, Commitment> metaProvider ) {
        Part part = assignment == null ? null : assignment.getPart();
        List<DOTAttribute> attributes = DOTAttribute.emptyList();
        attributes.add( new DOTAttribute( "fontcolor", ifVisibleColor( part, AbstractMetaProvider.FONTCOLOR ) ) );
        attributes.add( new DOTAttribute( "fontsize", ChecklistsMapMetaProvider.NODE_FONT_SIZE ) );
        attributes.add( new DOTAttribute( "fontname", ChecklistsMapMetaProvider.NODE_FONT ) );
        attributes.add( new DOTAttribute( "labelloc", "b" ) );
        String label = sanitize( goal.getSuccessLabel( ) );
        attributes.add( new DOTAttribute( "label", label ) );
        attributes.add( new DOTAttribute( "shape", "none" ) );
        attributes.add( new DOTAttribute( "tooltip", goal.getFullTitle() ) );
        String dirName;
        try {
            dirName = metaProvider.getImageDirectory().getFile().getAbsolutePath();
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to get image directory location", e );
        }
        attributes.add( new DOTAttribute( "image", dirName + "/" + getGoalIcon( goal, part ) ) );
        out.print( getIndent() );
        out.print( goalVertexId );
        out.print( "[" );
        out.print( asElementAttributes( attributes ) );
        out.println( "];" );
    }

    private String getGoalIcon( Goal goal, Part part ) {
        if ( part != null && !isVisible( part ) )
            return "goal_blank.png";
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

    private String getGoalVertexId( Assignment assignment, Goal goal ) {
        Part part = assignment.getPart();
        return "GOAL" + part.getGoals().indexOf( goal ) + "X" + part.getId();
    }

    private String getGoalVertexId( Segment segment, Goal goal ) {
        return "GOAL" + segment.getGoals().indexOf( goal ) + "X" + segment.getId();
    }

    private void exportGoalEdges( PrintWriter out, Graph<Assignment, Commitment> g ) {
        for ( Assignment assignment : g.vertexSet() ) {
            Part part = assignment.getPart();
            for ( Goal goal : part.getGoals() ) {
                exportGoalEdge( assignment, goal, out, g );
            }
        }
        for ( EventPhase eventPhase : terminators.keySet() ) {
            for ( Assignment assignment : terminators.get( eventPhase ) ) {
                for ( Goal goal : assignment.getPart().getSegment().getGoals() ) {
                    if ( goal.isEndsWithSegment() ) {
                        exportStopGoalEdge( goal, assignment, assignment.getPart().getSegment(), out, g );
                    }
                }
            }
        }
    }

    private void exportGoalEdge( Assignment assignment, Goal goal, PrintWriter out, Graph<Assignment, Commitment> g ) {
        List<DOTAttribute> attributes = getNonFlowEdgeAttributes( assignment.getPart() );
        attributes.add( new DOTAttribute( "label", goal.isRiskMitigation() ? "mitigates" : "achieves" ) );
        String goalId = getGoalVertexId( assignment, goal );
        String assignmentId = getMetaProvider().getVertexIDProvider().getVertexName( assignment );
        out.print( getIndent() + assignmentId + getArrow( g ) + goalId );
        out.print( "[" );
        if ( !attributes.isEmpty() )
            out.print( asElementAttributes( attributes ) );
        out.println( "];" );
    }

    private void exportStopGoalEdge( Goal goal, Assignment assignment, Segment segment, PrintWriter out,
                                     Graph<Assignment, Commitment> g ) {
        List<DOTAttribute> attributes = getNonFlowEdgeAttributes( assignment.getPart() );
        attributes.add( new DOTAttribute( "label", "terminates" ) );
        String goalId = getGoalVertexId( segment, goal );
        out.print( getIndent() + getStopId( segment.getEventPhase() ) + getArrow( g ) + goalId );
        out.print( "[" );
        if ( !attributes.isEmpty() )
            out.print( asElementAttributes( attributes ) );
        out.println( "];" );
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


