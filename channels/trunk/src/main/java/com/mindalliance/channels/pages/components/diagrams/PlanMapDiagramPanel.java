package com.mindalliance.channels.pages.components.diagrams;

import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.graph.Diagram;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Plan map diagram panel.
 * Ajax-enabled.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 31, 2009
 * Time: 5:36:00 PM
 */
public class PlanMapDiagramPanel extends AbstractDiagramPanel {

    /**
     * List of scenarios to be mapped.
     */
    private List<Scenario> scenarios;

    public PlanMapDiagramPanel( String id, IModel<ArrayList<Scenario>> model ) {
        super( id );
        scenarios = model.getObject();
    }

    public PlanMapDiagramPanel(
            String id,
            IModel<ArrayList<Scenario>> model,
            double[] diagramSize,
            String orientation,
            boolean withImageMap ) {
        super( id, diagramSize, orientation, withImageMap );
        scenarios = model.getObject();
    }


    protected Diagram makeDiagram() {
        return getDiagramFactory().newPlanMapDiagram( scenarios );
    }

    protected String makeDiagramUrl() {
        return null;
    }

    protected void onSelectGraph( String graphId, AjaxRequestTarget target ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    protected void onSelectVertex( String graphId, String vertexId, AjaxRequestTarget target ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    protected void onSelectEdge( String graphId, String edgeId, AjaxRequestTarget target ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
