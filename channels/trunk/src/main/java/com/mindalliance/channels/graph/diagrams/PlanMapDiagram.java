package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.DiagramFactory;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.analysis.graph.PlanMapGraphBuilder;
import com.mindalliance.channels.analysis.graph.ScenarioRelationship;
import com.mindalliance.channels.graph.AbstractDiagram;
import com.mindalliance.channels.graph.GraphRenderer;
import com.mindalliance.channels.graph.URLProvider;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Scenario;
import org.jgrapht.DirectedGraph;
import org.springframework.core.io.Resource;

import java.io.OutputStream;
import java.util.List;

/**
 * A plan map diagram.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 1, 2009
 * Time: 1:58:47 PM
 */
public class PlanMapDiagram extends AbstractDiagram<Scenario,ScenarioRelationship> {

    /**
     * The scenarios mapped.
     */
    private List<Scenario> scenarios;
    /**
     * Phase or event grouping scenarios.
     */
    private ModelObject selectedGroup;
    /**
     * Whether to group scenarios by phases.
     */
    private boolean groupByPhase;
    /**
     * Whether to group scenarios by events.
     */
    private boolean groupByEvent;
    /**
     * Selected vertex-scenario.
     */
    private Scenario selectedScenario;

    /**
     * Selected edge-scenario relationship.
     */
    private ScenarioRelationship selectedScRel;

    /** Provider of imagemap links. */
    private URLProvider<Scenario, ScenarioRelationship> uRLProvider;

    public PlanMapDiagram(
            List<Scenario> scenarios,
            boolean groupByPhase,
            boolean groupByEvent,
            ModelObject selectedGroup,
            Scenario scenario,
            ScenarioRelationship scRel,
            double[] diagramSize,
            String orientation ) {

        super( diagramSize, orientation );
        this.scenarios = scenarios;
        this.selectedGroup = selectedGroup;
        this.groupByPhase = groupByPhase;
        this.groupByEvent = groupByEvent;
        selectedScenario = scenario;
        selectedScRel = scRel;
    }

    /** {@inheritDoc} */
    public void render( String outputFormat, OutputStream outputStream ) {
        DiagramFactory<Scenario, ScenarioRelationship> factory = getDiagramFactory();
        GraphRenderer<Scenario, ScenarioRelationship> renderer = factory.getGraphRenderer();

        renderer.highlight( selectedScenario, selectedScRel );
        renderer.render(
                createGraph( factory.getQueryService() ),
                createExporter( outputFormat, factory.getImageDirectory() ), outputFormat,
                outputStream );
    }

    private DirectedGraph<Scenario, ScenarioRelationship> createGraph( QueryService queryService ) {
        return new PlanMapGraphBuilder( scenarios, queryService ).buildDirectedGraph();
    }

    /**
     * Provide an overridable provider for imagemaps links.
     * @return the URL provider, or null to use the default one.
     */
    public URLProvider<Scenario,ScenarioRelationship> getURLProvider() {
        return uRLProvider;
    }

    public void setURLProvider( URLProvider<Scenario, ScenarioRelationship> uRLProvider ) {
        this.uRLProvider = uRLProvider;
    }

    private PlanMapDOTExporter createExporter( String outputFormat, Resource imageDirectory ) {

        PlanMapMetaProvider metaProvider =
            new PlanMapMetaProvider( scenarios, outputFormat, imageDirectory, getAnalyst() );
        metaProvider.setGroupByPhase( groupByPhase );
        metaProvider.setGroupByEvent( groupByEvent );
        metaProvider.setSelectedGroup( selectedGroup );
        metaProvider.setURLProvider( getURLProvider() );

        double[] diagramSize = getDiagramSize();
        if ( diagramSize != null )
            metaProvider.setGraphSize( diagramSize );

        String orientation = getOrientation();
        if ( orientation != null )
            metaProvider.setGraphOrientation( orientation );

        return new PlanMapDOTExporter( metaProvider );
    }
}
