package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.analysis.graph.ScenarioRelationship;
import com.mindalliance.channels.graph.Diagram;
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
        DataQueryObject dqo = getDqo();
        if ( parameters.containsKey( "scenario" ) && !parameters.getString( "scenario" ).equals( "NONE" ) ) {
            Long scenarioId = Long.valueOf(parameters.getString( "scenario" ));
            try {
                scenario = dqo.find(Scenario.class, scenarioId);
            } catch ( NotFoundException e ) {
                LOG.warn("Scenario not found at :" + scenarioId, e);
            }
        }
        if ( parameters.containsKey( "connection" ) && !parameters.getString( "connection" ).equals( "NONE" ) ) {
            Long scRelId = Long.valueOf(parameters.getString( "connection" ));
            scRel = new ScenarioRelationship();
            scRel.setId( scRelId, getDqo() );
        }
        allScenarios = dqo.list( Scenario.class );
    }

       /**
     * {@inheritDoc}
     */
    protected Diagram makeDiagram( double[] size, String orientation ) {
       return getDiagramFactory().newPlanMapDiagram( allScenarios, scenario, scRel, size, orientation );
    }
}
