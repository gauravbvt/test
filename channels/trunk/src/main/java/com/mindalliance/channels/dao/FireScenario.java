package com.mindalliance.channels.dao;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Connector;
import com.mindalliance.channels.Dao;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.UserIssue;
import com.mindalliance.channels.Delay;

/**
 * The fire in the building scenario...
 *
 * @todo Move to test package when far enough along
 */
public class FireScenario extends Scenario {

    public FireScenario( Dao dao, EvacuationScenario evac ) {
        setDao( dao );

        setName( "Fire in the building" );
        setDescription( "A fire happens" );

        final Actor joe = dao.findOrMakeActor( "Joe Smith" );
        final Part js1 = getDefaultPart();
        js1.setActor( joe );
        js1.setTask( "investigating fire" );
        js1.setRole( dao.findOrMakeRole( "Fire Warden" ) );
        js1.setDescription( "Fire alarms must be investigated carefully." );

        final Part js2 = createPart( joe, "monitoring evacuation" );
        final Part tenant = createPart( dao.findOrMakeRole( "Tenant" ), "noticing fire" );
        final Part chief = createPart( dao.findOrMakeRole( "Fire Chief" ), "supervising operations" );

        final Actor system = dao.findOrMakeActor( "Fire Alarm" );
        final Part alarm = createPart( system, "ringing" );
        UserIssue issue = new UserIssue( alarm );
        issue.setDescription( "Hearing-challenged tenants may not hear the alarm.");
        issue.setRemediation( "Add flashing light signal.");
        issue.setReportedBy( "jdoe" );
        dao.addUserIssue( issue );
        alarm.setRole( dao.findOrMakeRole( "System" ) );

        final Part fd = createPart();
        fd.setOrganization( dao.findOrMakeOrganization( "Fire Department" ) );
        fd.setTask( "responding" );

        connect( tenant, alarm );
        final Flow f1 = connect( alarm, js1 );
        f1.setName( "location" );
        f1.setAskedFor( true );
        f1.setChannel( "wall panel" );
        f1.setDescription( "The fire location reported by the system" );

        final Flow f2 = connect( js1, chief );
        f2.setName( "fire location" );
        f2.setAskedFor( true );
        f2.setCritical( true );
        f2.setDescription( "Communicate the location of the fire" );

        final Flow f3 = connect( chief, js1 );
        f3.setName( "stairways safe" );
        f3.setMaxDelay( new Delay(10, Delay.Unit.minutes) );
        f3.setCritical( true );
        f3.setChannel( "Radio" );
        f3.setDescription( "Confirms that stairways are safe for evacuation" );

        final Flow f4 = connect( js2, chief );
        f4.setName( "evacuation status" );
        f4.setAskedFor( true );
        f4.setCritical( true );

        final Connector ga = evac.inputs().next();
        /*final Flow f5 = */
        connect( js1, ga );
        // f5.setCritical( true );

        final Connector end = evac.outputs().next();
        final Flow f6 = connect( end, js2 );
        f6.setCritical( true );

        connect( alarm, fd ).setName( "address" );
        connect( fd, chief );

        chief.createOutcome().setName( "\"all-clear\"" );
    }
}
