package com.mindalliance.channels.graph;

import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;

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
public class ScenarioMetaProvider implements MetaProvider<Node,Flow> {

    Scenario scenario;
    String outputFormat;
    String urlFormat;
    String imageDirectory;

    public ScenarioMetaProvider( Scenario scenario, String outputFormat, String urlFormat, String imageDirectory ) {
        this.scenario = scenario;
        this.outputFormat = outputFormat;
        this.urlFormat = urlFormat;
        this.imageDirectory = imageDirectory;
    }

    public URLProvider<Node,Flow> getURLProvider() {
        return new URLProvider<Node, Flow>() {
            /**
             * The vertex's URL. Returns null if none.
             *
             * @param vertex -- a vertex
             * @return a URL string
             */
            public String getVertexURL( Node vertex ) {
                Object[] args = {scenario.getId(), vertex.getId()};
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

    public DOTAttributeProvider<Node, Flow> getDOTAttributeProvider( ) {
    return new DOTAttributeProvider<Node, Flow>() {
        public List<DOTAttribute> getGraphAttributes() {
            return DOTAttribute.emptyList();
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
                list.add( new DOTAttribute( "image", getIcon( vertex, outputFormat ) ) );
                list.add( new DOTAttribute( "labelloc", "b" ) );
                if ( highlighted ) {
                    list.add( new DOTAttribute( "shape", "box" ) );
                    list.add( new DOTAttribute( "style", "dotted" ) );
                } else {
                    list.add( new DOTAttribute( "shape", "none" ) );
                }
            }
            list.add( new DOTAttribute( "fontsize", "10" ) );
            list.add( new DOTAttribute( "fontname", "Arial" ) );
            return list;
        }

        public List<DOTAttribute> getEdgeAttributes( Flow edge, boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            if ( edge.isAskedFor() ) {
                list.add( new DOTAttribute( "style", "dotted" ) );
                // list.add( new DOTAttribute( "arrowtail", "onormal" ) );
            }
            list.add( new DOTAttribute( "arrowsize", "0.75" ) );
            list.add( new DOTAttribute( "fontname", "Arial" ) );
            list.add( new DOTAttribute( "fontsize", "9" ) );
            list.add( new DOTAttribute( "fontcolor", "darkslategray" ) );
            list.add( new DOTAttribute( "len", "1.5" ) );
            return list;
        }
    };
}

    public EdgeNameProvider<Flow> getEdgeLabelProvider() {
        return new EdgeNameProvider<Flow>() {
            public String getEdgeName( Flow flow ) {
                return flow.getName();
            }
        };
    }

    public VertexNameProvider<Node> getVertexLabelProvider() {
        return new VertexNameProvider<Node>() {
            public String getVertexName( Node node ) {
                return getNodeLabel( node ).replaceAll( "\\|", "\\\\n" );
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

    private String getIcon( Node node, String format ) {
        String iconName;
        int numLines = 0;
        if ( node.isScenarioNode() ) {
            iconName = "scenario1";
        } else if ( node.isConnector() ) {
            iconName = "connector";
        }
        // node is a part
        else {
            String label = getNodeLabel( node );
            String[] lines = label.split( "\\|" );
            numLines = Math.min( lines.length, 2 );
            Part part = (Part) node;
            if ( part.getActor() != null ) {
                iconName = part.getActor().isSystem() ? "system" : "person";
            } else if ( part.getRole() != null ) {
                iconName = "role";
            } else if ( part.getOrganization() != null ) {
                iconName = "organization";
            } else {
                iconName = "unknown";
            }
        }
        return imageDirectory + "/" + iconName + ( numLines > 0 ? numLines : "" ) + "." + format;
    }

    private String getNodeLabel( Node node ) {
        if ( node.isPart() ) {
            Part part = (Part) node;
            final String actorString = part.getActor() != null ? part.getActor().toString()
                    : part.getRole() != null ? part.getRole().toString()
                    : part.getOrganization() != null ? part.getOrganization().toString()
                    : Part.getDefaultActor();
            return MessageFormat.format( "{0}|{1}", actorString, part.getTask() );
        } else {
            return node.getName();
        }
    }

}
