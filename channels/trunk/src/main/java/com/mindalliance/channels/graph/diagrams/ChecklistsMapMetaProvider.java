/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.imaging.ImagingService;
import com.mindalliance.channels.graph.AbstractMetaProvider;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.DOTAttributeProvider;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.URLProvider;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.springframework.core.io.Resource;

import java.text.MessageFormat;
import java.util.List;

public class ChecklistsMapMetaProvider extends AbstractMetaProvider<Assignment, Commitment> {

    /**
     * Color for subgraph contour.
     */
    protected static final String SUBGRAPH_COLOR = "azure2";

    /**
     * Font for subgraph labels.
     */
    protected static final String SUBGRAPH_FONT = "Arial Bold Oblique";

    /**
     * Font size for subgraph labels.
     */
    protected static final String SUBGRAPH_FONT_SIZE = "10";

    /**
     * Font for node labels.
     */
    public static final String NODE_FONT = "Arial";

    /**
     * Font size for node labels.
     */
    public static final String NODE_FONT_SIZE = "10";

    /**
     * Distance for edge head and tail labels.
     */
    protected static final String LABEL_DISTANCE = "1.0";

    /**
     * Distance for edge head and tail labels.
     */
    protected static final String LABEL_ANGLE = "45";

    /**
     * Highlight pen width.
     */
    protected static final String HIGHLIGHT_PENWIDTH = "2.0";

    /**
     * Highlight pen color.
     */
    protected static final String HIGHLIGHT_COLOR = "gray";

    /**
     * Font of highlighted node.
     */
    protected static final String HIGHLIGHT_NODE_FONT = "Arial Bold";

    private Segment segment;

    // Extra arguments MUST start with '_'
    private static final String PROC_VERTEX_URL_FORMAT =
            "?graph={0,number,0}&vertex={1,number,0}&_actor={2,number,0}&_role={3,number,0}&_org={4,number,0}";

    // Extra arguments MUST start with '_'
    private static final String PROC_EDGE_URL_FORMAT =
            "?graph={0,number,0}&edge={1,number,0}&_actor={2,number,0}&_role={3,number,0}&_org={4,number,0}";

    public ChecklistsMapMetaProvider( Segment segment, String outputFormat, Resource imageDirectory, Analyst analyst,
                                      CommunityService communityService ) {
        super( outputFormat, imageDirectory, analyst, communityService );
        this.segment = segment;
    }

    @Override
    public Object getContext() {
        return segment;
    }

    @Override
    public URLProvider<Assignment, Commitment> getURLProvider() {
        return new URLProvider<Assignment, Commitment>() {
            /**
             * The URL for the graph that contains the vertex
             *
             * @param node -- a vertex
             * @return a URL string
             */
            @Override
            public String getGraphURL( Assignment node ) {
                // Plan id = 0 since there is only one plan
                Object[] args = { segment == null ? 0 : segment.getId() };
                return MessageFormat.format( GRAPH_URL_FORMAT, args );
            }

            /**
             * The vertex's URL. Returns null if none.
             *
             * @param assignment -- a vertex
             * @return a URL string
             */
            @Override
            public String getVertexURL( Assignment assignment ) {
                Part part = assignment.getPart();
                Actor actor = assignment.getActor();
                Role role = assignment.getRole();
                Organization organization = assignment.getOrganization();
                Object[] args = {
                        part.getSegment().getId(), part.getId(),
                        actor == null || actor.isUnknown() ? 0 : actor.getId(),
                        role == null || role.isUnknown() ? 0 : role.getId(),
                        organization == null || organization.isUnknown() ? 0 : organization.getId()
                };
                return MessageFormat.format( PROC_VERTEX_URL_FORMAT, args );
            }

            /**
             * The edges's URL. Returns null if none.
             *
             * @param commitment -- an edge
             * @return a URL string
             */
            @Override
            public String getEdgeURL( Commitment commitment ) {
                Flow flow = commitment.getSharing();
                Assignment assignment = commitment.getCommitter();
                Actor actor = assignment.getActor();
                Role role = assignment.getRole();
                Organization organization = assignment.getOrganization();
                Object[] args = {
                        flow.getSegment().getId(), flow.getId(),
                        ( actor == null || actor.isUnknown() ? 0 : actor.getId() ),
                        ( role == null || role.isUnknown() ? 0 : role.getId() ),
                        ( organization == null || organization.isUnknown() ? 0 : organization.getId() )
                };
                return MessageFormat.format( PROC_EDGE_URL_FORMAT, args );
            }
        };
    }

