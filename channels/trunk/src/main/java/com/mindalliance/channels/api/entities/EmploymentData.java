package com.mindalliance.channels.api.entities;

import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.model.Employment;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.HashSet;
import java.util.Set;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/1/11
 * Time: 10:27 AM
 */
@XmlType( propOrder = {"name", "organizationId", "title", "roleId", "jurisdictionId", "supervisorId", "confirmed"} )
public class EmploymentData {

    private Employment employment;
    private ChannelsUserInfo userInfo;

    public EmploymentData() {
        // required
    }

    public EmploymentData( Employment employment ) {
        this.employment = employment;
    }

    public EmploymentData( Employment employment, ChannelsUserInfo userInfo ) {
        this.employment = employment;
        this.userInfo = userInfo;
    }
    
    @XmlElement
    public String getName() {
        return userInfo != null
                ? userInfo.getFullName()
                : employment.getActor().getName();
    }

    @XmlElement
    public String getTitle() {
        return !employment.getTitle().isEmpty()
                ? employment.getTitle()
                : "(" + employment.getRole().getName() + ")";
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
        return employment.isConfirmed();
    }

    @XmlElement
    public Long getOrganizationId() {
        return employment.getOrganization().getId();
    }

    public Set<Long> allActorIds() {
        Set<Long> ids = new HashSet<Long>(  );
        ids.add( employment.getActor().getId() );
        if ( getSupervisorId() != null )
            ids.add( getSupervisorId() );
        return ids;
    }
}
