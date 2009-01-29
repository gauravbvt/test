package com.mindalliance.channels.graph;

import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.analysis.Analyst;

import java.text.MessageFormat;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 25, 2008
 * Time: 2:31:11 PM
 * A provider of graph attribute providers needed for rendering a scenario
 */
public class ScenarioMetaProvider implements MetaProvider<Node, Flow> {

    /**
     * Direction of graph layout
     */
    private static final String RANKDIR = "LR";
    /**
     * Color used to indicate issues.
     */
    private static final String COLOR_ERROR = "red3";
    /**
     * Color for subgraph contour
     */
    private static final String SUBGRAPH_COLOR = "azure2";
    /**
     * Font for subgraph labels
     */
    private static final String SUBGRAPH_FONT = "Arial-Bold";
    /**
     * Font size for subgraph labels
     */
    private static final String SUBGRAPH_FONT_SIZE = "10";
    /**
     * Font for node labels
     */
    private static final String NODE_FONT = "Arial";
    /**
     * Font for edge labels
     */
    private static final String EDGE_FONT = "Helvetica-Oblique";
    /**
     * Font size for edge labels
     */
    private static final String EDGE_FONT_SIZE = "8";
    /**
     * Font size for node labels
     */
    private static final String NODE_FONT_SIZE = "10";
    /**
     * Scenario in context
     */
    private Scenario scenario;
    /**
     * PNG, SVG, IMAP etc.
     */
    private String outputFormat;
    /**
     * Message format as URL template with {1} = scenario id
     */
    private String scenarioUrlFormat;
    /**
     * Message format as URL template with {1} = scenario id and {2} = vertex id
     */
    private String urlFormat;
    /**
     * Relative path to icon directory
     */
    private String imageDirectory;
    /**
     * Scenario analyst in context
     */
    private Analyst analyst;

    public ScenarioMetaProvider( Scenario scenario, String outputFormat, String urlFormat,
                                 String scenarioUrlFormat, String imageDirectory,
                                 Analyst analyst ) {
        this.scenario = scenario;
        this.outputFormat = outputFormat;
        this.urlFormat = urlFormat;
        this.scenarioUrlFormat = scenarioUrlFormat;
        this.imageDirectory = imageDirectory;
        this.analyst = analyst;
    }

    /**
     * Get context provisioned from
     *
     * @return an object that knows of the vertices and edges
     */
    public Object getContext() {
        return scenario;
    }

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
                return MessageFormat.format( scenarioUrlFormat, args );
            }

            /**
             * The vertex's URL. Returns null if none.
             *
             * @param node -- a vertex
             * @return a URL string
             */
            public String getVertexURL( Node node ) {
                Object[] args = {node.getScenario().getId(), node.getId()};
                return MessageFormat.format( urlFormat, args );
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

    public EdgeNameProvider<Flow> getEdgeLabelProvider() {
        return new EdgeNameProvider<Flow>() {
            public String getEdgeName( Flow flow ) {
                String label = flow.getName().replaceAll( "\\s+", "\\\\n" );
                if ( flow.isAskedFor() && !label.endsWith( "?" ) ) {
                    label = label + "?";
                }
                return sanitize( label );
            }
        };
    }

    public VertexNameProvider<Node> getVertexLabelProvider() {
        return new VertexNameProvider<Node>() {
            public String getVertexName( Node node ) {
                String label = getNodeLabel( node ).replaceAll( "\\|", "\\\\n" );
                return sanitize( label );
            }
        };
    }

    public VertexNameProvider<Node> getVertexIDProvider() {
        return new VertexNameProvider<Node>() {
            public String getVertexName( Node node ) {
                return "" + node.getId();
            }
        };
    }

    private String sanitize( String label ) {
        return label.replaceAll( "\"", "\\\\\"" );
    }

    private String getNodeLabel( Node node ) {
        if ( node.isPart() ) {
            Part part = (Part) node;
            String label = "";
            if (part.getActor() != null) label += part.getActor().toString();
            if (part.getRole() != null) {
                if (!label.isEmpty()) label += "|as ";
                label += part.getRole();
            }
            if (part.getOrganization() != null) {
                if (!label.isEmpty()) label += "|in ";
                label += part.getOrganization();
            }
            if (!label.isEmpty()) label += "|";
            label += part.getTask();
/*
            final String actorString = part.getActor() != null ? part.getActor().toString()
                    : part.getRole() != null ? part.getRole().toString()
                    : part.getOrganization() != null ? part.getOrganization().toString()
                    : getDefaultActor();
            return MessageFormat.format( "{0}|{1}", actorString, part.getTask() );
*/
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
            list.add( new DOTAttribute( "rankdir", RANKDIR ) );
            // list.add( new DOTAttribute( "overlap", "false" ) );
            // list.add( new DOTAttribute( "splines", "true" ) );
            // list.add( new DOTAttribute( "sep", ".1" ) );
            return list;
        }

        /**
         * Gets semi-colon-separated style declarations for subgraphs
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
            if ( outputFormat.equalsIgnoreCase( FlowDiagram.SVG ) ) {
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
            if ( analyst.hasIssues( vertex, Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) {
                list.add( new DOTAttribute( "fontcolor", COLOR_ERROR ) );
                list.add( new DOTAttribute( "tooltip", sanitize( analyst.getIssuesSummary( vertex,
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
            if ( edge.isAll() ) {
                list.add( new DOTAttribute( "headlabel", "(all)" ) );
            }
            if ( analyst.hasIssues( edge, Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) {
                list.add( new DOTAttribute( "fontcolor", COLOR_ERROR ) );
                list.add( new DOTAttribute( "color", COLOR_ERROR ) );
                list.add( new DOTAttribute( "tooltip", sanitize( analyst.getIssuesSummary( edge,
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
                iconName = "role";
            } else if ( part.getOrganization() != null ) {
                iconName = "organization";
            } else {
                iconName = "unknown";
            }
        }
        return imageDirectory + "/" + iconName + ( numLines > 0 ? numLines : "" ) + ".png";
    }

}
