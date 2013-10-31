/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Dissemination;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.core.model.Transformation;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.graph.AbstractMetaProvider;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.DOTAttributeProvider;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.URLProvider;
import org.jgrapht.ext.EdgeNameProvider;
import org.springframework.core.io.Resource;

import java.text.MessageFormat;
import java.util.List;

/**
 * Dissemination meta provider.
 */
public class DisseminationMetaProvider extends AbstractFlowMetaProvider<Node, Dissemination> {

    private static final int MAX_INFO_LENGTH = 20;

    public DisseminationMetaProvider( SegmentObject segmentObject, String outputFormat, Resource imageDirectory,
                                      Analyst analyst, QueryService queryService ) {
        super( (ModelObject) segmentObject, outputFormat, imageDirectory, analyst, false, false, false, false, queryService );
    }

    @Override
    public String getGraphOrientation() {
        return "LR";
    }

    @Override
    public URLProvider<Node, Dissemination> getURLProvider() {
        return new URLProvider<Node, Dissemination>() {
            /**
             * The URL for the graph that contains the vertex
             *
             * @param node -- a vertex
             * @return a URL string
             */
            @Override
            public String getGraphURL( Node node ) {
                Object[] args = {node.getSegment().getId()};
                return MessageFormat.format( GRAPH_URL_FORMAT, args );
            }

            /**
             * The vertex's URL. Returns null if none.
             *
             * @param node -- a vertex
             * @return a URL string
             */
            @Override
            public String getVertexURL( Node node ) {
                if ( node.isPart() ) {
                    Object[] args = {node.getSegment().getId(), node.getId()};
                    return MessageFormat.format( VERTEX_URL_FORMAT, args );
                } else {
                    return null;
                }
            }

            /**
             * The edges's URL. Returns null if none.
             *
             * @param edge -- an edge
             * @return a URL string
             */
            @Override
            public String getEdgeURL( Dissemination edge ) {
                // Plan id = 0 for now sice there is only one plan
                Object[] args = {0, edge.getFlow().getId()};
                return MessageFormat.format( EDGE_URL_FORMAT, args );
            }
        };
    }

    @Override
    public EdgeNameProvider<Dissemination> getEdgeLabelProvider() {
        return new EdgeNameProvider<Dissemination>() {
            @Override
            public String getEdgeName( Dissemination edge ) {
                Flow flow = edge.getFlow();
                StringBuilder sb = new StringBuilder();
                sb.append( edge.getSubject().getLabel( MAX_INFO_LENGTH ) );
                if ( edge.getFlow().isProhibited() ) {
                    sb.append( " -PROHIBITED-" );
                }
                if ( !flow.getRestrictions().isEmpty() ) {
                    sb.append( " (if " );
                    sb.append( flow.getRestrictionString( true ) );
                    sb.append( ")" );
                }
                Transformation.Type xformType = edge.getTransformationType();
                if ( !( edge.isRoot() || xformType == Transformation.Type.Identity ) ) {
                    sb.append( " [" );
                    sb.append( xformType.getLabel() );
                    sb.append( " " );
                    sb.append( edge.getTransformedSubject().getLabel( MAX_INFO_LENGTH ) );
                    sb.append( "]" );
                }
                String label = AbstractMetaProvider.separate(
                        sb.toString(),
                        LINE_WRAP_SIZE ).replaceAll( "\\|", "\\\\n" );

                return sanitize( label );
            }
        };
    }

    @Override
    public DOTAttributeProvider<Node, Dissemination> getDOTAttributeProvider() {
        return new SegmentDOTAttributeProvider();
    }

    /**
     * A DOTAttributeProvider for segments.
     */
    private class SegmentDOTAttributeProvider implements DOTAttributeProvider<Node, Dissemination> {

        public SegmentDOTAttributeProvider() {
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
            // list.add( new DOTAttribute( "overlap", "false" ) );
            // list.add( new DOTAttribute( "splines", "true" ) );
            // list.add( new DOTAttribute( "sep", ".1" ) );
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
        public List<DOTAttribute> getVertexAttributes( CommunityService communityService, Node vertex, boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            if ( getOutputFormat().equalsIgnoreCase( DiagramFactory.SVG ) ) {
                if ( vertex.isPart() ) {
                    list.add( new DOTAttribute( "shape", "box" ) );
                } else if ( vertex.isConnector() ) {
                    list.add( new DOTAttribute( "shape", "point" ) );
                    // segmentNode
                } else {
                    list.add( new DOTAttribute( "shape", "egg" ) );
                }
                // assuming a bitmap format
            } else {
                list.add( new DOTAttribute( "image", getIcon( communityService, DisseminationMetaProvider.this.getAnalyst().getImagingService(),
                        vertex ) ) );
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
            if ( getAnalyst().hasUnwaivedIssues( getQueryService(),
                                                       vertex, Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) {
                list.add( new DOTAttribute( "fontcolor", COLOR_ERROR ) );
                list.add( new DOTAttribute( "tooltip", sanitize( getAnalyst().getIssuesOverview( getQueryService(),
                        vertex,
                        Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) ) );
            } else {
                String tooltip = vertex.getTitle();
                if ( vertex.isPart() ) {
                    List<Actor> partActors = getQueryService().findAllActualActors(
                            ( (Part) vertex ).resourceSpec() );
                    if ( partActors.size() > 1 ) {
                        tooltip = sanitize( listActors( partActors ) );
                    }
                }
                list.add( new DOTAttribute( "tooltip", tooltip ) );
            }
            return list;
        }

        @Override
        public List<DOTAttribute> getEdgeAttributes( CommunityService communityService, Dissemination edge, boolean highlighted ) {
            Flow flow = edge.getFlow();
            boolean conceptual = getAnalyst().isEffectivelyConceptual( getQueryService(), flow );
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
            if ( flow.isIfTaskFails() ) {
                list.add( new DOTAttribute( "arrowtail", "box" ) );
                list.add( new DOTAttribute( "dir", "both" ) );
            }
            if ( flow.isAskedFor() ) {
                list.add( new DOTAttribute( "arrowtail", "onormal" ) );
                list.add( new DOTAttribute( "dir", "both" ) );
            }
            list.add( new DOTAttribute( "style",
                    conceptual
                            ? flow.isCritical()
                            ? "dashed"
                            : "dotted"
                            : flow.isCritical()
                            ? "bold"
                            : "normal"
            ) );

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
                list.add( new DOTAttribute( "headlabel", headLabel )
            );
            if ( tailLabel != null )
                list.add( new DOTAttribute( "taillabel", tailLabel )
            );
            if ( headLabel != null || tailLabel != null ) {
                list.add( new DOTAttribute( "labeldistance", LABEL_DISTANCE ) );
                list.add( new DOTAttribute( "labelangle", LABEL_ANGLE ) );
            }
            // Issue coloring
            if ( getAnalyst().hasUnwaivedIssues( getQueryService(),
                                                       flow, Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) {
                list.add( new DOTAttribute( "fontcolor", COLOR_ERROR ) );
                list.add( new DOTAttribute( "color", COLOR_ERROR ) );
                list.add( new DOTAttribute( "tooltip", sanitize( getAnalyst().getIssuesOverview( getQueryService(),
                        flow,
                        Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) ) );
            } else {
                list.add( new DOTAttribute( "tooltip", sanitize( flow.getTitle() ) ) );
            }
            return list;
        }
    }

    @Override
    public Object getContext() {
        return ( (SegmentObject) super.getContext() ).getSegment();
    }
}
