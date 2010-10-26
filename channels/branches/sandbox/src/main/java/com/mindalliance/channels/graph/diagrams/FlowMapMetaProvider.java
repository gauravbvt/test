package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.graph.AbstractMetaProvider;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.DOTAttributeProvider;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.URLProvider;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import org.jgrapht.ext.EdgeNameProvider;
import org.springframework.core.io.Resource;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;

/**
 * Provider of providers for segments.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 25, 2008
 * Time: 2:31:11 PM
 * A provider of graph attribute providers needed for rendering a segment
 */
public class FlowMapMetaProvider extends AbstractFlowMetaProvider<Node, Flow> {

    public FlowMapMetaProvider( ModelObject modelObject,
                                String outputFormat,
                                Resource imageDirectory,
                                Analyst analyst ) {
        this( modelObject, outputFormat, imageDirectory, analyst, false, false );
    }

    public FlowMapMetaProvider( ModelObject modelObject,
                                String outputFormat,
                                Resource imageDirectory,
                                Analyst analyst,
                                boolean showingGoals,
                                boolean showingConnectors ) {
        super( modelObject, outputFormat, imageDirectory, analyst, showingGoals, showingConnectors );
    }

    /**
     * {@inheritDoc}
     */
    public URLProvider<Node, Flow> getURLProvider() {
        return new URLProvider<Node, Flow>() {
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
            public String getEdgeURL( Flow edge ) {
                // Plan id = 0 for now sice there is only one plan
                Object[] args = {0, edge.getId()};
                return MessageFormat.format( EDGE_URL_FORMAT, args );
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    public EdgeNameProvider<Flow> getEdgeLabelProvider() {
        return new EdgeNameProvider<Flow>() {
            public String getEdgeName( Flow flow ) {
                String flowName = flow.getName();
                if ( flow.isAskedFor() && !flowName.endsWith( "?" ) ) {
                    flowName += "?";
                }
                if ( flow.isProhibited() ) {
                    flowName += " (PROHIBITED)";
                }
                String label = AbstractMetaProvider.separate(
                        flowName,
                        LINE_WRAP_SIZE ).replaceAll( "\\|", "\\\\n" );
                return sanitize( label );
            }
        };
    }


    public DOTAttributeProvider<Node, Flow> getDOTAttributeProvider() {
        return new SegmentDOTAttributeProvider();
    }

    /**
     * A DOTAttributeProvider for segments.
     */
    private class SegmentDOTAttributeProvider implements DOTAttributeProvider<Node, Flow> {

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
                list.add( new DOTAttribute( "image", getIcon( FlowMapMetaProvider.this.getAnalyst().getImagingService(),
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
            if ( vertex.isConnector() ) {
                Connector connector = (Connector) vertex;
                Iterator<ExternalFlow> externalFlows = connector.externalFlows();
                list.add( new DOTAttribute( "fontcolor", "white" ) );
                if ( externalFlows.hasNext() ) {
                    list.add( new DOTAttribute( "tooltip", "Connected to: " + summarizeExternalFlows( externalFlows ) ) );
                } else {
                    if ( connector.isSource() && !connector.getInnerFlow().isSatisfied() ) {
                        list.add( new DOTAttribute( "tooltip", "Need completely unsatisfied" ) );
                    } else if ( connector.isTarget() && !connector.getInnerFlow().isSatisfying() ) {
                        list.add( new DOTAttribute( "tooltip", "Capability unused" ) );
                    } else {
                        list.add( new DOTAttribute(
                                "tooltip",
                                connector.isTarget() ? "Capability" : "Need" ) );
                    }
                }
            }
            return list;
        }

        public List<DOTAttribute> getEdgeAttributes( Flow edge, boolean highlighted ) {
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
            if ( edge.isAskedFor() ) {
                list.add( new DOTAttribute( "arrowtail", "onormal" ) );
                list.add( new DOTAttribute( "style", edge.isCritical() ? "bold" : "solid" ) );
            } else {
                if ( edge.isCritical() ) {
                    list.add( new DOTAttribute( "style", "bold" ) );
                    list.add( new DOTAttribute( "style", "bold" ) );
                    list.add( new DOTAttribute( "fontcolor", "black" ) );
                }
            }
            // head and tail labels
            String headLabel = null;
            String tailLabel = null;
            if ( edge.isAll() ) {
                if ( edge.isTerminatingToTarget() )
                    headLabel = "(stop all)";
                else if ( edge.isTriggeringToTarget() )
                    headLabel = "(start all)";
                else {
                    headLabel = "(all)";
                }
            } else {
                if ( edge.isTerminatingToTarget() )
                    headLabel = "(stop)";
                else if ( edge.isTriggeringToTarget() )
                    headLabel = "(start)";

            }
            if ( edge.isTerminatingToSource() ) {
                tailLabel = "(stop)";
            } else if ( edge.isTriggeringToSource() ) {
                tailLabel = "(start)";
            }
            if ( headLabel != null ) list.add( new DOTAttribute( "headlabel", headLabel ) );
            if ( tailLabel != null ) list.add( new DOTAttribute( "taillabel", tailLabel ) );
            if ( headLabel != null || tailLabel != null ) {
                list.add( new DOTAttribute( "labeldistance", LABEL_DISTANCE ) );
                list.add( new DOTAttribute( "labelangle", LABEL_ANGLE ) );
            }
            // Issue coloring
            if ( getAnalyst().hasUnwaivedIssues( edge, Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) {
                list.add( new DOTAttribute( "fontcolor", COLOR_ERROR ) );
                list.add( new DOTAttribute( "color", COLOR_ERROR ) );
                list.add( new DOTAttribute( "tooltip", sanitize( getAnalyst().getIssuesSummary( edge,
                        Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) ) );
            } else {
                list.add( new DOTAttribute( "tooltip", sanitize( edge.getTitle() ) ) );
            }
            return list;
        }

    }

    private String summarizeExternalFlows( Iterator<ExternalFlow> externalFlows ) {
        StringBuilder sb = new StringBuilder();
        while ( externalFlows.hasNext() ) {
            ExternalFlow flow = externalFlows.next();
            sb.append( flow.getTitle() );
            if ( externalFlows.hasNext() ) sb.append( " -- " );
        }
        return sanitize( sb.toString() );
    }

}
