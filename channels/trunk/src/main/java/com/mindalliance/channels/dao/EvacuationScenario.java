package com.mindalliance.channels.dao;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Channel;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Medium;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.DataQueryObject;

/**
 * The building evacuation scenario.
 */
public class EvacuationScenario extends Scenario {

    public EvacuationScenario() {
    }

    public EvacuationScenario( DataQueryObject dqo ) {
        dqo.createPart( this );
        initialize( this, dqo );
    }

    /**
     * Add scenario contents to a given scenario
     * @param scenario the resulting scenario
     * @param dqo the data query object to use
     */
    public static void initialize( Scenario scenario, DataQueryObject dqo ) {
        scenario.setName( "Building Evacuation" );
        Part p = scenario.getDefaultPart();
        p.setActor( dqo.findOrCreate( Actor.class, "Sam Adams" ) );
        p.setTask( "supervising evacuation" );
        Flow goAhead = p.createRequirement( dqo );
        goAhead.setName( "go-ahead" );
        // goAhead.becomeCritical( );
        goAhead.becomeTriggeringToTarget();
        goAhead.addChannel( new Channel( Medium.Phone, "800-555-4433" ) );
        Flow end = p.createOutcome( dqo );
        end.setName( "evacuation ended" );
        end.becomeTerminatingToSource();
    }
}
