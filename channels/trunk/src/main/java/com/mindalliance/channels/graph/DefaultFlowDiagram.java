package com.mindalliance.channels.graph;

import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.text.MessageFormat;

import org.apache.wicket.markup.html.link.ImageMap;
import org.jgrapht.Graph;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 19, 2008
 * Time: 2:56:50 PM
 */
public class DefaultFlowDiagram implements FlowDiagram<Node, Flow> {
    /**
     * The PNG format
     */
    static final String PNG = "png";
    /**
     * The SVG format
     */
    static final String SVG = "svg";
    /**
     * The image map format
     */
    static final String IMAGE_MAP = "imap";
    /**
     * A graph builder
     */
    private GraphBuilder graphBuilder;
    /**
     * A GraphRenderer for nodes and flows
     */
    private GraphRenderer<Node, Flow> graphRenderer;
    /**
     * 0: scenario id, 1: node id
     */
    private String urlFormat = "?scenario={0}&node={1}";
    /**
     * Path to image directory
     */
    private String imageDirectory;

    /**
     * Constructor
     */
    public DefaultFlowDiagram() {
    }

    public void setGraphBuilder( GraphBuilder graphBuilder ) {
        this.graphBuilder = graphBuilder;
    }

    public void setGraphRenderer( GraphRenderer<Node, Flow> graphRenderer ) {
        this.graphRenderer = graphRenderer;
    }

    public String getUrlFormat() {
        return urlFormat;
    }

    public void setUrlFormat( String urlFormat ) {
        this.urlFormat = urlFormat;
    }

    public String getImageDirectory() {
        return imageDirectory;
    }

    public void setImageDirectory( String imageDirectory ) {
        this.imageDirectory = imageDirectory;
    }

    /**
     * Produces the PNG stream of a directed graph diagram of the scenario.
     *
     * @param scenario     A scenario
     * @param selectedNode The scenario node currently selected
     * @return An InputStream
     * @throws com.mindalliance.channels.graph.DiagramException
     *          when diagram generation fails
     */
    public InputStream getPNG( Scenario scenario, Node selectedNode ) throws DiagramException {
        Graph<Node, Flow> graph = graphBuilder.buildScenarioGraph( scenario );
        graphRenderer.highlightVertex( selectedNode );
        return render( graph, PNG, scenario );
    }

    /**
     * Produces the SVG stream of a directed graph diagram of the scenario.
     *
     * @param scenario     A scenario
     * @param selectedNode The scenario node currently selected
     * @return An InputStream
     * @throws com.mindalliance.channels.graph.DiagramException
     *          when diagram generation fails
     */
    public InputStream getSVG( Scenario scenario, Node selectedNode ) throws DiagramException {
        Graph<Node, Flow> graph = graphBuilder.buildScenarioGraph( scenario );
        graphRenderer.highlightVertex( selectedNode );
        return render( graph, SVG, scenario );
    }


    /**
     * Gets an image map component for a directed graph diagram of the scenario
     *
     * @param scenario     A scenario
     * @param selectedNode The scenario node currently selected
     * @return an ImageMap
     * @throws com.mindalliance.channels.graph.DiagramException
     *          when diagram generation fails
     */
    public ImageMap getImageMap( Scenario scenario, Node selectedNode ) throws DiagramException {
        Graph<Node, Flow> graph = graphBuilder.buildScenarioGraph( scenario );
        InputStream in = render( graph, IMAGE_MAP, scenario );
        BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ( ( line = reader.readLine() ) != null ) {
                sb.append( line );
            }
        } catch ( IOException e ) {
            throw new DiagramException( "Failed to read generated diagram", e );
        }
        String map = sb.toString();
        // patches apparent bug in dot
        map = map.replace( "base referer", "base referer " );
        System.out.println( map );
        return new ImageMap( map );
    }

    private InputStream render( Graph<Node, Flow> graph, String format,
                                Scenario scenario ) throws DiagramException {
        return graphRenderer.render( graph,
                getVertexIDProvider(),
                getVertexLabelProvider(),
                getEdgeLabelProvider(),
                getDOTAttributeProvider( format ),
                getUrlProvider( scenario ),
                format
        );
    }

    private URLProvider<Node, Flow> getUrlProvider( final Scenario scenario ) {
        return new URLProvider<Node, Flow>() {
            public String getVertexURL( Node vertex ) {
                Object[] args = {scenario.getId(), vertex.getId()};
                return MessageFormat.format( urlFormat, args );
            }

            public String getEdgeURL( Flow edge ) {
                return null;
            }
        };
    }

    private DOTAttributeProvider<Node, Flow> getDOTAttributeProvider( final String format ) {
        return new DOTAttributeProvider<Node, Flow>() {
            public List<DOTAttribute> getGraphAttributes() {
                return DOTAttribute.emptyList();
            }

            public List<DOTAttribute> getVertexAttributes( Node vertex, boolean highlighted ) {
                List<DOTAttribute> list = DOTAttribute.emptyList();
                if ( format.equalsIgnoreCase( SVG ) ) {
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
                    list.add( new DOTAttribute( "image", getIcon( vertex, format ) ) );
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
                    // list.add( new DOTAttribute( "style", "dotted" ) );
                    list.add( new DOTAttribute( "arrowtail", "onormal" ) );
                }
                list.add( new DOTAttribute( "fontname", "Arial" ) );
                list.add( new DOTAttribute( "fontsize", "9" ) );
                list.add( new DOTAttribute( "fontcolor", "darkslategray" ) );
                list.add( new DOTAttribute( "len", "1.5" ) );
                return list;
            }
        };
    }

    private EdgeNameProvider<Flow> getEdgeLabelProvider() {
        return new EdgeNameProvider<Flow>() {
            public String getEdgeName( Flow flow ) {
                return flow.getName();
            }
        };
    }

    private VertexNameProvider<Node> getVertexLabelProvider() {
        return new VertexNameProvider<Node>() {
            public String getVertexName( Node node ) {
                return getNodeLabel( node ).replaceAll( "\\|", "\\\\n" );
            }
        };
    }

    private VertexNameProvider<Node> getVertexIDProvider() {
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
                    : Part.DEFAULT_ACTOR;
            return MessageFormat.format( "{0}|{1}", actorString, part.getTask() );
        } else {
            return node.getName();
        }
    }

}
