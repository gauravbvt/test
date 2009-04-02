package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.graph.URLProvider;
import com.mindalliance.channels.graph.DOTAttributeProvider;
import com.mindalliance.channels.graph.AbstractMetaProvider;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.analysis.network.ScenarioRelationship;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;

import java.util.List;
import java.text.MessageFormat;

/**
 * Plan meta provider.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 1, 2009
 * Time: 8:02:40 PM
 */
public class PlanMapMetaProvider extends AbstractMetaProvider<Scenario, ScenarioRelationship> {
    /**
     * Message format as URL template with {1} = scenario id.
     */
    private static String SCENARIO_URL_FORMAT = "?scenario={0,number,0}";
    /**
     * Message format as URL template with {1} = scenario id.
     */
    private static String SCENARIO_REL_URL_FORMAT = "?scenarioRel={0,number,0},{1,number,0}";
    /**
     * Number of characters after which a long line is wrapped on separator.
     */
    private static final int LINE_WRAP_SIZE = 15;
    /**
     * Color for subgraph contour
     */
    private static final String SCENARIO_COLOR = "azure2";
    /**
     * Font for subgraph labels.
     */
    private static final String SCENARIO_FONT = "Arial-Bold";
    /**
     * Font size for subgraph labels.
     */
    private static final String SCENARIO_FONT_SIZE = "10";


    /**
     * Scenarios in plan.
     */
    private List<Scenario> scenarios;

    public PlanMapMetaProvider(
            List<Scenario> scenarios,
            String outputFormat,
            String imageDirectory,
            Analyst analyst ) {
        super( outputFormat, imageDirectory, analyst );
        this.scenarios = scenarios;
    }

    public Object getContext() {
        return scenarios;
    }

    /**
     * {@inheritDoc}
     */
    public URLProvider<Scenario, ScenarioRelationship> getURLProvider() {
        return new URLProvider<Scenario, ScenarioRelationship>() {

            public String getGraphURL( Scenario vertex ) {
                return null;
            }

            public String getVertexURL( Scenario scenario ) {
                Object[] args = {scenario.getId()};
                return MessageFormat.format( SCENARIO_URL_FORMAT, args );
            }

            public String getEdgeURL( ScenarioRelationship scRel ) {
                Object[] args = {scRel.getFromScenario().getId(), scRel.getToScenario().getId()};
                return MessageFormat.format( SCENARIO_REL_URL_FORMAT, args );
            }
        };
    }

    public DOTAttributeProvider<Scenario, ScenarioRelationship> getDOTAttributeProvider() {
        return new PlanDOTAttributeProvider();
    }

    public EdgeNameProvider<ScenarioRelationship> getEdgeLabelProvider() {
        return new EdgeNameProvider<ScenarioRelationship>() {
            public String getEdgeName( ScenarioRelationship scenarioRel ) {
                int count = scenarioRel.getExternalFlows().size();
                return count + ( count > 1 ? " flows" : " flow" );
            }
        };
    }

    public VertexNameProvider<Scenario> getVertexLabelProvider() {
        return new VertexNameProvider<Scenario>() {
            public String getVertexName( Scenario scenario ) {
                String label = separate(
                        scenario.getName(),
                        LINE_WRAP_SIZE ).replaceAll( "\\|", "\\\\n" );

                return sanitize( label );
            }
        };
    }

    public VertexNameProvider<Scenario> getVertexIDProvider() {
        return new VertexNameProvider<Scenario>() {
            public String getVertexName( Scenario scenario ) {
                return "" + scenario.getId();
            }
        };
    }

    /**
     * Plan DOT attribute provider.
     * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
     * Proprietary and Confidential.
     * User: jf
     * Date: Apr 1, 2009
     * Time: 8:37:04 PM
     */
    private class PlanDOTAttributeProvider implements DOTAttributeProvider<Scenario,ScenarioRelationship> {


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

        public List<DOTAttribute> getSubgraphAttributes() {
            return DOTAttribute.emptyList();
        }

        public List<DOTAttribute> getVertexAttributes( Scenario vertex, boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "shape", "box" ) );
            if ( highlighted ) {
                list.add( new DOTAttribute( "color", SCENARIO_COLOR ) );
                list.add( new DOTAttribute( "style", "filled" ) );
            } else {
                list.add( new DOTAttribute( "style", "solid" ) );
                list.add( new DOTAttribute( "color", "gray" ) );
            }
            list.add( new DOTAttribute( "fontsize", SCENARIO_FONT_SIZE ) );
            list.add( new DOTAttribute( "fontname", SCENARIO_FONT ) );
            if ( getAnalyst().hasIssues( vertex, Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) {
                list.add( new DOTAttribute( "fontcolor", COLOR_ERROR ) );
                list.add( new DOTAttribute( "tooltip", sanitize( getAnalyst().getIssuesSummary( vertex,
                        Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) ) );
            }
            return list;
        }

        public List<DOTAttribute> getEdgeAttributes( ScenarioRelationship edge, boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "arrowsize", "0.75" ) );
            list.add( new DOTAttribute( "fontname", EDGE_FONT ) );
            list.add( new DOTAttribute( "fontsize", EDGE_FONT_SIZE ) );
            list.add( new DOTAttribute( "fontcolor", "darkslategray" ) );
            list.add( new DOTAttribute( "len", "1.5" ) );
            list.add( new DOTAttribute( "weight", "2.0" ) );
            // Issue coloring
            if ( edge.hasIssues(getAnalyst() )) {
                list.add( new DOTAttribute( "fontcolor", COLOR_ERROR ) );
                list.add( new DOTAttribute( "color", COLOR_ERROR ) );
                list.add( new DOTAttribute( "tooltip", sanitize( edge.getIssuesSummary( getAnalyst() ) ) ));
            }
            return list;
        }
    }

}
