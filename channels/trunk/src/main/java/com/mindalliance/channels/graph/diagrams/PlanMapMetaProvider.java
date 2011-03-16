package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.analysis.graph.SegmentRelationship;
import com.mindalliance.channels.graph.AbstractMetaProvider;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.DOTAttributeProvider;
import com.mindalliance.channels.graph.URLProvider;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Segment;
import org.apache.commons.lang.StringUtils;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.springframework.core.io.Resource;

import java.text.MessageFormat;
import java.util.List;

/**
 * Plan meta provider.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 1, 2009
 * Time: 8:02:40 PM
 */
public class PlanMapMetaProvider extends AbstractMetaProvider<Segment, SegmentRelationship> {
    /**
     * Color for subgraph contour
     */
    private static final String SEGMENT_COLOR = "azure2";
    /**
     * Color for subgraph contour
     */
    private static final String SELECTED_SEGMENT_COLOR = "azure4";
    /**
     * Font for subgraph labels.
     */
    private static final String SEGMENT_FONT = "Arial-Bold";
    /**
     * Font size for subgraph labels.
     */
    private static final String SEGMENT_FONT_SIZE = "10";

    /**
     * Color for subgraph contour
     */
    private static final String SUBGRAPH_COLOR = "gray94";
    /**
     * Color for selected subgraph contour
     */
    private static final String SELECTED_SUBGRAPH_COLOR = "gray85";
    /**
     * Font for subgraph labels.
     */
    private static final String SUBGRAPH_FONT = "Arial-Bold";
    /**
     * Font size for subgraph labels.
     */
    private static final String SUBGRAPH_FONT_SIZE = "10";
    /**
     * Whether to group by phase.
     */
    private boolean groupByPhase;
    /**
     * Whether to group by event.
     */
    private boolean groupByEvent;

    /**
     * The thing that generates URLs in an image map.
     */
    private URLProvider<Segment, SegmentRelationship> uRLProvider;

    /**
     * Segments in plan.
     */
    private List<Segment> segments;
    private ModelObject selectedGroup;

    public PlanMapMetaProvider(
            List<Segment> segments,
            String outputFormat,
            Resource imageDirectory,
            Analyst analyst ) {

        super( outputFormat, imageDirectory, analyst );
        this.segments = segments;
    }

    public Object getContext() {
        return segments;
    }

    public boolean isGroupByPhase() {
        return groupByPhase;
    }

    public void setGroupByPhase( boolean groupByPhase ) {
        this.groupByPhase = groupByPhase;
    }

    public boolean isGroupByEvent() {
        return groupByEvent;
    }

    public void setGroupByEvent( boolean groupByEvent ) {
        this.groupByEvent = groupByEvent;
    }

    public void setURLProvider( URLProvider<Segment, SegmentRelationship> uRLProvider ) {
        this.uRLProvider = uRLProvider;
    }

    /**
     * {@inheritDoc}
     */
    public URLProvider<Segment, SegmentRelationship> getURLProvider() {
        if ( uRLProvider == null )
            uRLProvider = new DefaultURLProvider();

        return uRLProvider;
    }

    public DOTAttributeProvider<Segment, SegmentRelationship> getDOTAttributeProvider() {
        return new PlanDOTAttributeProvider();
    }

    public EdgeNameProvider<SegmentRelationship> getEdgeLabelProvider() {
        return new EdgeNameProvider<SegmentRelationship>() {
            public String getEdgeName( SegmentRelationship segmentRel ) {
                String name = "";
                if ( segmentRel.hasExternalFlows() ) {
                    int count = segmentRel.getExternalFlows().size();
                    name = name + count + ( count > 1 ? " flows" : " flow" );
                }
                if ( segmentRel.hasInitiators() ) {
                    name += name.isEmpty() ? " " : " and ";
                    int count = segmentRel.getInitiators().size();
                    name = name + count + ( count > 1 ? " triggers" : " trigger" );
                }
                if ( segmentRel.hasTerminators() ) {
                    name += name.isEmpty() ? " " : " and ";
                    int count = segmentRel.getTerminators().size();
                    name = name + count + ( count > 1 ? " terminations" : " termination" );
                }
                return name;
            }
        };
    }

    public VertexNameProvider<Segment> getVertexLabelProvider() {
        return new VertexNameProvider<Segment>() {
            public String getVertexName( Segment segment ) {
                String label = AbstractMetaProvider.separate(
                        segmentLabel( segment ),
                        LINE_WRAP_SIZE ).replaceAll( "\\|", "\\\\n" );

                return sanitize( label );
            }
        };
    }

    private String segmentLabel( Segment segment ) {
        StringBuilder sb = new StringBuilder();
        sb.append( segment.getName() );
        sb.append( "|(" );
        if ( groupByPhase ) {
            sb.append( segment.getEvent().getName() );
        } else if ( groupByEvent ) {
            sb.append( segment.getPhase().getName() );
        } else {
            sb.append( segment.getPhaseEventTitle() );
        }
        sb.append( ')' );
        return sb.toString();
    }

