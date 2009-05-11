package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.graph.AbstractDOTExporter;
import com.mindalliance.channels.graph.AbstractMetaProvider;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.MetaProvider;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;
import org.jgrapht.Graph;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Scenario DOT exporter.
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
     * Parts that start with the scenario.
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
        MetaProvider<Node, Flow> metaProvider = getMetaProvider();
        Scenario scenario = (Scenario) metaProvider.getContext();
        for ( Node node : g.vertexSet() ) {
            if ( node.isPart() ) {
                Part part = (Part) node;
                assert scenario.getEvent() != null;
                if ( part.getInitiatedEvent() == scenario.getEvent() ) {
                    assert ( part.getScenario() != scenario );
                    initiators.add( part );
                } else if ( part.getScenario() == scenario ) {
                    if ( part.isTerminatesEvent() ) {
                        terminators.add( part );
                    }
                    if ( part.isStartsWithScenario() ) {
                        autoStarters.add( part );
                    }
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
        Map<Scenario, Set<Node>> scenarioNodes = new HashMap<Scenario, Set<Node>>();
        for ( Node node : g.vertexSet() ) {
            Scenario scenario = node.getScenario();
            Set<Node> nodesInScenario = scenarioNodes.get( scenario );
            if ( nodesInScenario == null ) {
                nodesInScenario = new HashSet<Node>();
                scenarioNodes.put( scenario, nodesInScenario );
            }
            nodesInScenario.add( node );
        }
        for ( Scenario scenario : scenarioNodes.keySet() ) {
            if ( scenario != metaProvider.getContext() ) {
                out.println( "subgraph cluster_"
                        + scenario.getName().replaceAll( "[^a-zA-Z0-9_]", "_" )
                        + " {" );
                List<DOTAttribute> attributes = new DOTAttribute( "label",
                        "Scenario: " + scenario.getName() ).asList();
                if ( metaProvider.getDOTAttributeProvider() != null ) {
                    attributes.addAll(
                            metaProvider.getDOTAttributeProvider().getSubgraphAttributes() );
                }
                if ( metaProvider.getURLProvider() != null ) {
                    String url = metaProvider.getURLProvider().
                            getGraphURL( scenarioNodes.get( scenario ).iterator().next() );
                    if ( url != null ) attributes.add( new DOTAttribute( "URL", url ) );
                }
                out.print( asGraphAttributes( attributes ) );
                out.println();
                printoutVertices( out, scenarioNodes.get( scenario ) );
                out.println( "}" );
            } else {
                printoutVertices( out, scenarioNodes.get( scenario ) );
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
        Scenario scenario = (Scenario)metaProvider.getContext();
        String label = scenario.getEvent().getName() + " starts";
        attributes.add( new DOTAttribute( "label", label ) );
        attributes.add( new DOTAttribute( "shape", "none" ) );
        attributes.add( new DOTAttribute( "tooltip", label ) );
        attributes.add( new DOTAttribute( "image", metaProvider.getImageDirectory() + "/" + "start.png" ) );
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
        Scenario scenario = (Scenario)metaProvider.getContext();
        String label = scenario.getEvent().getName() + " ends";
        attributes.add( new DOTAttribute( "label", label ) );
        attributes.add( new DOTAttribute( "shape", "none" ) );
        attributes.add( new DOTAttribute( "tooltip", label ) );
        attributes.add( new DOTAttribute( "image", metaProvider.getImageDirectory() + "/" + "stop.png" ) );
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
        for ( Part initiator : initiators ) {
            List<DOTAttribute> attributes = getTimingEdgeAttributes();
            attributes.add( new DOTAttribute( "label", makeLabelAboutModelObject(
                    "Causes",
                    initiator.getInitiatedEvent() ) ) );
            attributes.add( new DOTAttribute(
                    "tooltip",
                    sanitize( "Causes "
                    + initiator.getInitiatedEvent().getName().toLowerCase() ) ) );
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
        for ( Part terminator : terminators ) {
            List<DOTAttribute> attributes = getTimingEdgeAttributes();
            attributes.add( new DOTAttribute( "label", makeLabelAboutModelObject(
                    "Ends",
                    terminator.getScenario() ) ) );
            attributes.add( new DOTAttribute(
                    "tooltip",
                    sanitize( "Ends "
                    + terminator.getScenario().getEvent().getName().toLowerCase() ) ) );
            String terminatorId = getVertexID( terminator );
            out.print( getIndent() + terminatorId + getArrow( g ) + STOP );
            out.print( "[" );
            if ( !attributes.isEmpty() ) {
                out.print( asElementAttributes( attributes ) );
            }
            out.println( "];" );
        }
    }

    private String makeLabelAboutModelObject( String s, ModelObject mo ) {
        return AbstractMetaProvider.separate(
                s + " " + mo.getName().toLowerCase(),
                AbstractMetaProvider.LINE_WRAP_SIZE ).replaceAll( "\\|", "\\\\n" );
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

    /**
     * Make label safe.
     * @param label  a string
     * @return a sanitized string
     */
    public String sanitize( String label ) {
        return label.replaceAll( "\"", "\\\\\"" );
    }

}
