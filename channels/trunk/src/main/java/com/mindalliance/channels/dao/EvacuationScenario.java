package com.mindalliance.channels.dao;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Dao;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;

/**
 * The building evacuation scenario.
 */
public class EvacuationScenario extends Scenario {

    public EvacuationScenario( Dao dao ) {
        setDao( dao );
        setName( "Building Evacuation" );

        final Part p = getDefaultPart();
        p.setActor( new Actor( "Sam Adams" ) );
        p.setTask( "supervising evacuation" );

        final Flow goAhead = p.createRequirement();
        goAhead.setName( "go-ahead" );
        goAhead.setCritical( true );
        goAhead.setChannel( "phone: 555-4433" );

        final Flow end = p.createOutcome();
        end.setName( "end" );
    }
}
