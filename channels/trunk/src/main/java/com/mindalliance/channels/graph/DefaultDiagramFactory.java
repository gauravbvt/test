package com.mindalliance.channels.graph;

import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.DiagramFactory;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.analysis.graph.ScenarioRelationship;
import com.mindalliance.channels.analysis.graph.EntityRelationship;
import com.mindalliance.channels.graph.diagrams.FlowMapDiagram;
import com.mindalliance.channels.graph.diagrams.PlanMapDiagram;
import com.mindalliance.channels.graph.diagrams.EntityNetworkDiagram;

import java.util.List;

/**
 * The default implementation of DiagramFactory
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 11, 2009
 * Time: 3:15:10 PM
 * @param <V> a vertex class
 * @param <E> an edge class
 */
public class DefaultDiagramFactory<V, E> implements DiagramFactory {

    /**
     * A GraphRenderer for nodes and flows
     */
    private GraphRenderer<V, E> graphRenderer;
    /**
     * Path to image directory
     */
    private String imageDirectory;
    /**
     * Data query object.
     */
    private DataQueryObject dqo;

    public DefaultDiagramFactory() {
    }

    public void setGraphRenderer( GraphRenderer<V, E> graphRenderer ) {
        this.graphRenderer = graphRenderer;
    }

    /**
     * {@inheritDoc}
     */
    public GraphRenderer<V, E> getGraphRenderer() {
        return graphRenderer;
    }

    public String getImageDirectory() {
        return imageDirectory;
    }

    public void setImageDirectory( String imageDirectory ) {
        this.imageDirectory = imageDirectory;
    }

    public DataQueryObject getDqo() {
        return dqo;
    }

    public void setDqo( DataQueryObject dqo ) {
        this.dqo = dqo;
    }

    /**
     * {@inheritDoc}
     */
    public Diagram newFlowMapDiagram(
            Scenario scenario,
            Node node,
            double[] diagramSize,
            String orientation ) {
        return new FlowMapDiagram( scenario, node, diagramSize, orientation );
    }

    public Diagram newEntityNetworkDiagram(
            ModelObject entity,
            EntityRelationship selectedEntityRel,
            double[] diagramSize,
            String orientation ) {
        return new EntityNetworkDiagram( entity, selectedEntityRel, diagramSize, orientation );
    }

    /**
      * {@inheritDoc}
      */
    // TODO - why can't I say:  List<Scenario> scenarios ?
    public Diagram newPlanMapDiagram(
            List scenarios,
            Scenario scenario,
            ScenarioRelationship scRel,
            double[] diagramSize,
            String orientation ) {
        return new PlanMapDiagram( 
                (List<Scenario>)scenarios,
                scenario, scRel,
                diagramSize,
                orientation );
    }

}
