package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.analysis.graph.ScenarioRelationship;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Scenario;
import org.apache.wicket.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Generation of the plan map PNG.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 3, 2009
 * Time: 4:26:10 PM
 */
public class PlanMapPage extends PngWebPage {
    /**
     * The log.
     */
    private static final Logger LOG = LoggerFactory.getLogger( PlanMapPage.class );
    /**
     * The selected phase or event.
     */
    private ModelEntity group;
    /**
     * Group scenarios by phase.
     */
    private boolean groupByPhase;
    /**
     * Group scenarios by event.
     */
    private boolean groupByEvent;
    /**
     * The selected scenario.
     */
    private Scenario scenario;
    /**
     * The selected scenario relationship.
     */
    private ScenarioRelationship scRel;
    /**
     * All scenarios in plan.
     */
    private List<Scenario> allScenarios;

    public PlanMapPage( PageParameters parameters ) {
        super( parameters );
        QueryService queryService = getQueryService();
        if ( parameters.containsKey( "groupby" ) && !parameters.getString( "groupby" ).equals( "NONE" ) ) {
            String groupBy = parameters.getString( "groupby" );
            groupByPhase = groupBy.equals( "phase" );
            groupByEvent = groupBy.equals( "event" );
        }
        if ( parameters.containsKey( "group" ) && !parameters.getString( "group" ).equals( "NONE" ) ) {
            Long groupId = Long.valueOf( parameters.getString( "group" ) );
            try {
                group = queryService.find( ModelEntity.class, groupId );
            } catch ( NotFoundException e ) {
                LOG.warn( "Phase or event group not found at :" + groupId, e );
            }
        }
        if ( parameters.containsKey( "scenario" ) && !parameters.getString( "scenario" ).equals( "NONE" ) ) {
            Long scenarioId = Long.valueOf( parameters.getString( "scenario" ) );
            try {
                scenario = queryService.find( Scenario.class, scenarioId );
            } catch ( NotFoundException e ) {
                LOG.warn( "Scenario not found at :" + scenarioId, e );
            }
        }
        if ( parameters.containsKey( "connection" ) && !parameters.getString( "connection" ).equals( "NONE" ) ) {
            Long scRelId = Long.valueOf( parameters.getString( "connection" ) );
            scRel = new ScenarioRelationship();
            scRel.setId( scRelId, getQueryService() );
        }
        allScenarios = queryService.list( Scenario.class );
    }

    /**
     * {@inheritDoc}
     */
    protected Diagram makeDiagram( double[] size, String orientation ) {
        return getDiagramFactory().newPlanMapDiagram(
                allScenarios,
                groupByPhase,
                groupByEvent,
                group,
                scenario,
                scRel,
                size,
                orientation );
    }
}
