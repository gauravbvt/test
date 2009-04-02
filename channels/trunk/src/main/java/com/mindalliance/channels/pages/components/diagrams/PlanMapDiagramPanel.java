package com.mindalliance.channels.pages.components.diagrams;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.Scenario;

import java.util.ArrayList;

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

    public PlanMapDiagramPanel( String id, IModel<ArrayList<Scenario>> model ) {
        super( id, model );
    }

    public PlanMapDiagramPanel(
            String id,
            IModel<ArrayList<Scenario>> model,
            double[] diagramSize,
            String orientation,
            boolean withImageMap ) {
        super( id, model, diagramSize, orientation, withImageMap );
    }


    protected Diagram makeDiagram() {
        return getDiagramFactory().newPlanMapDiagram( (ArrayList<Scenario>)getModel().getObject() );
    }

    protected String makeDiagramUrl() {
        return null;
    }
}
