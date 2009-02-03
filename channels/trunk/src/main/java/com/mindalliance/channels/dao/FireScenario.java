package com.mindalliance.channels.dao;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Connector;
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

import javax.persistence.Entity;

/**
 * The fire in the building scenario...
 *
 * @todo Move to test package when far enough along
 */
@Entity
public class FireScenario extends Scenario {

    public FireScenario() {
    }

    public FireScenario( Service service, EvacuationScenario evac ) throws NotFoundException {

        setName( "Fire in the building" );
        setDescription( "A fire happens" );

        final Actor joe = service.findOrCreate( Actor.class, "Joe Smith" );
        final Part js1 = getDefaultPart();
        js1.setActor( joe );
        js1.setTask( "investigating fire" );
        js1.setRole( service.findOrCreate( Role.class, "Fire Warden" ) );
        js1.setDescription( "Fire alarms must be investigated carefully." );

        final Part js2 = createPart( service, joe, "monitoring evacuation" );
        final Part tenant = createPart( service, service.findOrCreate( Role.class, "Tenant" ), "noticing fire" );
        final Part chief = createPart( service, service.findOrCreate( Role.class, "Fire Chief" ), "supervising operations" );

        final Actor system = service.findOrCreate( Actor.class, "Fire Alarm" );
        final Part alarm = createPart( service, system, "ringing" );
        UserIssue issue = new UserIssue( alarm );
        issue.setDescription( "Hearing-challenged tenants may not hear the alarm." );
        issue.setRemediation( "Add flashing light signal." );
        issue.setReportedBy( "jdoe" );
        service.add( issue );
        alarm.setRole( service.findOrCreate( Role.class, "System" ) );
        final Part fd = service.createPart( this );
        fd.setOrganization( service.findOrCreate( Organization.class, "Fire Department" ) );
        fd.setTask( "responding" );

        connect( tenant, alarm );
        final Flow f1 = connect( alarm, js1 );
        f1.setName( "location" );
        f1.setAskedFor( true );
        f1.addChannel( new Channel( service.mediumNamed("Other"), "wall panel" ) );
        f1.setDescription( "The fire location reported by the system" );

        final Flow f2 = connect( js1, chief );
        f2.setName( "fire location" );
        f2.setAskedFor( true );
        f2.setCritical( true );
        f2.setDescription( "Communicate the location of the fire" );

        final Flow f3 = connect( chief, js1 );
        f3.setName( "stairways safe" );
        f3.setMaxDelay( new Delay( 10, Delay.Unit.minutes ) );
        f3.setCritical( true );
        f3.addChannel( new Channel( service.mediumNamed("Radio"), "band 3" ) );
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

        chief.createOutcome( service ).setName( "\"all-clear\"" );
    }
}
