package com.mindalliance.channels.dao;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.Channel;
import com.mindalliance.channels.NotFoundException;

import javax.persistence.Entity;

/**
 * The building evacuation scenario.
 */
@Entity
public class EvacuationScenario extends Scenario {

    public EvacuationScenario() {
    }

    public EvacuationScenario( Service service ) throws NotFoundException {
        setName( "Building Evacuation" );

        final Part p = getDefaultPart();
        p.setActor( service.findOrCreate( Actor.class, "Sam Adams" ) );
        p.setTask( "supervising evacuation" );

        final Flow goAhead = p.createRequirement( service );
        goAhead.setName( "go-ahead" );
        goAhead.setCritical( true );
        goAhead.addChannel( new Channel(service.mediumNamed("Phone"), "800-555-4433" ) );

        final Flow end = p.createOutcome( service );
        end.setName( "end" );
    }
}
