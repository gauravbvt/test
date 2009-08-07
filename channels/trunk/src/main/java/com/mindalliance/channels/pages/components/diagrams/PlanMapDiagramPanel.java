package com.mindalliance.channels.pages.components.diagrams;

import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.analysis.graph.ScenarioRelationship;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.URLProvider;
import com.mindalliance.channels.graph.diagrams.PlanMapDiagram;
import com.mindalliance.channels.model.Scenario;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( PlanMapDiagramPanel.class );

    /**
     * Plan manager.
     */
    @SpringBean
    private PlanManager planManager;
    
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

    /** URL provider for imagemap links. */
    private URLProvider<Scenario, ScenarioRelationship> uRLProvider;

    public PlanMapDiagramPanel(
            String id, IModel<ArrayList<Scenario>> model, Scenario selectedScenario,
            ScenarioRelationship selectedScRel, Settings settings ) {

        this( id, model, selectedScenario, selectedScRel, null, settings );
    }

    public PlanMapDiagramPanel(
            String id, IModel<ArrayList<Scenario>> model, Scenario selectedScenario,
            ScenarioRelationship selectedScRel,
            URLProvider<Scenario, ScenarioRelationship> uRLProvider,
            Settings settings ) {

        super( id, settings );
        scenarios = model.getObject();
        this.selectedScenario = selectedScenario;
        this.selectedScRel = selectedScRel;
        this.uRLProvider = uRLProvider;
        init();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Diagram makeDiagram() {

        PlanMapDiagram diagram = new PlanMapDiagram( scenarios,
                                                     selectedScenario,
                                                     selectedScRel,
                                                     getDiagramSize(),
                                                     getOrientation() );
        diagram.setURLProvider( getURLProvider() );
        return diagram;
    }

    /**
     * Overridable imagemap link provider.
     * @return a link provider, or null for the default one.
     */
    public URLProvider<Scenario, ScenarioRelationship> getURLProvider() {
        return uRLProvider;
    }

    public void setURLProvider( URLProvider<Scenario, ScenarioRelationship> uRLProvider ) {
        this.uRLProvider = uRLProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getContainerId() {
        return "plan-map";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String makeDiagramUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append( "/plan.png?scenario=" );
        sb.append( selectedScenario == null ? "NONE" : selectedScenario.getId() );
        sb.append( "&connection=" );
        sb.append( selectedScRel == null ? "NONE" : selectedScRel.getId() );
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
    @Override
    protected void onClick( AjaxRequestTarget target ) {
        update( target, new Change( Change.Type.Selected, planManager.getCurrentPlan() ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
    protected void onSelectVertex(
            String graphId,
            String vertexId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            AjaxRequestTarget target ) {
        try {
            String js = scroll( domIdentifier, scrollTop, scrollLeft );
            Scenario scenario = getQueryService().find( Scenario.class, Long.valueOf( vertexId ) );
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
    @Override
    protected void onSelectEdge(
            String graphId,
            String edgeId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            AjaxRequestTarget target ) {
        ScenarioRelationship scRel = new ScenarioRelationship();
        scRel.setId( Long.valueOf( edgeId ), getQueryService() );
        String js = scroll( domIdentifier, scrollTop, scrollLeft );
        Change change = new Change( Change.Type.Selected, scRel );
        change.setScript( js );
        update( target, change );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String makeSeed() {
        // Force regeneration
        return "&_modified=" + System.currentTimeMillis();
    }

}
