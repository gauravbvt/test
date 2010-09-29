package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.graph.AbstractMetaProvider;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.DOTAttributeProvider;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.URLProvider;
import com.mindalliance.channels.imaging.ImagingService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.springframework.core.io.Resource;

import java.io.IOException;
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
public class FlowMapMetaProvider extends AbstractMetaProvider<Node, Flow> {
    /**
     * Color for subgraph contour
     */
    private static final String SUBGRAPH_COLOR = "azure2";
    /**
     * Font for subgraph labels.
     */
    private static final String SUBGRAPH_FONT = "Arial Bold Oblique";
    /**
     * Font size for subgraph labels.
     */
    private static final String SUBGRAPH_FONT_SIZE = "10";
    /**
     * Font for node labels
     */
    public static final String NODE_FONT = "Arial";
    /**
     * Font size for node labels.
     */
    public static final String NODE_FONT_SIZE = "10";
    /**
     * Distance for edge head and tail labels.
     */
    private static final String LABEL_DISTANCE = "1.0";
    /**
     * Distance for edge head and tail labels.
     */
    private static final String LABEL_ANGLE = "45";
   /**
     * Highlight pen width.
     */
    private static final String HIGHLIGHT_PENWIDTH = "2.0";
    /**
     * Highlight pen color.
     */
    private static final String HIGHLIGHT_COLOR = "gray";
    /**
     * Font of highlighted node.
     */
    private static final String HIGHLIGHT_NODE_FONT = "Arial Bold";
   /**
     * Segment in context.
     */
    private ModelObject context;
    /**
     * Whether to show goals.
     */
    private boolean showingGoals;

    public FlowMapMetaProvider( ModelObject modelObject,
                                String outputFormat,
                                Resource imageDirectory,
                                Analyst analyst ) {
        this( modelObject, outputFormat, imageDirectory, analyst, false );
    }

    public FlowMapMetaProvider( ModelObject modelObject,
                                String outputFormat,
                                Resource imageDirectory,
                                Analyst analyst,
                                boolean showingGoals ) {
        super( outputFormat, imageDirectory, analyst );
        this.context = modelObject;
        this.showingGoals = showingGoals;
    }

    public boolean isShowingGoals() {
        return showingGoals;
    }

    /**
     * Get context provisioned from.
     *
     * @return an object that knows of the vertices and edges
     */
    public Object getContext() {
        return context;
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
                if ( flow.isProhibited() ) {
                    flowName += " (PROHIBITED)";
                }
                String label = AbstractMetaProvider.separate(
                        flowName,
                        LINE_WRAP_SIZE ).replaceAll( "\\|", "\\\\n" );
                if ( flow.isAskedFor() && !label.endsWith( "?" ) ) {
                    label = label + "?";
                }
                return sanitize( label );
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    public VertexNameProvider<Node> getVertexLabelProvider() {
        return new VertexNameProvider<Node>() {
            public String getVertexName( Node node ) {
                String label = getNodeLabel( node ).replaceAll( "\\|", "\\\\n" );
                return sanitize( label );
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    public VertexNameProvider<Node> getVertexIDProvider() {
        return new VertexNameProvider<Node>() {
            public String getVertexName( Node node ) {
                return "" + node.getId();
            }
        };
    }

    protected String getNodeLabel( Node node ) {
        if ( node.isPart() ) {
            Part part = (Part) node;
            return part.getFullTitle( "|", getAnalyst().getQueryService() );
        } else {
            return "c";
        }
    }

    public static String getDefaultActor() {
        return Part.DEFAULT_ACTOR;
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

    private String listActors( List<Actor> partActors ) {
        StringBuilder sb = new StringBuilder();
        Iterator<Actor> actors = partActors.iterator();
        while ( actors.hasNext() ) {
            sb.append( actors.next().getName() );
            if ( actors.hasNext() ) sb.append( ", " );
        }
        return sb.toString();
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

    private String getIcon( ImagingService imagingService, Node node ) {
        String iconName;
        int numLines = 0;
        String imagesDirName;
        try {
            imagesDirName = getImageDirectory().getFile().getAbsolutePath();
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to get image directory location", e );
        }
        if ( node.isConnector() ) {
            Connector connector = (Connector) node;
            Flow flow = connector.getInnerFlow();
            if ( flow.isNeed() && flow.isSatisfied()
                    || flow.isCapability() && flow.isSatisfying() ) {
                iconName = imagesDirName + "/connector";
            } else {
                iconName = imagesDirName + "/connector_red";
            }
        }
        // node is a part
        else {
            String label = getNodeLabel( node );
            String[] lines = label.split( "\\|" );
            numLines = Math.min( lines.length, 5 );
            Part part = (Part) node;
            iconName = imagingService.findIconName( part, imagesDirName, getAnalyst().getQueryService() );
        }
        return iconName + ( numLines > 0 ? numLines : "" ) + ".png";
    }

}
