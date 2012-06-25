package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.model.EventTiming;
import org.apache.commons.lang.StringEscapeUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Web service data element for an event timing (composing a situational context).
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/8/11
 * Time: 4:14 PM
 */
@XmlType( propOrder = { "event", "timing" } )

public class EventTimingData {

    private EventTiming eventTiming;

    public EventTimingData() {
        // required
    }

    public EventTimingData( EventTiming eventTiming ) {
        this.eventTiming = eventTiming;
    }

    @XmlElement
    public Long getEvent() {
        return eventTiming.getEvent().getId();
    }

    @XmlElement
    public String getTiming() {
        return StringEscapeUtils.escapeXml( eventTiming.getTiming().getLabel() );
    }

    public String getLabel() {
        return eventTiming.toString();
    }
}