    @Override
    public DOTAttributeProvider<Assignment, Commitment> getDOTAttributeProvider() {
        return new ProceduresDOTAttributeProvider();
    }

    @Override
    public EdgeNameProvider<Commitment> getEdgeLabelProvider() {
        return new EdgeNameProvider<Commitment>() {
            @Override
            public String getEdgeName( Commitment commitment ) {
                Flow flow = commitment.getSharing();
                String flowName = flow.getName();
                if ( flow.isAskedFor() && !flowName.endsWith( "?" ) )
                    flowName += "?";
                if ( flow.isProhibited() )
                    flowName += " -PROHIBITED-";
                if ( !flow.getRestrictions().isEmpty() )
                    flowName += " (if " + flow.getRestrictionString( true ) + ')';
                String label = AbstractMetaProvider.separate( flowName, LINE_WRAP_SIZE ).replaceAll( "\\|", "\\\\n" );
                return sanitize( label );
            }
        };
    }

    @Override
    public VertexNameProvider<Assignment> getVertexLabelProvider() {
        return new VertexNameProvider<Assignment>() {
            @Override
            public String getVertexName( Assignment assignment ) {
                String label = assignment.getFullTitle( "|" ).replaceAll( "\\|", "\\\\n" );
                return sanitize( label );
            }
        };
    }

    @Override
    public VertexNameProvider<Assignment> getVertexIDProvider() {
        return new VertexNameProvider<Assignment>() {
            @Override
            public String getVertexName( Assignment assignment ) {
                return sanitizeToId( assignment.getFullTitle( "|" ) ) + String.valueOf( assignment.getPart().getId() );
            }
        };
    }

    /**
     * A DOTAttributeProvider for segments.
     */
    private class ProceduresDOTAttributeProvider implements DOTAttributeProvider<Assignment, Commitment> {

        private ProceduresDOTAttributeProvider() {
        }

