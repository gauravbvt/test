package com.mindalliance.channels.dao;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Delay;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.UserIssue;
import com.mindalliance.channels.Channel;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Issue;

import javax.persistence.Entity;

/**
 * The fire in the building scenario...
 *
 * @todo Move to test package when far enough along
 */
@SuppressWarnings( { "HardCodedStringLiteral" } )
@Entity
public class FireScenario extends Scenario {

    public FireScenario() {
    }

    public FireScenario( Service service, EvacuationScenario evac ) {

        setName( "Fire in the building" );
        setDescription( "A fire happens" );
        Actor joe = service.findOrCreate( Actor.class, "Joe Smith" );

        Part js1 = service.createPart( this );
        js1.setActor( joe );
        js1.setTask( "investigating fire" );
        js1.setRole( service.findOrCreate( Role.class, "Fire Warden" ) );
        js1.setDescription( "Fire alarms must be investigated carefully." );

        Part js2 = createPart( service, joe, "monitoring evacuation" );
        Part tenant = createPart( service,
                                  service.findOrCreate( Role.class, "Tenant" ), "noticing fire" );
        Part chief = createPart( service,
                                 service.findOrCreate( Role.class, "Fire Chief" ),
                                 "supervising operations" );

        Part alarm = createPart( service,
                                 service.findOrCreate( Actor.class, "Fire Alarm" ), "ringing" );
        alarm.setRole( service.findOrCreate( Role.class, "System" ) );
        UserIssue issue = new UserIssue( alarm );
        issue.setDescription( "Hearing-challenged tenants may not hear the alarm." );
        issue.setRemediation( "Add flashing light signal." );
        issue.setReportedBy( "jdoe" );
        issue.setSeverity( Issue.Level.Major );
        service.add( issue );

        Part fd = service.createPart( this );
        fd.setOrganization( service.findOrCreate( Organization.class, "Fire Department" ) );
        fd.setTask( "responding" );
        service.connect( tenant, alarm, "" );

        Flow f1 = service.connect( alarm, js1, "location" );
        f1.setAskedFor( true );
        f1.addChannel( new Channel( service.mediumNamed( "Other" ), "wall panel" ) );
        f1.setDescription( "The fire location reported by the system" );

        Flow f2 = service.connect( js1, chief, "fire location" );
        f2.setAskedFor( true );
        f2.setCritical( true );
        f2.setDescription( "Communicate the location of the fire" );

        Flow f3 = service.connect( chief, js1, "stairways safe" );
        f3.setMaxDelay( new Delay( 10, Delay.Unit.minutes ) );
        f3.setCritical( true );
        f3.addChannel( new Channel( service.mediumNamed( "Radio" ), "band 3" ) );
        f3.setDescription( "Confirms that stairways are safe for evacuation" );

        Flow f4 = service.connect( js2, chief, "evacuation status" );
        f4.setAskedFor( true );
        f4.setCritical( true );

        service.connect( js1, evac.inputs().next(), "" );
        service.connect( evac.outputs().next(), js2, "" ).setCritical( true );
        service.connect( alarm, fd, "address" );
        service.connect( fd, chief, "" );
        chief.createOutcome( service ).setName( "\"all-clear\"" );
    }
}
