package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.model.time.Cycle;
import com.mindalliance.channels.core.model.time.Tranche;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Web service data element for a cycle.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/30/14
 * Time: 3:24 PM
 */
@XmlType( propOrder = {"label", "timeUnit", "skip", "tranches"})
public class CycleData implements Serializable {

    private Cycle cycle;

    public CycleData() {
        // required
    }

    public CycleData( Cycle cycle ) {
        this.cycle = cycle;
    }

    @XmlElement
    public String getTimeUnit() {
        return cycle.getTimeUnit().name();
    }

    @XmlElement
    public int getSkip() {
        return cycle.getSkip();
    }

    @XmlElement( name="tranche")
    public List<String> getTranches() {
        List<String> labels = new ArrayList<String>(  );
        for ( Tranche tranche : cycle.getTranches() ) {
            labels.add( tranche.getLabel() );
        }
        return labels;
    }

    @XmlElement
    public String getLabel() {
        return cycle.getLabel();
    }
}
