package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.graph.AbstractDOTExporter;
import com.mindalliance.channels.graph.AbstractMetaProvider;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.MetaProvider;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
import org.jgrapht.Graph;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Flow map DOT exporter.
 * Exports a Graph in DOT format.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 19, 2008
 * Time: 4:15:11 PM
 */
public class FlowMapDOTExporter extends AbstractDOTExporter<Node, Flow> {

    /**
     * Start vertex.
     */
    private static final String START = "__start__";
    /**
     * Stop vertex.
     */
    private static final String STOP = "__stop__";
    /**
     * Initiating, external parts.
     */
    Set<Part> initiators = new HashSet<Part>();
    /**
     * Terminating, internal parts.
     */
    Set<Part> terminators = new HashSet<Part>();
    /**
     * Parts that start with the segment.
     */
    Set<Part> autoStarters = new HashSet<Part>();

    public FlowMapDOTExporter( MetaProvider<Node, Flow> metaProvider ) {
        super( metaProvider );
    }

    /**
     * {@inheritDoc}
     */
    protected void beforeExport( Graph<Node, Flow> g ) {
        super.beforeExport( g );
        Segment segment = getSegment();
        for ( Node node : g.vertexSet() ) {
            if ( node.isPart() ) {
                Part part = (Part) node;
                assert segment.getEvent() != null;
                if ( segment.isInitiatedBy( part ) ) {
                    initiators.add( part );
                } else if ( segment.isTerminatedBy( part ) ) {
                    terminators.add( part );
                }
                if ( part.getSegment().equals( segment ) && part.setIsStartsWithSegment() ) {
                    autoStarters.add( part );
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void exportVertices( PrintWriter out, Graph<Node, Flow> g ) {
        AbstractMetaProvider<Node, Flow> metaProvider = (AbstractMetaProvider<Node, Flow>) getMetaProvider();
        if ( !( initiators.isEmpty() && autoStarters.isEmpty() ) ) exportStart( out, metaProvider );
        Map<Segment, Set<Node>> segmentNodes = new HashMap<Segment, Set<Node>>();
        for ( Node node : g.vertexSet() ) {
            Segment segment = node.getSegment();
            Set<Node> nodesInSegment = segmentNodes.get( segment );
            if ( nodesInSegment == null ) {
                nodesInSegment = new HashSet<Node>();
                segmentNodes.put( segment, nodesInSegment );
            }
            nodesInSegment.add( node );
        }
        for ( Segment segment : segmentNodes.keySet() ) {
            if ( !segment.equals( getSegment() ) ) {
                out.println( "subgraph cluster_"
                        + segment.getName().replaceAll( "[^a-zA-Z0-9_]", "_" )
                        + " {" );
                List<DOTAttribute> attributes = new DOTAttribute( "label",
                        "Segment: " + segment.getName() ).asList();
                if ( metaProvider.getDOTAttributeProvider() != null ) {
                    attributes.addAll(
                            metaProvider.getDOTAttributeProvider().getSubgraphAttributes( false ) );
                }
                if ( metaProvider.getURLProvider() != null ) {
                    String url = metaProvider.getURLProvider().
                            getGraphURL( segmentNodes.get( segment ).iterator().next() );
                    if ( url != null ) attributes.add( new DOTAttribute( "URL", url ) );
                }
                out.print( asGraphAttributes( attributes ) );
                out.println();
                printoutVertices( out, segmentNodes.get( segment ) );
                out.println( "}" );
            } else {
                printoutVertices( out, segmentNodes.get( segment ) );
            }
        }
        if ( !terminators.isEmpty() ) exportStop( out, metaProvider );
    }

    private void exportStart( PrintWriter out, AbstractMetaProvider<Node, Flow> metaProvider ) {
        List<DOTAttribute> attributes = DOTAttribute.emptyList();
        attributes.add( new DOTAttribute( "fontcolor", AbstractMetaProvider.FONTCOLOR ) );
        attributes.add( new DOTAttribute( "fontsize", FlowMapMetaProvider.NODE_FONT_SIZE ) );
        attributes.add( new DOTAttribute( "fontname", FlowMapMetaProvider.NODE_FONT ) );
        attributes.add( new DOTAttribute( "labelloc", "b" ) );
        String label = "Segment starts";
        attributes.add( new DOTAttribute( "label", label ) );
        attributes.add( new DOTAttribute( "shape", "none" ) );
        attributes.add( new DOTAttribute( "tooltip", label ) );
        String dirName;
        try {
            dirName = metaProvider.getImageDirectory().getFile().getAbsolutePath();
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to get image directory location", e );
        }
        attributes.add( new DOTAttribute( "image", dirName + "/" + "start.png" ) );
        out.print( getIndent() );
        out.print( START );
        out.print( "[" );
        out.print( asElementAttributes( attributes ) );
        out.println( "];" );
    }

    private void exportStop( PrintWriter out, AbstractMetaProvider<Node, Flow> metaProvider ) {
        List<DOTAttribute> attributes = DOTAttribute.emptyList();
        attributes.add( new DOTAttribute( "fontcolor", AbstractMetaProvider.FONTCOLOR ) );
        attributes.add( new DOTAttribute( "fontsize", FlowMapMetaProvider.NODE_FONT_SIZE ) );
        attributes.add( new DOTAttribute( "fontname", FlowMapMetaProvider.NODE_FONT ) );
        attributes.add( new DOTAttribute( "labelloc", "b" ) );
        String label = "Segment ends";
        attributes.add( new DOTAttribute( "label", label ) );
        attributes.add( new DOTAttribute( "shape", "none" ) );
        attributes.add( new DOTAttribute( "tooltip", label ) );
        String dirName;
        try {
            dirName = metaProvider.getImageDirectory().getFile().getAbsolutePath();
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to get image directory location", e );
        }
        attributes.add( new DOTAttribute( "image", dirName + "/" + "stop.png" ) );
        out.print( getIndent() );
        out.print( STOP );
        out.print( "[" );
        out.print( asElementAttributes( attributes ) );
        out.println( "];" );
    }

    protected void exportEdges( PrintWriter out, Graph<Node, Flow> g ) {
        if ( !initiators.isEmpty() ) exportInitiations( out, g );
        if ( !autoStarters.isEmpty() ) exportAutoStarts( out, g );
        super.exportEdges( out, g );
        if ( !terminators.isEmpty() ) exportTerminations( out, g );
    }

    private void exportInitiations( PrintWriter out, Graph<Node, Flow> g ) {
        Segment segment = getSegment();
        for ( Part initiator : initiators ) {
            List<DOTAttribute> attributes = getTimingEdgeAttributes();
            attributes.add( new DOTAttribute( "label", makeLabel( segment.initiationCause( initiator ) ) ) );
            /*attributes.add( new DOTAttribute(
                    "tooltip",
                    sanitize( segment.initiationCause( initiator ) ) ) );*/
            String initiatorId = getVertexID( initiator );
            out.print( getIndent() + initiatorId + getArrow( g ) + START );
            out.print( "[" );
            if ( !attributes.isEmpty() ) {
                out.print( asElementAttributes( attributes ) );
            }
            out.println( "];" );
        }
    }

    private void exportAutoStarts( PrintWriter out, Graph<Node, Flow> g ) {
        for ( Part autoStarter : autoStarters ) {
            List<DOTAttribute> attributes = getTimingEdgeAttributes();
            attributes.add( new DOTAttribute( "headlabel", "(starts)" ) );
            String autoStarterId = getVertexID( autoStarter );
            out.print( getIndent() + START + getArrow( g ) + autoStarterId );
            out.print( "[" );
            if ( !attributes.isEmpty() ) {
                out.print( asElementAttributes( attributes ) );
            }
            out.println( "];" );
        }
    }

    private void exportTerminations( PrintWriter out, Graph<Node, Flow> g ) {
        Segment segment = getSegment();
        for ( Part terminator : terminators ) {
            List<DOTAttribute> attributes = getTimingEdgeAttributes();
            attributes.add( new DOTAttribute( "label", makeLabel( segment.terminationCause( terminator ) ) ) );
            /*attributes.add( new DOTAttribute(
                    "tooltip",
                    sanitize( segment.terminationCause( terminator ) ) ) );*/
            String terminatorId = getVertexID( terminator );
            out.print( getIndent() + terminatorId + getArrow( g ) + STOP );
            out.print( "[" );
            if ( !attributes.isEmpty() ) {
                out.print( asElementAttributes( attributes ) );
            }
            out.println( "];" );
        }
    }

    private List<DOTAttribute> getTimingEdgeAttributes() {
        List<DOTAttribute> list = DOTAttribute.emptyList();
        list.add( new DOTAttribute( "color", "gray" ) );
        list.add( new DOTAttribute( "arrowhead", "none" ) );
        list.add( new DOTAttribute( "fontname", AbstractMetaProvider.EDGE_FONT ) );
        list.add( new DOTAttribute( "fontsize", AbstractMetaProvider.EDGE_FONT_SIZE ) );
        list.add( new DOTAttribute( "fontcolor", "dimgray" ) );
        list.add( new DOTAttribute( "len", "1.5" ) );
        list.add( new DOTAttribute( "weight", "2.0" ) );
        return list;
    }


    private Segment getSegment() {
        return (Segment) getMetaProvider().getContext();
    }

}
