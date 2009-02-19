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
import com.mindalliance.channels.Service;
import com.mindalliance.channels.UserIssue;

/**
 * The fire in the building scenario...
 *
 * @todo Move to test package when far enough along
 */
public class FireScenario extends Scenario {

    public FireScenario() {
    }

    public FireScenario( Service service, Scenario evac ) {
        service.createPart( this );
        initialize( this, service, evac );
    }

    /**
     * Initialize a scenario to contents of "Fire in the building".
     * @param scenario the scenario, preferably empty
     * @param service the service
     * @param evac the "Building Evacuation" scenario
     */
    public static void initialize( Scenario scenario, Service service, Scenario evac ) {
        scenario.setName( "Fire in the building" );
        scenario.setDescription( "A fire happens" );
        Actor joe = service.findOrCreate( Actor.class, "Joe Smith" );
        Part js1 = scenario.getDefaultPart();
        js1.setActor( joe );
        js1.setTask( "investigating fire" );
        js1.setRole( service.findOrCreate( Role.class, "Fire Warden" ) );
        js1.setDescription( "Fire alarms must be investigated carefully." );
        Part js2 = scenario.createPart( service, joe, "monitoring evacuation" );
        Part tenant = scenario.createPart( service,
                                  service.findOrCreate( Role.class, "Tenant" ), "noticing fire" );
        Part chief = scenario.createPart( service,
                                 service.findOrCreate( Role.class, "Fire Chief" ),
                                 "supervising operations" );
        Part alarm = scenario.createPart( service,
                                 service.findOrCreate( Actor.class, "Fire Alarm" ), "ringing" );
        alarm.setRole( service.findOrCreate( Role.class, "System" ) );
        UserIssue issue = new UserIssue( alarm );
        issue.setDescription( "Hearing-challenged tenants may not hear the alarm." );
        issue.setRemediation( "Add flashing light signal." );
        issue.setReportedBy( "jdoe" );
        issue.setSeverity( Issue.Level.Major );
        service.add( issue );
        Part fd = service.createPart( scenario );
        fd.setOrganization( service.findOrCreate( Organization.class, "Fire Department" ) );
        fd.setTask( "responding" );
        service.connect( tenant, alarm, "" );
        Flow f1 = service.connect( alarm, js1, "location" );
        f1.setAskedFor( true );
        f1.addChannel( new Channel( Medium.Other, "wall panel" ) );
        f1.setDescription( "The fire location reported by the system" );
        Flow f2 = service.connect( js1, chief, "fire location" );
        f2.setAskedFor( true );
        f2.becomeCritical();
        f2.setDescription( "Communicate the location of the fire" );
        Flow f3 = service.connect( chief, js1, "stairways safe" );
        f3.setMaxDelay( new Delay( 10, Delay.Unit.minutes ) );
        f3.becomeCritical();
        f3.addChannel( new Channel( Medium.Radio, "band 3" ) );
        f3.setDescription( "Confirms that stairways are safe for evacuation" );
        Flow f4 = service.connect( js2, chief, "evacuation status" );
        f4.setAskedFor( true );
        f4.becomeCritical();
        service.connect( js1, evac.inputs().next(), "" );
        service.connect( evac.outputs().next(), js2, "" ).becomeCritical();
        service.connect( alarm, fd, "address" );
        service.connect( fd, chief, "" );
        chief.createOutcome( service ).setName( "\"all-clear\"" );
    }
}
