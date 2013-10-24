package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.model.Delay;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Web service data element for a time delay.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/9/11
 * Time: 2:39 PM
 */
@XmlType( propOrder = {"amount", "unit", "seconds"} )

public class TimeDelayData  implements Serializable {

    private Delay delay;

    public TimeDelayData() {
        // required
    }

    public TimeDelayData( Delay delay ) {
        this.delay = delay;
    }

    @XmlElement
    public int getAmount() {
        return delay.getAmount();
    }

    @XmlElement
    public String getUnit() {
        return delay.getUnit().name();
    }

    @XmlElement
    public int getSeconds() {
        return delay.getSeconds();
    }

    public String getLabel() {
        return delay.isImmediate()
                ? delay.toString()
                : ( "Within " + delay.toString() );
    }

    public boolean isImmediate() {
        return delay.isImmediate();
    }
}
