package com.mindalliance.channels.pages.components.diagrams;

import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.analysis.graph.ScenarioRelationship;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.graph.Diagram;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.text.MessageFormat;

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
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( PlanMapDiagramPanel.class );
    /**
     * List of scenarios to be mapped.
     */
    private List<Scenario> scenarios;
    /**
     * Selected scenario.
     */
    private Scenario selectedScenario;
    /**
     * Selected scenario releationship.
     */
    private ScenarioRelationship selectedScRel;

    public PlanMapDiagramPanel(
            String id,
            IModel<ArrayList<Scenario>> model,
            Scenario selectedScenario,
            ScenarioRelationship selectedScRel,
            String domIdentifier ) {
        this( id, model, selectedScenario, selectedScRel, null, null, true, domIdentifier );
    }

    public PlanMapDiagramPanel(
            String id,
            IModel<ArrayList<Scenario>> model,
            Scenario selectedScenario,
            ScenarioRelationship selectedScRel,
            double[] diagramSize,
            String orientation,
            boolean withImageMap,
            String domIdentifier ) {
        super( id, diagramSize, orientation, withImageMap, domIdentifier );
        scenarios = model.getObject();
        this.selectedScenario = selectedScenario;
        this.selectedScRel = selectedScRel;
        init();
    }


    /**
     * {@inheritDoc}
     */
    protected Diagram makeDiagram(double[] diagramSize, String orientation ) {
        return getDiagramFactory().newPlanMapDiagram(
                scenarios,
                selectedScenario,
                selectedScRel,
                diagramSize,
                orientation);
    }

    /**
     * {@inheritDoc}
     */
    protected String getContainerId() {
        return "plan-map";
    }

    /**
     * {@inheritDoc}
     */
    protected String makeDiagramUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append( "plan.png?scenario=" );
        sb.append( selectedScenario == null ? "NONE" : selectedScenario.getId() );
        sb.append( "&connection=" );
        sb.append( selectedScRel == null ? "NONE" : selectedScRel.getId() );
        sb.append( "&time=" );
        sb.append( MessageFormat.format( "{2,number,0}", System.currentTimeMillis() ) );
        double[] diagramSize = getDiagramSize();
        if ( diagramSize != null ) {
            sb.append( "&size=" );
            sb.append( diagramSize[0] );
            sb.append( "," );
            sb.append( diagramSize[1] );
        }
        String orientation = getOrientation();
        if ( orientation != null ) {
            sb.append( "&orientation=" );
            sb.append( orientation );
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc }
     */
    protected void onClick( AjaxRequestTarget target ) {
        update( target, new Change( Change.Type.Selected, Project.getProject() ) );
    }

    /**
     * {@inheritDoc}
     */
    protected void onSelectGraph(
            String graphId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            AjaxRequestTarget target ) {
        // Do nothing -- never called
    }

    /**
     * {@inheritDoc}
     */
    protected void onSelectVertex(
            String graphId,
            String vertexId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            AjaxRequestTarget target ) {
        try {
            String js = scroll( domIdentifier, scrollTop, scrollLeft );
            Scenario scenario = getDqo().find( Scenario.class, Long.valueOf( vertexId ) );
            Change change = new Change( Change.Type.Selected, scenario );
            change.setScript( js );
            update( target, change );
        } catch ( NotFoundException e ) {
            LOG.warn( "Nout found", e );
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void onSelectEdge(
            String graphId,
            String edgeId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            AjaxRequestTarget target ) {
        ScenarioRelationship scRel = new ScenarioRelationship();
        scRel.setId( Long.valueOf( edgeId ), getDqo() );
        String js = scroll( domIdentifier, scrollTop, scrollLeft );
        Change change = new Change( Change.Type.Selected, scRel );
        change.setScript( js );
        update( target, change );
    }
}
