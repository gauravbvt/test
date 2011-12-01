package com.mindalliance.channels.api;

import com.mindalliance.channels.core.model.Phase;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Web Service data element for a phase object.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/30/11
 * Time: 1:55 PM
 */
@XmlRootElement( name = "phase", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder = {"id", "name", "timing"} )
public class PhaseData extends ModelObjectData {

    public PhaseData() {
        // for enunciate
    }

    public PhaseData( Phase phase ) {
        super( phase );
    }

    @Override
    @XmlElement
    public long getId() {
        return super.getId();
    }

    @Override
    @XmlElement
    public String getName() {
        return super.getName();
    }

    @XmlElement
    public String getTiming() {
        return getPhase().getTiming().getLabel();
    }

    private Phase getPhase() {
        return (Phase)getModelObject();
    }
}
