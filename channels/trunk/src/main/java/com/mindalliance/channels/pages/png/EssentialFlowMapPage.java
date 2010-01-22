package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.ScenarioObject;
import com.mindalliance.channels.pages.PlanPage;
import org.apache.wicket.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PNG view of an essential flow map.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 20, 2010
 * Time: 7:56:20 PM
 */
public class EssentialFlowMapPage extends PngWebPage {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( EssentialFlowMapPage.class );
    /**
     * Parameter.
     */
    public static final String FAILURE = "failure";
    /**
     * Parameter.
     */
    public static final String ASSUME_FAILS = "assume_fails";

    /**
     * The hypothetical failure.
     */
    private ScenarioObject scenarioObject;
    private boolean assumeFails;

    public EssentialFlowMapPage( PageParameters parameters ) {
        super( parameters );
        QueryService queryService = getQueryService();
        Scenario scenario = PlanPage.findScenario( queryService, parameters );
        if ( scenario != null && parameters.containsKey( FAILURE ) ) {
            try {
                long id = parameters.getLong( FAILURE );
                try {
                    scenarioObject = scenario.findFlow( id );
                } catch ( NotFoundException e ) {
                    // ignore
                }
                if ( scenarioObject == null )
                    scenarioObject = scenario.getNode( id );
            } catch ( Exception ignored ) {
                LOG.warn( "Invalid failed scenario object specified in parameters." );
            }
        }
        assumeFails = parameters.getAsBoolean( ASSUME_FAILS, false );
    }

    /**
     * {@inheritDoc}
     */
    protected Diagram makeDiagram( double[] size, String orientation ) throws DiagramException {
        if ( scenarioObject == null )
            throw new DiagramException( "Can't find failed scenario object" );
        else
            return getDiagramFactory().newEssentialFlowMapDiagram(
                    scenarioObject,
                    assumeFails,
                    size,
                    orientation );
    }
}
