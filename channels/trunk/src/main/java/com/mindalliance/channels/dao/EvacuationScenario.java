package com.mindalliance.channels.dao;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.TransmissionMedium;

/**
 * The building evacuation scenario.
 */
public class EvacuationScenario extends Scenario {

    public EvacuationScenario() {
    }

    public EvacuationScenario( QueryService queryService ) {
        queryService.createPart( this );
        initialize( this, queryService );
    }

    /**
     * Add scenario contents to a given scenario
     * @param scenario the resulting scenario
     * @param queryService the query service to use
     */
    public static void initialize( Scenario scenario, QueryService queryService ) {
        scenario.setName( "Building Evacuation" );
        Part p = scenario.getDefaultPart();
        p.setActor( queryService.findOrCreate( Actor.class, "Sam Adams" ) );
        p.setTask( "supervising evacuation" );
        Flow goAhead = p.createRequirement( queryService );
        goAhead.setName( "go-ahead" );
        // goAhead.becomeCritical( );
        goAhead.becomeTriggeringToTarget();
        goAhead.addChannel( new Channel(
                queryService.findOrCreate( TransmissionMedium.class, "Phone" ),
                "800-555-4433" ) );
        Flow end = p.createOutcome( queryService );
        end.setName( "evacuation ended" );
        end.becomeTerminatingToSource();
    }
}
