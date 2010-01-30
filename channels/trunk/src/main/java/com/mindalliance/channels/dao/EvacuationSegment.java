package com.mindalliance.channels.dao;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.TransmissionMedium;

/**
 * The building evacuation segment.
 */
public class EvacuationSegment extends Segment {

    public EvacuationSegment() {
    }

    public EvacuationSegment( QueryService queryService ) {
        queryService.createPart( this );
        initialize( this, queryService );
    }

    /**
     * Add segment contents to a given segment
     * @param segment the resulting segment
     * @param queryService the query service to use
     */
    public static void initialize( Segment segment, QueryService queryService ) {
        segment.setName( "Building Evacuation" );
        Part p = segment.getDefaultPart();
        p.setActor( queryService.findOrCreate( Actor.class, "Sam Adams" ) );
        p.setTask( "supervising evacuation" );
        Flow goAhead = p.createRequirement( queryService );
        goAhead.setName( "go-ahead" );
        // goAhead.becomeCritical( );
        goAhead.becomeTriggeringToTarget();
        goAhead.addChannel( new Channel(
                queryService.findOrCreate( TransmissionMedium.class, "Phone" ),
                "800-555-4433" ) );
        Flow end = p.createOutcome( queryService );
        end.setName( "evacuation ended" );
        end.becomeTerminatingToSource();
    }
}
