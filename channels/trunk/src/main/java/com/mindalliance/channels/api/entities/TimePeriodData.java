package com.mindalliance.channels.api.entities;

import com.mindalliance.channels.core.model.TimePeriod;
import org.apache.commons.lang.StringEscapeUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Web service data element for a time period.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/14/11
 * Time: 9:40 AM
 */
@XmlType( propOrder={"dayOfWeek", "value", "fromTime", "toTime"} )
public class TimePeriodData  implements Serializable {

    private TimePeriod timePeriod;
    private String dayOfWeek;

    public TimePeriodData() {
        // required
    }

    public TimePeriodData( TimePeriod timePeriod, String dayOfWeek ) {
        this.timePeriod = timePeriod;
        this.dayOfWeek = dayOfWeek;
    }

    @XmlElement
    public String getDayOfWeek() {
        return dayOfWeek;
    }

    @XmlElement
    public String getValue() {
        return StringEscapeUtils.escapeXml( timePeriod.toString() );
    }

    @XmlElement
    public int getFromTime() {
        return timePeriod.getFromTime();
    }

    @XmlElement
    public int getToTime() {
        return timePeriod.getToTime();
    }

}
