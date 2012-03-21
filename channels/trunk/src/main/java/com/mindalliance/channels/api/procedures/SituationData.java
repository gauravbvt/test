package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.EventTiming;
import com.mindalliance.channels.core.query.PlanService;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Web Service data element for the situation in which a procedure is triggered according to a plan.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/11
 * Time: 10:26 AM
 */
@XmlType( propOrder = { "eventId", "phaseId", "context"} )
public class SituationData  extends AbstractProcedureElementData {

    public SituationData() {
        // required
    }

    public SituationData(
            Assignment assignment,
            PlanService planService,
            PlanParticipationService planParticipationService,
            ChannelsUser user ) {
        super( assignment,planService, planParticipationService, user );
    }

    @XmlElement
    public Long getEventId() {
        return getAssignment().getEventPhase().getEvent().getId();
    }

    @XmlElement
    public Long getPhaseId() {
        return getAssignment().getEventPhase().getPhase().getId();
    }

    @XmlElement
    public List<EventTimingData> getContext() {
        List<EventTimingData> context = new ArrayList<EventTimingData>(  );
        for ( EventTiming eventTiming : getAssignment().getPart().getSegment().getContext() ) {
            context.add( new EventTimingData( eventTiming ) );
        }
        return context;
    }


}
