package com.mindalliance.channels.api.entities;

import com.mindalliance.channels.core.model.Availability;
import com.mindalliance.channels.core.model.TimePeriod;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Web service data element for availability.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/14/11
 * Time: 9:28 AM
 */
@XmlType( propOrder = {"always", "timePeriods"} )
public class AvailabilityData  implements Serializable {

    private Availability availability;

    public AvailabilityData() {
        // required
    }

    public AvailabilityData( Availability availability ) {
        this.availability = availability;
    }

    @XmlElement
    public boolean getAlways() {
        return availability.isAlways();
    }

    @XmlElement( name = "timePeriod" )
    public List<TimePeriodData> getTimePeriods() {
        if ( availability.isAlways() ) {
            return null;
        } else {
            List<TimePeriodData> timePeriods = new ArrayList<TimePeriodData>();
            int dayIndex = 0;
            for ( TimePeriod timePeriod : availability.getTimePeriods() ) {
                timePeriods.add( new TimePeriodData( timePeriod, Availability.dayOfWeek( dayIndex ) ) );
                dayIndex++;
            }
            return timePeriods;
        }
    }

    public String getLabel() {
        return availability.toString();
    }

}
