package com.mindalliance.channels.api.entities;

import com.mindalliance.channels.core.model.WorkTime;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Web service data element for availability.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/14/11
 * Time: 9:28 AM
 */
@XmlType( propOrder = {"availability"} )
public class AvailabilityData  implements Serializable {

    private WorkTime availability;

    public AvailabilityData() {
        // required
    }

    public AvailabilityData( WorkTime availability ) {
        this.availability = availability;
    }

    @XmlElement
    public String getAvailability() {
        return getLabel();
    }


    public String getLabel() {
        return availability.toString();
    }

}
