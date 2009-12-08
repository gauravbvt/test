package com.mindalliance.channels.dao;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Delay;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.TransmissionMedium;
import com.mindalliance.channels.model.UserIssue;

/**
 * The fire in the building scenario...
 *
 * @todo Move to test package when far enough along
 */
public class FireScenario extends Scenario {

    public FireScenario() {
    }

    public FireScenario( QueryService queryService, Scenario evac ) {
        queryService.createPart( this );
        initialize( this, queryService, evac );
    }

    /**
     * Initialize a scenario to contents of "Fire in the building".
     * @param scenario the scenario, preferably empty
     * @param queryService the query service
     * @param evac the "Building Evacuation" scenario
     */
    public static void initialize( Scenario scenario, QueryService queryService, Scenario evac ) {
        scenario.setName( "Fire in the building" );
        scenario.setDescription( "A fire happens" );
        Actor joe = queryService.findOrCreate( Actor.class, "Joe Smith" );
        Part js1 = scenario.getDefaultPart();
        js1.setActor( joe );
        js1.setTask( "investigating fire" );
        js1.setRole( queryService.findOrCreate( Role.class, "Fire Warden" ) );
        js1.setDescription( "Fire alarms must be investigated carefully." );
        Part js2 = scenario.createPart( queryService, joe, "monitoring evacuation" );
        Part tenant = scenario.createPart( queryService,
                                  queryService.findOrCreate( Role.class, "Tenant" ), "noticing fire" );
        Part chief = scenario.createPart( queryService,
                                 queryService.findOrCreate( Role.class, "Fire Chief" ),
                                 "supervising operations" );
        Part alarm = scenario.createPart( queryService,
                                 queryService.findOrCreate( Actor.class, "Fire Alarm" ), "ringing" );
        alarm.setRole( queryService.findOrCreate( Role.class, "System" ) );
        UserIssue issue = new UserIssue( alarm );
        issue.setDescription( "Hearing-challenged tenants may not hear the alarm." );
        issue.setRemediation( "Add flashing light signal." );
        issue.setReportedBy( "jdoe" );
        issue.setSeverity( Issue.Level.Major );
        queryService.add( issue );
        Part fd = queryService.createPart( scenario );
        fd.setOrganization( queryService.findOrCreate( Organization.class, "Fire Department" ) );
        fd.setTask( "responding" );
        Flow fire = queryService.connect( tenant, alarm, "fire!" );
        fire.becomeTriggeringToTarget();
        Flow f1 = queryService.connect( alarm, js1, "location" );
        f1.setAskedFor( true );
        f1.addChannel( new Channel( queryService.findOrCreate(
                TransmissionMedium.class,
                "Cell" ),
                "917-233-3333" ) );
        f1.setDescription( "The fire location reported by the system" );
        Flow f2 = queryService.connect( js1, chief, "fire location" );
        f2.setAskedFor( true );
        f2.becomeCritical();
        f2.setDescription( "Communicate the location of the fire" );
        Flow f3 = queryService.connect( chief, js1, "stairways safe" );
        f3.setMaxDelay( new Delay( 10, Delay.Unit.minutes ) );
        f3.becomeCritical();
        f3.addChannel( new Channel( queryService.findOrCreate(
                TransmissionMedium.class,
                "Radio" ),
                "band 3" ) );
        f3.setDescription( "Confirms that stairways are safe for evacuation" );
        Flow f4 = queryService.connect( js2, chief, "evacuation status" );
        f4.setAskedFor( true );
        f4.becomeCritical();
        queryService.connect( js1, evac.inputs().next(), "" );
        queryService.connect( evac.outputs().next(), js2, "" ).becomeTerminatingToTarget();
        Flow fireAddress = queryService.connect( alarm, fd, "address" );
        fireAddress.becomeTriggeringToTarget();
        queryService.connect( fd, chief, "" );
        chief.createOutcome( queryService ).setName( "\"all-clear\"" );
    }
}
