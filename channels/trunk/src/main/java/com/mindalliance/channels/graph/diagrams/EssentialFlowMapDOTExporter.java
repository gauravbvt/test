package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.graph.AbstractDOTExporter;
import com.mindalliance.channels.graph.AbstractMetaProvider;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.MetaProvider;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Risk;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.ScenarioObject;
import org.jgrapht.Graph;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Essential flow map exporter.
 * Exports a Graph in DOT format.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 19, 2010
 * Time: 11:30:31 AM
 */
public class EssentialFlowMapDOTExporter extends AbstractDOTExporter<Node, Flow> {

    /**
     * Stop vertex.
     */
    private static final String STOP = "stop";
    /**
     * Terminating, internal parts.
     */
    Set<Part> terminators = new HashSet<Part>();


    public EssentialFlowMapDOTExporter( MetaProvider<Node, Flow> metaProvider ) {
        super( metaProvider );
    }

    /**
     * {@inheritDoc}
     */
    protected void beforeExport( Graph<Node, Flow> g ) {
        super.beforeExport( g );
        for ( Node node : g.vertexSet() ) {
            if ( node.isPart() ) {
                Part part = (Part) node;
                if ( part.getScenario().isTerminatedBy( part ) &&
                        !isOnlyTheSourceOfFailedFlow( part, g ) ) {
                    terminators.add( part );
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void exportVertices( PrintWriter out, Graph<Node, Flow> g ) {
        AbstractMetaProvider<Node, Flow> metaProvider = (AbstractMetaProvider<Node, Flow>) getMetaProvider();
        Map<Scenario, Set<Node>> scenarioNodes = new HashMap<Scenario, Set<Node>>();
        for ( Node node : g.vertexSet() ) {
            Set<Node> nodesInScenario = scenarioNodes.get( node.getScenario() );
            if ( nodesInScenario == null ) {
                nodesInScenario = new HashSet<Node>();
                scenarioNodes.put( node.getScenario(), nodesInScenario );
            }
            nodesInScenario.add( node );
        }
        for ( Scenario scenario : scenarioNodes.keySet() ) {
            if ( !scenario.equals( getScenario() ) ) {
                out.println( "subgraph cluster_"
                        + scenario.getName().replaceAll( "[^a-zA-Z0-9_]", "_" )
                        + " {" );
                List<DOTAttribute> attributes = new DOTAttribute( "label",
                        "Scenario: " + scenario.getName() ).asList();
                if ( metaProvider.getDOTAttributeProvider() != null ) {
                    attributes.addAll(
                            metaProvider.getDOTAttributeProvider().getSubgraphAttributes( false ) );
                }
                if ( metaProvider.getURLProvider() != null ) {
                    String url = metaProvider.getURLProvider().
                            getGraphURL( scenarioNodes.get( scenario ).iterator().next() );
                    if ( url != null ) attributes.add( new DOTAttribute( "URL", url ) );
                }
                out.print( asGraphAttributes( attributes ) );
                out.println();
                printoutVertices( out, scenarioNodes.get( scenario ) );
                exportStop( out, metaProvider, scenario );
                exportRisks( out, metaProvider, g, scenario );
                out.println( "}" );
            } else {
                printoutVertices( out, scenarioNodes.get( scenario ) );
                exportStop( out, metaProvider, scenario );
                exportRisks( out, metaProvider, g, scenario );
            }
        }
    }

    private void exportRisks(
            PrintWriter out,
            AbstractMetaProvider<Node, Flow> metaProvider,
            Graph<Node, Flow> g,
            Scenario scenario ) {
        for ( Node node : g.vertexSet() ) {
            Part part = (Part) node;
            if ( !isOnlyTheSourceOfFailedFlow( part, g ) ) {
                if ( part.getScenario().equals( scenario ) )
                    for ( Risk risk : part.getMitigations() ) {
                        exportRisk( getRiskVertexId( part, risk ), risk, out, metaProvider );
                    }
            }
        }
        if ( getTerminatedScenarios().contains( scenario ) ) {
            for ( Risk risk : scenario.getRisks() ) {
                if ( risk.isEndsWithScenario() ) {
                    exportRisk( getRiskVertexId( scenario, risk ), risk, out, metaProvider );
                }
            }
        }
    }

    private void exportRisk(
            String riskVertexId,
            Risk risk,
            PrintWriter out,
            AbstractMetaProvider<Node, Flow> metaProvider ) {
        List<DOTAttribute> attributes = DOTAttribute.emptyList();
        attributes.add( new DOTAttribute( "fontcolor", AbstractMetaProvider.FONTCOLOR ) );
        attributes.add( new DOTAttribute( "fontsize", FlowMapMetaProvider.NODE_FONT_SIZE ) );
        attributes.add( new DOTAttribute( "fontname", FlowMapMetaProvider.NODE_FONT ) );
        attributes.add( new DOTAttribute( "labelloc", "b" ) );
        String label = sanitize( risk.getTitle( "|" ).replaceAll( "\\|", "\\\\n" ) );
        attributes.add( new DOTAttribute( "label", label ) );
        attributes.add( new DOTAttribute( "shape", "none" ) );
        attributes.add( new DOTAttribute( "tooltip", risk.getFullTitle() ) );
        String dirName;
        try {
            dirName = metaProvider.getImageDirectory().getFile().getAbsolutePath();
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to get image directory location", e );
        }
        attributes.add( new DOTAttribute( "image", dirName + "/" + getRiskIcon( risk ) ) );
        out.print( getIndent() );
        out.print( riskVertexId );
        out.print( "[" );
        out.print( asElementAttributes( attributes ) );
        out.println( "];" );
    }

    private String getRiskIcon( Risk risk ) {
        switch ( risk.getSeverity() ) {
            case Minor:
                return "risk_minor.png";
            case Major:
                return "risk_major.png";
            case Severe:
                return "risk_severe.png";
            default:
                throw new RuntimeException( "Unknown risk level" );
        }
    }

    private String getRiskVertexId( Part part, Risk risk ) {
        return "risk" + +part.getMitigations().indexOf( risk ) + "_" + part.getId();
    }

    private String getRiskVertexId( Scenario scenario, Risk risk ) {
        return "risk" + scenario.getRisks().indexOf( risk ) + "_" + scenario.getId();
    }


    private void exportStop(
            PrintWriter out,
            AbstractMetaProvider<Node, Flow> metaProvider,
            Scenario scenario ) {
        if ( getTerminatedScenarios().contains( scenario ) ) {
            List<DOTAttribute> attributes = DOTAttribute.emptyList();
            attributes.add( new DOTAttribute( "fontcolor", AbstractMetaProvider.FONTCOLOR ) );
            attributes.add( new DOTAttribute( "fontsize", FlowMapMetaProvider.NODE_FONT_SIZE ) );
            attributes.add( new DOTAttribute( "fontname", FlowMapMetaProvider.NODE_FONT ) );
            attributes.add( new DOTAttribute( "labelloc", "b" ) );
            String label = sanitize( scenario.getPhaseEventTitle() + " ends" );
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
            out.print( getScenarioStopId( scenario ) );
            out.print( "[" );
            out.print( asElementAttributes( attributes ) );
            out.println( "];" );
        }
    }

    private String getScenarioStopId( Scenario scenario ) {
        return STOP + scenario.getId();
    }


    protected void exportEdges( PrintWriter out, Graph<Node, Flow> g ) {
        super.exportEdges( out, g );
        if ( !terminators.isEmpty() )
            exportTerminations( out, g );
        exportRiskEdges( out, g );
    }

    private void exportRiskEdges( PrintWriter out, Graph<Node, Flow> g ) {
        for ( Node node : g.vertexSet() ) {
            Part part = (Part) node;
            if ( !isOnlyTheSourceOfFailedFlow( part, g ) ) {
                for ( Risk risk : part.getMitigations() ) {
                    exportRiskEdge( part, risk, out, g );
                }
            }
        }
        for ( Scenario scenario : getTerminatedScenarios() ) {
            for ( Risk risk : scenario.getRisks() ) {
                if ( risk.isEndsWithScenario() ) {
                    exportRiskEdge( scenario, risk, out, g );
                }
            }
        }
    }

    private void exportRiskEdge( Part part, Risk risk, PrintWriter out, Graph<Node, Flow> g ) {
        List<DOTAttribute> attributes = getNonFlowEdgeAttributes();
        attributes.add( new DOTAttribute( "label", "mitigates" ) );
        String riskId = getRiskVertexId( part, risk );
        String partId = getMetaProvider().getVertexIDProvider().getVertexName( part );
        out.print( getIndent() + partId + getArrow( g ) + riskId );
        out.print( "[" );
        if ( !attributes.isEmpty() ) {
            out.print( asElementAttributes( attributes ) );
        }
        out.println( "];" );
    }

    private void exportRiskEdge( Scenario scenario, Risk risk, PrintWriter out, Graph<Node, Flow> g ) {
        List<DOTAttribute> attributes = getNonFlowEdgeAttributes();
        attributes.add( new DOTAttribute( "label", "terminates" ) );
        String riskId = getRiskVertexId( scenario, risk );
        out.print( getIndent() + getScenarioStopId( scenario ) + getArrow( g ) + riskId );
        out.print( "[" );
        if ( !attributes.isEmpty() ) {
            out.print( asElementAttributes( attributes ) );
        }
        out.println( "];" );
    }

    private void exportTerminations( PrintWriter out, Graph<Node, Flow> g ) {
        for ( Part terminator : terminators ) {
            Scenario scenario = terminator.getScenario();
            List<DOTAttribute> attributes = getNonFlowEdgeAttributes();
            attributes.add( new DOTAttribute( "label", makeLabel( scenario.terminationCause( terminator ) ) ) );
            String terminatorId = getVertexID( terminator );
            out.print( getIndent() + terminatorId + getArrow( g ) + getScenarioStopId( scenario ) );
            out.print( "[" );
            if ( !attributes.isEmpty() ) {
                out.print( asElementAttributes( attributes ) );
            }
            out.println( "];" );
        }
    }

    private Set<Scenario> getTerminatedScenarios() {
        Set<Scenario> scenarios = new HashSet<Scenario>();
        for ( Part part : terminators ) {
            scenarios.add( part.getScenario() );
        }
        return scenarios;
    }

    private List<DOTAttribute> getNonFlowEdgeAttributes() {
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

    private ScenarioObject getFailure() {
        return (ScenarioObject) getMetaProvider().getContext();
    }

    private Scenario getScenario() {
        return getFailure().getScenario();
    }

    private boolean isOnlyTheSourceOfFailedFlow( Part part, Graph<Node, Flow> g ) {
        ScenarioObject failure = getFailure();
        return failure instanceof Flow
                && ( (Flow) failure ).getSource().equals( part )
                && g.edgesOf( part ).size() == 1;
    }

}
