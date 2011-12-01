package com.mindalliance.channels.api;

import com.mindalliance.channels.core.model.Employment;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/1/11
 * Time: 10:27 AM
 */
@XmlRootElement( name = "employment", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder = {"agentId", "title", "roleId", "jurisdictionId", "supervisorId", "confirmed"} )
public class EmploymentData {

    private Employment employment;
    private boolean confirmed;

    public EmploymentData() {
        // for enunciate
    }

    public EmploymentData( Employment employment, boolean confirmed ) {
        this.employment = employment;
        this.confirmed = confirmed;
    }

    @XmlElement
    public Long getAgentId() {
        return employment.getActor().getId();
    }

    @XmlElement
    public String getTitle() {
        return !employment.getTitle().isEmpty()
                ? employment.getTitle()
                : null;
    }

    @XmlElement
    public Long getRoleId() {
        return employment.getRole().getId();
    }

    @XmlElement
    public Long getJurisdictionId() {
        return employment.getJurisdiction() != null
                ? employment.getJurisdiction().getId()
                : null;
    }

    @XmlElement
    public Long getSupervisorId() {
        return employment.getSupervisor() != null
                ? employment.getSupervisor().getId()
                : null;
    }

    @XmlElement
    public Boolean getConfirmed() {
        return confirmed;
    }

}
