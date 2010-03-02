package com.mindalliance.channels.dao;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Delay;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.TransmissionMedium;
import com.mindalliance.channels.model.UserIssue;

/**
 * The fire in the building segment...
 *
 * @todo Move to test package when far enough along
 */
public class FireSegment extends Segment {

    public FireSegment() {
    }

    public FireSegment( QueryService queryService, Segment evac ) {
        queryService.createPart( this );
        initialize( this, queryService, evac );
    }

    /**
     * Initialize a segment to contents of "Fire in the building".
     * @param segment the segment, preferably empty
     * @param queryService the query service
     * @param evac the "Building Evacuation" segment
     */
    public static void initialize( Segment segment, QueryService queryService, Segment evac ) {
        segment.setName( "Fire in the building" );
        segment.setDescription( "A fire happens" );
        Actor joe = queryService.findOrCreate( Actor.class, "Joe Smith" );
        Part js1 = segment.getDefaultPart();
        js1.setActor( joe );
        js1.setTask( "investigating fire" );
        js1.setRole( queryService.findOrCreate( Role.class, "Fire Warden" ) );
        js1.setDescription( "Fire alarms must be investigated carefully." );
        Part js2 = segment.createPart( queryService, joe, "monitoring evacuation" );
        Part tenant = segment.createPart( queryService,
                                  queryService.findOrCreate( Role.class, "Tenant" ), "noticing fire" );
        Part chief = segment.createPart( queryService,
                                 queryService.findOrCreate( Role.class, "Fire Chief" ),
                                 "supervising operations" );
        Part alarm = segment.createPart( queryService,
                                 queryService.findOrCreate( Actor.class, "Fire Alarm" ), "ringing" );
        alarm.setRole( queryService.findOrCreate( Role.class, "System" ) );
        UserIssue issue = new UserIssue( alarm );
        issue.setDescription( "Hearing-challenged tenants may not hear the alarm." );
        issue.setRemediation( "Add flashing light signal." );
        issue.setReportedBy( "jdoe" );
        issue.setSeverity( Level.Medium );
        queryService.add( issue );
        Part fd = queryService.createPart( segment );
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
        chief.createSend( queryService ).setName( "\"all-clear\"" );
    }
}