        @Override
        public List<DOTAttribute> getGraphAttributes() {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "fontcolor", FONTCOLOR ) );
            list.add( new DOTAttribute( "rankdir", getGraphOrientation() ) );
            if ( getGraphSize() != null ) {
                list.add( new DOTAttribute( "size", getGraphSizeString() ) );
                list.add( new DOTAttribute( "ratio", "compress" ) );
            }
            return list;
        }

        /**
         * Gets semi-colon-separated style declarations for subgraphs.
         *
         * @return the style declarations
         */
        @Override
        public List<DOTAttribute> getSubgraphAttributes( boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "fontcolor", FONTCOLOR ) );
            list.add( new DOTAttribute( "fontsize", SUBGRAPH_FONT_SIZE ) );
            list.add( new DOTAttribute( "fontname", SUBGRAPH_FONT ) );
            list.add( new DOTAttribute( "color", SUBGRAPH_COLOR ) );
            list.add( new DOTAttribute( "style", "filled" ) );
            return list;
        }

        @Override
        public List<DOTAttribute> getVertexAttributes( CommunityService communityService, Assignment assignment,
                                                       boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            if ( getOutputFormat().equalsIgnoreCase( DiagramFactory.SVG ) ) {
                list.add( new DOTAttribute( "shape", "box" ) );
                // assuming a bitmap format
            } else {
                list.add( new DOTAttribute( "image",
                                            getIcon( communityService, ChecklistsMapMetaProvider.this.getAnalyst().getImagingService(),
                                                     assignment ) ) );
                list.add( new DOTAttribute( "labelloc", "b" ) );
                if ( highlighted ) {
                    list.add( new DOTAttribute( "shape", "box" ) );
                    list.add( new DOTAttribute( "style", "solid" ) );
                    list.add( new DOTAttribute( "color", HIGHLIGHT_COLOR ) );
                    list.add( new DOTAttribute( "penwidth", HIGHLIGHT_PENWIDTH ) );
                    list.add( new DOTAttribute( "fontname", HIGHLIGHT_NODE_FONT ) );
                } else {
                    list.add( new DOTAttribute( "shape", "none" ) );
                    list.add( new DOTAttribute( "fontname", NODE_FONT ) );
                }
            }
            list.add( new DOTAttribute( "fontcolor", FONTCOLOR ) );
            list.add( new DOTAttribute( "fontsize", NODE_FONT_SIZE ) );
            String tooltip = assignment.getPart().getTitle();
            list.add( new DOTAttribute( "tooltip", tooltip ) );
            return list;
        }

        @Override
        public List<DOTAttribute> getEdgeAttributes( CommunityService communityService, Commitment edge, boolean highlighted ) {
            Flow flow = edge.getSharing();
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "arrowsize", "0.75" ) );
            list.add( new DOTAttribute( "fontcolor", FONTCOLOR ) );
            if ( highlighted ) {
                list.add( new DOTAttribute( "fontname", EDGE_FONT_BOLD ) );
            } else {
                list.add( new DOTAttribute( "fontname", EDGE_FONT ) );
            }
            list.add( new DOTAttribute( "fontsize", EDGE_FONT_SIZE ) );
            list.add( new DOTAttribute( "fontcolor", "darkslategray" ) );
            list.add( new DOTAttribute( "len", "1.5" ) );
            list.add( new DOTAttribute( "weight", "2.0" ) );
            if ( flow.isAskedFor() ) {
                list.add( new DOTAttribute( "dir", "both" ) );
                list.add( new DOTAttribute( "arrowtail", "onormal" ) );
                list.add( new DOTAttribute( "style", flow.isCritical() ? "bold" : "solid" ) );
            } else {
                if ( flow.isCritical() ) {
                    list.add( new DOTAttribute( "style", "bold" ) );
                    list.add( new DOTAttribute( "style", "bold" ) );
                    list.add( new DOTAttribute( "fontcolor", "black" ) );
                }
            }
            // head and tail labels
            String headLabel = null;
            String tailLabel = null;
            if ( flow.isAll() ) {
                if ( flow.isTerminatingToTarget() )
                    headLabel = "(stop all)";
                else if ( flow.isTriggeringToTarget() )
                    headLabel = "(start all)";
                else {
                    headLabel = "(all)";
                }
            } else {
                if ( flow.isTerminatingToTarget() )
                    headLabel = "(stop)";
                else if ( flow.isTriggeringToTarget() )
                    headLabel = "(start)";
            }
            if ( flow.isTerminatingToSource() ) {
                tailLabel = "(stop)";
            } else if ( flow.isTriggeringToSource() ) {
                tailLabel = "(start)";
            }
            if ( headLabel != null )
                list.add( new DOTAttribute( "headlabel", headLabel ) );
            if ( tailLabel != null )
                list.add( new DOTAttribute( "taillabel", tailLabel ) );
            if ( headLabel != null || tailLabel != null ) {
                list.add( new DOTAttribute( "labeldistance", LABEL_DISTANCE ) );
                list.add( new DOTAttribute( "labelangle", LABEL_ANGLE ) );
            }
            list.add( new DOTAttribute( "tooltip", sanitize( flow.getTitle() ) ) );
            return list;
        }
    }

    protected String getIcon( CommunityService communityService, ImagingService imagingService, Assignment assignment ) {
        String iconName;
        String[] lines = assignment.getFullTitle( "|" ).split( "\\|" );
        int numLines = Math.min( lines.length, 5 );
        iconName = imagingService.findIconName( communityService, assignment );

        return iconName + ( numLines > 0 ? numLines : "" ) + ".png";
    }
}
