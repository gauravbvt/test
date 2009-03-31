package com.mindalliance.channels.dao;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Channel;
import com.mindalliance.channels.Delay;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.Medium;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.UserIssue;

/**
 * The fire in the building scenario...
 *
 * @todo Move to test package when far enough along
 */
public class FireScenario extends Scenario {

    public FireScenario() {
    }

    public FireScenario( DataQueryObject dqo, Scenario evac ) {
        dqo.createPart( this );
        initialize( this, dqo, evac );
    }

    /**
     * Initialize a scenario to contents of "Fire in the building".
     * @param scenario the scenario, preferably empty
     * @param dqo the data query object
     * @param evac the "Building Evacuation" scenario
     */
    public static void initialize( Scenario scenario, DataQueryObject dqo, Scenario evac ) {
        scenario.setName( "Fire in the building" );
        scenario.setDescription( "A fire happens" );
        Actor joe = dqo.findOrCreate( Actor.class, "Joe Smith" );
        Part js1 = scenario.getDefaultPart();
        js1.setActor( joe );
        js1.setTask( "investigating fire" );
        js1.setRole( dqo.findOrCreate( Role.class, "Fire Warden" ) );
        js1.setDescription( "Fire alarms must be investigated carefully." );
        Part js2 = scenario.createPart( dqo, joe, "monitoring evacuation" );
        Part tenant = scenario.createPart( dqo,
                                  dqo.findOrCreate( Role.class, "Tenant" ), "noticing fire" );
        Part chief = scenario.createPart( dqo,
                                 dqo.findOrCreate( Role.class, "Fire Chief" ),
                                 "supervising operations" );
        Part alarm = scenario.createPart( dqo,
                                 dqo.findOrCreate( Actor.class, "Fire Alarm" ), "ringing" );
        alarm.setRole( dqo.findOrCreate( Role.class, "System" ) );
        UserIssue issue = new UserIssue( alarm );
        issue.setDescription( "Hearing-challenged tenants may not hear the alarm." );
        issue.setRemediation( "Add flashing light signal." );
        issue.setReportedBy( "jdoe" );
        issue.setSeverity( Issue.Level.Major );
        dqo.add( issue );
        Part fd = dqo.createPart( scenario );
        fd.setOrganization( dqo.findOrCreate( Organization.class, "Fire Department" ) );
        fd.setTask( "responding" );
        Flow fire = dqo.connect( tenant, alarm, "fire!" );
        fire.becomeTriggeringToTarget();
        Flow f1 = dqo.connect( alarm, js1, "location" );
        f1.setAskedFor( true );
        f1.addChannel( new Channel( Medium.Other, "wall panel" ) );
        f1.setDescription( "The fire location reported by the system" );
        Flow f2 = dqo.connect( js1, chief, "fire location" );
        f2.setAskedFor( true );
        f2.becomeCritical();
        f2.setDescription( "Communicate the location of the fire" );
        Flow f3 = dqo.connect( chief, js1, "stairways safe" );
        f3.setMaxDelay( new Delay( 10, Delay.Unit.minutes ) );
        f3.becomeCritical();
        f3.addChannel( new Channel( Medium.Radio, "band 3" ) );
        f3.setDescription( "Confirms that stairways are safe for evacuation" );
        Flow f4 = dqo.connect( js2, chief, "evacuation status" );
        f4.setAskedFor( true );
        f4.becomeCritical();
        dqo.connect( js1, evac.inputs().next(), "" );
        dqo.connect( evac.outputs().next(), js2, "" ).becomeTerminatingToTarget();
        Flow fireAddress = dqo.connect( alarm, fd, "address" );
        fireAddress.becomeTriggeringToTarget();
        dqo.connect( fd, chief, "" );
        chief.createOutcome( dqo ).setName( "\"all-clear\"" );
    }
}
