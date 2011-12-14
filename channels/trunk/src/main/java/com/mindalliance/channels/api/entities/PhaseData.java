package com.mindalliance.channels.api.entities;

import com.mindalliance.channels.api.ModelObjectData;
import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Plan;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Web Service data element for a phase object.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/30/11
 * Time: 1:55 PM
 */
@XmlType( propOrder = {"id", "name", "timing", "documentation"} )
public class PhaseData extends ModelObjectData {

    public PhaseData() {
        // required
    }

    public PhaseData( Phase phase, Plan plan ) {
        super( phase, plan );
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

    @XmlElement
    @Override
    public DocumentationData getDocumentation() {
        return super.getDocumentation();
    }

    private Phase getPhase() {
        return (Phase)getModelObject();
    }
}
