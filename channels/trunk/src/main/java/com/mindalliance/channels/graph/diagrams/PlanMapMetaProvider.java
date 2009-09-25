package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.analysis.graph.ScenarioRelationship;
import com.mindalliance.channels.graph.AbstractMetaProvider;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.DOTAttributeProvider;
import com.mindalliance.channels.graph.URLProvider;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Scenario;
import org.apache.commons.lang.StringUtils;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.springframework.core.io.Resource;

import java.text.MessageFormat;
import java.util.List;

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
     * Color for subgraph contour
     */
    private static final String SCENARIO_COLOR = "azure2";
    /**
     * Color for subgraph contour
     */
    private static final String SELECTED_SCENARIO_COLOR = "azure4";
    /**
     * Font for subgraph labels.
     */
    private static final String SCENARIO_FONT = "Arial-Bold";
    /**
     * Font size for subgraph labels.
     */
    private static final String SCENARIO_FONT_SIZE = "10";

    /**
     * Color for subgraph contour
     */
    private static final String SUBGRAPH_COLOR = "gray94";
    /**
     * Color for selected subgraph contour
     */
    private static final String SELECTED_SUBGRAPH_COLOR = "gray85";
    /**
     * Font for subgraph labels.
     */
    private static final String SUBGRAPH_FONT = "Arial-Bold";
    /**
     * Font size for subgraph labels.
     */
    private static final String SUBGRAPH_FONT_SIZE = "10";
    /**
     * Whether to group by phase.
     */
    private boolean groupByPhase;
    /**
     * Whether to group by event.
     */
    private boolean groupByEvent;

    /**
     * The thing that generates URLs in an image map.
     */
    private URLProvider<Scenario, ScenarioRelationship> uRLProvider;

    /**
     * Scenarios in plan.
     */
    private List<Scenario> scenarios;
    private ModelObject selectedGroup;

    public PlanMapMetaProvider(
            List<Scenario> scenarios,
            String outputFormat,
            Resource imageDirectory,
            Analyst analyst ) {

        super( outputFormat, imageDirectory, analyst );
        this.scenarios = scenarios;
    }

    public Object getContext() {
        return scenarios;
    }

    public boolean isGroupByPhase() {
        return groupByPhase;
    }

    public void setGroupByPhase( boolean groupByPhase ) {
        this.groupByPhase = groupByPhase;
    }

    public boolean isGroupByEvent() {
        return groupByEvent;
    }

    public void setGroupByEvent( boolean groupByEvent ) {
        this.groupByEvent = groupByEvent;
    }

    public void setURLProvider( URLProvider<Scenario, ScenarioRelationship> uRLProvider ) {
        this.uRLProvider = uRLProvider;
    }

    /**
     * {@inheritDoc}
     */
    public URLProvider<Scenario, ScenarioRelationship> getURLProvider() {
        if ( uRLProvider == null )
            uRLProvider = new DefaultURLProvider();

        return uRLProvider;
    }

    public DOTAttributeProvider<Scenario, ScenarioRelationship> getDOTAttributeProvider() {
        return new PlanDOTAttributeProvider();
    }

    public EdgeNameProvider<ScenarioRelationship> getEdgeLabelProvider() {
        return new EdgeNameProvider<ScenarioRelationship>() {
            public String getEdgeName( ScenarioRelationship scenarioRel ) {
                String name = "";
                if ( scenarioRel.hasExternalFlows() ) {
                    int count = scenarioRel.getExternalFlows().size();
                    name = name + count + ( count > 1 ? " flows" : " flow" );
                }
                if ( scenarioRel.hasInitiators() ) {
                    name += name.isEmpty() ? " " : " and ";
                    int count = scenarioRel.getInitiators().size();
                    name = name + count + ( count > 1 ? " triggers" : " trigger" );
                }
                if ( scenarioRel.hasTerminators() ) {
                    name += name.isEmpty() ? " " : " and ";
                    int count = scenarioRel.getTerminators().size();
                    name = name + count + ( count > 1 ? " terminations" : " termination" );
                }
                return name;
            }
        };
    }

    public VertexNameProvider<Scenario> getVertexLabelProvider() {
        return new VertexNameProvider<Scenario>() {
            public String getVertexName( Scenario scenario ) {
                String label = AbstractMetaProvider.separate(
                        scenarioLabel( scenario ),
                        LINE_WRAP_SIZE ).replaceAll( "\\|", "\\\\n" );

                return sanitize( label );
            }
        };
    }

    private String scenarioLabel( Scenario scenario ) {
        StringBuilder sb = new StringBuilder();
        sb.append( scenario.getName() );
        sb.append( "|(" );
        if ( groupByPhase ) {
            sb.append( scenario.getEvent().getName() );
        } else if ( groupByEvent ) {
            sb.append( scenario.getPhase().getName() );
        } else {
            sb.append( scenario.getPhaseEventTitle() );
        }
        sb.append( ')' );
        return sb.toString();
    }

    public VertexNameProvider<Scenario> getVertexIDProvider() {
        return new VertexNameProvider<Scenario>() {
            public String getVertexName( Scenario scenario ) {
                return String.valueOf( scenario.getId() );
            }
        };
    }

    public void setSelectedGroup( ModelObject selectedGroup ) {
        this.selectedGroup = selectedGroup;
    }

    public ModelObject getSelectedGroup() {
        return selectedGroup;
    }

    /**
     * Plan DOT attribute provider.
     * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
     * Proprietary and Confidential.
     * User: jf
     * Date: Apr 1, 2009
     * Time: 8:37:04 PM
     */
    private class PlanDOTAttributeProvider implements DOTAttributeProvider<Scenario, ScenarioRelationship> {


        public List<DOTAttribute> getGraphAttributes() {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "rankdir", getGraphOrientation() ) );
            if ( getGraphSize() != null ) {
                list.add( new DOTAttribute( "size", getGraphSizeString() ) );
                list.add( new DOTAttribute( "ratio", "compress" ) );
            }
            return list;
        }

        public List<DOTAttribute> getSubgraphAttributes( boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "fontcolor", FONTCOLOR ) );
            list.add( new DOTAttribute( "fontsize", SUBGRAPH_FONT_SIZE ) );
            list.add( new DOTAttribute( "fontname", SUBGRAPH_FONT ) );
            if ( highlighted ) {
                list.add( new DOTAttribute( "color", SELECTED_SUBGRAPH_COLOR ) );
            } else {
                list.add( new DOTAttribute( "color", SUBGRAPH_COLOR ) );
            }
            list.add( new DOTAttribute( "style", "filled" ) );
            return list;
        }

        public List<DOTAttribute> getVertexAttributes( Scenario vertex, boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "shape", "box" ) );
            list.add( new DOTAttribute( "color", SCENARIO_COLOR ) );
            list.add( new DOTAttribute( "style", "filled" ) );
            if ( highlighted ) {
                list.add( new DOTAttribute( "color", SELECTED_SCENARIO_COLOR ) );
            } else {
                list.add( new DOTAttribute( "color", SCENARIO_COLOR ) );
            }
            list.add( new DOTAttribute( "fontsize", SCENARIO_FONT_SIZE ) );
            list.add( new DOTAttribute( "fontname", SCENARIO_FONT ) );
            list.add( new DOTAttribute(
                    "tooltip",
                    sanitize( StringUtils.abbreviate( vertex.getDescription(), 50 ) ) ) );
            if ( getAnalyst().hasUnwaivedIssues( vertex, Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) {
                list.add( new DOTAttribute( "fontcolor", COLOR_ERROR ) );
                list.add( new DOTAttribute( "tooltip", sanitize( getAnalyst().getIssuesSummary( vertex,
                        Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) ) );
            }
            return list;
        }

        public List<DOTAttribute> getEdgeAttributes( ScenarioRelationship edge, boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "arrowhead", "vee" ) );
            list.add( new DOTAttribute( "arrowsize", "0.75" ) );
            list.add( new DOTAttribute( "fontname", EDGE_FONT ) );
            list.add( new DOTAttribute( "fontsize", EDGE_FONT_SIZE ) );
            list.add( new DOTAttribute( "fontcolor", "darkslategray" ) );
            list.add( new DOTAttribute( "len", "1.5" ) );
            list.add( new DOTAttribute( "weight", "2.0" ) );
            if ( highlighted ) {
                list.add( new DOTAttribute( "penwidth", "3.0" ) );
            }
            // Issue coloring
            if ( edge.hasIssues( getAnalyst() ) ) {
                list.add( new DOTAttribute( "fontcolor", COLOR_ERROR ) );
                list.add( new DOTAttribute( "color", COLOR_ERROR ) );
                list.add( new DOTAttribute( "tooltip", sanitize( edge.getIssuesSummary( getAnalyst() ) ) ) );
            }
            return list;
        }
    }

    public class DefaultURLProvider implements URLProvider<Scenario, ScenarioRelationship> {

        public String getGraphURL( Scenario vertex ) {
            if ( isGroupByPhase() ) {
                Object[] args = {vertex.getPhase().getId()};
                return MessageFormat.format( GRAPH_URL_FORMAT, args );
            } else if ( isGroupByEvent() ) {
                Object[] args = {vertex.getEvent().getId()};
                return MessageFormat.format( GRAPH_URL_FORMAT, args );
            } else {
                return null;
            }
        }

        public String getVertexURL( Scenario vertex ) {
            return MessageFormat.format( VERTEX_URL_FORMAT, 0, vertex.getId() );
        }

        public String getEdgeURL( ScenarioRelationship edge ) {
            return MessageFormat.format( EDGE_URL_FORMAT, 0, edge.getId() );
        }
    }
}
