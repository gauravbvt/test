package com.mindalliance.channels.dao;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Channel;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Medium;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Service;

/**
 * The building evacuation scenario.
 */
public class EvacuationScenario extends Scenario {

    public EvacuationScenario() {
    }

    public EvacuationScenario( Service service ) {
        service.createPart( this );
        initialize( this, service );
    }

    /**
     * Add scenario contents to a given scenario
     * @param scenario the resulting scenario
     * @param service the service to use
     */
    public static void initialize( Scenario scenario, Service service ) {
        scenario.setName( "Building Evacuation" );
        Part p = scenario.getDefaultPart();
        p.setActor( service.findOrCreate( Actor.class, "Sam Adams" ) );
        p.setTask( "supervising evacuation" );
        Flow goAhead = p.createRequirement( service );
        goAhead.setName( "go-ahead" );
        goAhead.becomeCritical( );
        goAhead.addChannel( new Channel( Medium.Phone, "800-555-4433" ) );
        Flow end = p.createOutcome( service );
        end.setName( "end" );
    }
}
