package com.mindalliance.channels.graph.diagrams;

import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.graph.URLProvider;
import com.mindalliance.channels.graph.DOTAttributeProvider;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.AbstractMetaProvider;
import com.mindalliance.channels.analysis.Analyst;

import java.text.MessageFormat;
import java.util.List;

/**
 * Provider of providers for scenarios.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 25, 2008
 * Time: 2:31:11 PM
 * A provider of graph attribute providers needed for rendering a scenario
 */
public class FlowMapMetaProvider extends AbstractMetaProvider<Node, Flow> {

    /**
     * Message format as URL template with {1} = scenario id.
     */
    private static String SCENARIO_URL_FORMAT = "?scenario={0,number,0}";
    /**
     * Message format as URL template with {1} = scenario id and {2} = vertex id.
     */
    private static String NODE_URL_FORMAT = "?scenario={0,number,0}&node={1,number,0}";
    /**
     * Color for subgraph contour
     */
    private static final String SUBGRAPH_COLOR = "azure2";
    /**
     * Font for subgraph labels.
     */
    private static final String SUBGRAPH_FONT = "Arial-Bold";
    /**
     * Font size for subgraph labels.
     */
    private static final String SUBGRAPH_FONT_SIZE = "10";
    /**
     * Font for node labels
     */
    private static final String NODE_FONT = "Arial";
    /**
     * Font size for node labels.
     */
    private static final String NODE_FONT_SIZE = "10";
    /**
     * Number of characters after which a long line is wrapped on separator.
     */
    private static final int LINE_WRAP_SIZE = 15;
    /**
     * Distance for edge head and tail labels.
     */
    private static final String LABEL_DISTANCE = "1.0";
    /**
     * Distance for edge head and tail labels.
     */
    private static final String LABEL_ANGLE = "45";
    /**
     * Scenario in context.
     */
    private Scenario scenario;

    public FlowMapMetaProvider( Scenario scenario,
                                 String outputFormat,
                                 String imageDirectory,
                                 Analyst analyst ) {
        super( outputFormat, imageDirectory, analyst );
        this.scenario = scenario;
    }

    /**
     * Get context provisioned from.
     *
     * @return an object that knows of the vertices and edges
     */
    public Object getContext() {
        return scenario;
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
                Object[] args = {node.getScenario().getId()};
                return MessageFormat.format( SCENARIO_URL_FORMAT, args );
            }

            /**
             * The vertex's URL. Returns null if none.
             *
             * @param node -- a vertex
             * @return a URL string
             */
            public String getVertexURL( Node node ) {
                Object[] args = {node.getScenario().getId(), node.getId()};
                return MessageFormat.format( NODE_URL_FORMAT, args );
            }

            /**
             * The edges's URL. Returns null if none.
             *
             * @param edge -- an edge
             * @return a URL string
             */
            public String getEdgeURL( Flow edge ) {
                return null;
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    public EdgeNameProvider<Flow> getEdgeLabelProvider() {
        return new EdgeNameProvider<Flow>() {
            public String getEdgeName( Flow flow ) {
                String label = separate(
                        flow.getName(),
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

    private String getNodeLabel( Node node ) {
        if ( node.isPart() ) {
            Part part = (Part) node;
            String label = "";
            if ( part.getActor() != null ) label += part.getActor().toString();
            if ( part.getRole() != null ) {
                if ( !label.isEmpty() ) label += "|as ";
                label += part.getRole();
            }
            if ( part.getOrganization() != null ) {
                if ( !label.isEmpty() ) {
                    if ( part.getActor() == null || part.getRole() == null ) {
                        label += "|in ";
                    } else {
                        label += " in ";
                    }
                }
                label += part.getOrganization();
            }
            if ( !label.isEmpty() ) label += "|";
            label += part.getTask();
            if ( part.isRepeating() ) {
                label += " (every " + part.getRepeatsEvery().toString() + ")";
            }
            return label;
        } else {
            return node.getName();
        }
    }

    public static String getDefaultActor() {
        return Part.DEFAULT_ACTOR;
    }

    public DOTAttributeProvider<Node, Flow> getDOTAttributeProvider() {
        return new ScenarioDOTAttributeProvider();
    }

    /**
     * A DOTAttributeProvider for scenarios.
     */
    private class ScenarioDOTAttributeProvider implements DOTAttributeProvider<Node, Flow> {

        public ScenarioDOTAttributeProvider() {
        }

        public List<DOTAttribute> getGraphAttributes() {
            List<DOTAttribute> list = DOTAttribute.emptyList();
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
        public List<DOTAttribute> getSubgraphAttributes() {
            List<DOTAttribute> list = DOTAttribute.emptyList();
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
                    // scenarioNode
                } else {
                    list.add( new DOTAttribute( "shape", "egg" ) );
                }
                // assuming a bitmap format
            } else {
                list.add( new DOTAttribute( "image", getIcon( vertex ) ) );
                list.add( new DOTAttribute( "labelloc", "b" ) );
                if ( highlighted ) {
                    list.add( new DOTAttribute( "shape", "box" ) );
                    list.add( new DOTAttribute( "style", "solid" ) );
                    list.add( new DOTAttribute( "color", "gray" ) );
                } else {
                    list.add( new DOTAttribute( "shape", "none" ) );
                }
            }
            list.add( new DOTAttribute( "fontsize", NODE_FONT_SIZE ) );
            list.add( new DOTAttribute( "fontname", NODE_FONT ) );
            if ( getAnalyst().hasIssues( vertex, Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) {
                list.add( new DOTAttribute( "fontcolor", COLOR_ERROR ) );
                list.add( new DOTAttribute( "tooltip", sanitize( getAnalyst().getIssuesSummary( vertex,
                        Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) ) );
            }
            return list;
        }

        public List<DOTAttribute> getEdgeAttributes( Flow edge, boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "arrowsize", "0.75" ) );
            list.add( new DOTAttribute( "fontname", EDGE_FONT ) );
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
            if ( getAnalyst().hasIssues( edge, Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) {
                list.add( new DOTAttribute( "fontcolor", COLOR_ERROR ) );
                list.add( new DOTAttribute( "color", COLOR_ERROR ) );
                list.add( new DOTAttribute( "tooltip", sanitize( getAnalyst().getIssuesSummary( edge,
                        Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) ) );
            }
            return list;
        }

    }

    private String getIcon( Node node ) {
        String iconName;
        int numLines = 0;
        if ( node.isConnector() ) {
            iconName = "connector";
        }
        // node is a part
        else {
            String label = getNodeLabel( node );
            String[] lines = label.split( "\\|" );
            numLines = Math.min( lines.length, 3 );
            Part part = (Part) node;
            if ( part.getActor() != null ) {
                iconName = part.isSystem() ? "system" : "person";
            } else if ( part.getRole() != null ) {
                iconName = part.isSystem() ? "system" : "role";
            } else if ( part.getOrganization() != null ) {
                iconName = "organization";
            } else {
                iconName = "unknown";
            }
        }
        return getImageDirectory() + "/" + iconName + ( numLines > 0 ? numLines : "" ) + ".png";
    }

}
