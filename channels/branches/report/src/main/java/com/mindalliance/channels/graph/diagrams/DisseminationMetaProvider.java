package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.analysis.data.Dissemination;
import com.mindalliance.channels.graph.AbstractMetaProvider;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.DOTAttributeProvider;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.URLProvider;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.SegmentObject;
import com.mindalliance.channels.model.Transformation;
import org.jgrapht.ext.EdgeNameProvider;
import org.springframework.core.io.Resource;

import java.text.MessageFormat;
import java.util.List;

/**
 * Dissemination meta provider.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 22, 2010
 * Time: 8:27:02 PM
 */
public class DisseminationMetaProvider extends AbstractFlowMetaProvider<Node, Dissemination> {

    private static final int MAX_INFO_LENGTH = 20;

    public DisseminationMetaProvider(
            SegmentObject segmentObject,
            String outputFormat,
            Resource imageDirectory,
            Analyst analyst ) {
        super( (ModelObject) segmentObject, outputFormat, imageDirectory, analyst, false, false );
    }

    /**
     * {@inheritDoc}
     */
    public URLProvider<Node, Dissemination> getURLProvider() {
        return new URLProvider<Node, Dissemination>() {
            /**
             * The URL for the graph that contains the vertex
             *
             * @param node -- a vertex
             * @return a URL string
             */
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
            public String getEdgeURL( Dissemination edge ) {
                // Plan id = 0 for now sice there is only one plan
                Object[] args = {0, edge.getFlow().getId()};
                return MessageFormat.format( EDGE_URL_FORMAT, args );
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    public EdgeNameProvider<Dissemination> getEdgeLabelProvider() {
        return new EdgeNameProvider<Dissemination>() {
            public String getEdgeName( Dissemination edge ) {
                StringBuilder sb = new StringBuilder();
                sb.append( edge.getSubject().getLabel( MAX_INFO_LENGTH ) );
                Transformation.Type xformType = edge.getTransformationType();
                if ( !(edge.isRoot() || xformType ==  Transformation.Type.Identity ) ) {
                    sb.append( " (" );
                    sb.append( xformType.getLabel() );
                    sb.append( " " );
                    sb.append( edge.getTransformedSubject().getLabel( MAX_INFO_LENGTH ) );
                    sb.append( ")");
                }
                if ( edge.getFlow().isProhibited() ) {
                    sb.append( " [PROHIBITED]");
                }
                String label = AbstractMetaProvider.separate(
                        sb.toString(),
                        LINE_WRAP_SIZE ).replaceAll( "\\|", "\\\\n" );

                return sanitize( label );
            }
        };
    }

    public DOTAttributeProvider<Node, Dissemination> getDOTAttributeProvider() {
        return new SegmentDOTAttributeProvider();
    }

    /**
     * A DOTAttributeProvider for segments.
     */
    private class SegmentDOTAttributeProvider implements DOTAttributeProvider<Node, Dissemination> {

        public SegmentDOTAttributeProvider() {
        }

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
        public List<DOTAttribute> getSubgraphAttributes( boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "fontcolor", FONTCOLOR ) );
            list.add( new DOTAttribute( "fontsize", SUBGRAPH_FONT_SIZE ) );
            list.add( new DOTAttribute( "fontname", SUBGRAPH_FONT ) );
            list.add( new DOTAttribute( "color", SUBGRAPH_COLOR ) );
            list.add( new DOTAttribute( "style", "filled" ) );
            return list;
        }

        public List<DOTAttribute> getVertexAttributes( Node vertex, boolean highlighted ) {
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
                list.add( new DOTAttribute( "image", getIcon( DisseminationMetaProvider.this.getAnalyst().getImagingService(),
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
            if ( getAnalyst().hasUnwaivedIssues( vertex, Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) {
                list.add( new DOTAttribute( "fontcolor", COLOR_ERROR ) );
                list.add( new DOTAttribute( "tooltip", sanitize( getAnalyst().getIssuesSummary( vertex,
                        Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) ) );
            } else {
                String tooltip = vertex.getTitle();
                if ( vertex.isPart() ) {
                    List<Actor> partActors = getAnalyst().getQueryService().findAllActualActors(
                            ( (Part) vertex ).resourceSpec() );
                    if ( partActors.size() > 1 ) {
                        tooltip = sanitize( listActors( partActors ) );
                    }
                }
                list.add( new DOTAttribute( "tooltip", tooltip ) );
            }
            return list;
        }

        public List<DOTAttribute> getEdgeAttributes( Dissemination edge, boolean highlighted ) {
            Flow flow = edge.getFlow();
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
            if ( headLabel != null ) list.add( new DOTAttribute( "headlabel", headLabel ) );
            if ( tailLabel != null ) list.add( new DOTAttribute( "taillabel", tailLabel ) );
            if ( headLabel != null || tailLabel != null ) {
                list.add( new DOTAttribute( "labeldistance", LABEL_DISTANCE ) );
                list.add( new DOTAttribute( "labelangle", LABEL_ANGLE ) );
            }
            // Issue coloring
            if ( getAnalyst().hasUnwaivedIssues( flow, Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) {
                list.add( new DOTAttribute( "fontcolor", COLOR_ERROR ) );
                list.add( new DOTAttribute( "color", COLOR_ERROR ) );
                list.add( new DOTAttribute( "tooltip", sanitize( getAnalyst().getIssuesSummary( flow,
                        Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) ) );
            } else {
                list.add( new DOTAttribute( "tooltip", sanitize( flow.getTitle() ) ) );
            }
            return list;
        }

    }

    /**
     * {@inheritDoc}
     */
    public Object getContext() {
        return ( (SegmentObject) super.getContext() ).getSegment();
    }
}
