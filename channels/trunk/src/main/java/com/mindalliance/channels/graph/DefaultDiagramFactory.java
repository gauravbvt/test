package com.mindalliance.channels.graph;

import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Node;

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
     * 0: scenario id, 1: node id
     */
    private String urlFormat;
    /**
     * 0: scenario id
     */
    private String scenarioUrlFormat;
    /**
     * Path to image directory
     */
    private String imageDirectory;

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

    public String getUrlFormat() {
        return urlFormat;
    }

    public void setUrlFormat( String urlFormat ) {
        this.urlFormat = urlFormat;
    }

    public String getScenarioUrlFormat() {
        return scenarioUrlFormat;
    }

    public void setScenarioUrlFormat( String scenarioUrlFormat ) {
        this.scenarioUrlFormat = scenarioUrlFormat;
    }

    public String getImageDirectory() {
        return imageDirectory;
    }

    public void setImageDirectory( String imageDirectory ) {
        this.imageDirectory = imageDirectory;
    }

    /**
     * {@inheritDoc}
     */
    public FlowDiagram newFlowDiagram( Scenario scenario, Node node ) {
        return new DefaultFlowDiagram( scenario, node, this );
    }

    /**
     * {@inheritDoc}
     */
    public FlowDiagram newFlowDiagram( Scenario scenario ) {
        return new DefaultFlowDiagram( scenario, scenario.getDefaultPart(), this );
    }
}