    public VertexNameProvider<Segment> getVertexIDProvider() {
        return new VertexNameProvider<Segment>() {
            public String getVertexName( Segment segment ) {
                return String.valueOf( segment.getId() );
            }
        };
    }

    public void setSelectedGroup( ModelObject selectedGroup ) {
        this.selectedGroup = selectedGroup;
    }

    public ModelObject getSelectedGroup() {
        return selectedGroup;
    }

    /**
     * Plan DOT attribute provider.
     * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
     * Proprietary and Confidential.
     * User: jf
     * Date: Apr 1, 2009
     * Time: 8:37:04 PM
     */
    private class PlanDOTAttributeProvider implements DOTAttributeProvider<Segment, SegmentRelationship> {


        public List<DOTAttribute> getGraphAttributes() {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "rankdir", getGraphOrientation() ) );
            if ( getGraphSize() != null ) {
                list.add( new DOTAttribute( "size", getGraphSizeString() ) );
                list.add( new DOTAttribute( "ratio", "compress" ) );
            }
            return list;
        }

        public List<DOTAttribute> getSubgraphAttributes( boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "fontcolor", FONTCOLOR ) );
            list.add( new DOTAttribute( "fontsize", SUBGRAPH_FONT_SIZE ) );
            list.add( new DOTAttribute( "fontname", SUBGRAPH_FONT ) );
            if ( highlighted ) {
                list.add( new DOTAttribute( "color", SELECTED_SUBGRAPH_COLOR ) );
            } else {
                list.add( new DOTAttribute( "color", SUBGRAPH_COLOR ) );
            }
            list.add( new DOTAttribute( "style", "filled" ) );
            return list;
        }

        public List<DOTAttribute> getVertexAttributes( Segment vertex, boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "shape", "box" ) );
            list.add( new DOTAttribute( "color", SEGMENT_COLOR ) );
            list.add( new DOTAttribute( "style", "filled" ) );
            if ( highlighted ) {
                list.add( new DOTAttribute( "color", SELECTED_SEGMENT_COLOR ) );
            } else {
                list.add( new DOTAttribute( "color", SEGMENT_COLOR ) );
            }
            list.add( new DOTAttribute( "fontsize", SEGMENT_FONT_SIZE ) );
            list.add( new DOTAttribute( "fontname", SEGMENT_FONT ) );
            list.add( new DOTAttribute(
                    "tooltip",
                    sanitize( StringUtils.abbreviate( vertex.getDescription(), 50 ) ) ) );
            if ( !getPlan().isTemplate()
                    && getAnalyst().hasUnwaivedIssues( vertex, Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) {
                list.add( new DOTAttribute( "fontcolor", COLOR_ERROR ) );
                list.add( new DOTAttribute( "tooltip", sanitize( getAnalyst().getIssuesSummary( vertex,
                        Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) ) );
            }
            return list;
        }

        public List<DOTAttribute> getEdgeAttributes( SegmentRelationship edge, boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "arrowhead", "vee" ) );
            list.add( new DOTAttribute( "arrowsize", "0.75" ) );
            list.add( new DOTAttribute( "fontname", EDGE_FONT ) );
            list.add( new DOTAttribute( "fontsize", EDGE_FONT_SIZE ) );
            list.add( new DOTAttribute( "fontcolor", "darkslategray" ) );
            list.add( new DOTAttribute( "len", "1.5" ) );
            list.add( new DOTAttribute( "weight", "2.0" ) );
            if ( highlighted ) {
                list.add( new DOTAttribute( "penwidth", "3.0" ) );
            }
            // Issue coloring
            if ( !getPlan().isTemplate()
                    && edge.hasIssues( getAnalyst() ) ) {
                list.add( new DOTAttribute( "fontcolor", COLOR_ERROR ) );
                list.add( new DOTAttribute( "color", COLOR_ERROR ) );
                list.add( new DOTAttribute( "tooltip", sanitize( edge.getIssuesSummary( getAnalyst() ) ) ) );
            }
            return list;
        }
    }

    public class DefaultURLProvider implements URLProvider<Segment, SegmentRelationship> {

        public String getGraphURL( Segment vertex ) {
            if ( isGroupByPhase() ) {
                Object[] args = {vertex.getPhase().getId()};
                return MessageFormat.format( GRAPH_URL_FORMAT, args );
            } else if ( isGroupByEvent() ) {
                Object[] args = {vertex.getEvent().getId()};
                return MessageFormat.format( GRAPH_URL_FORMAT, args );
            } else {
                return null;
            }
        }

        public String getVertexURL( Segment vertex ) {
            return MessageFormat.format( VERTEX_URL_FORMAT, 0, vertex.getId() );
        }

        public String getEdgeURL( SegmentRelationship edge ) {
            return MessageFormat.format( EDGE_URL_FORMAT, 0, edge.getId() );
        }
    }
}
