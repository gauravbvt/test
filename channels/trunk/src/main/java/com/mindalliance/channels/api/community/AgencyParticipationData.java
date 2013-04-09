package com.mindalliance.channels.api.community;

import com.mindalliance.channels.api.plan.UserData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.participation.OrganizationParticipation;
import com.mindalliance.channels.core.dao.user.ChannelsUser;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Date;

@XmlType( name = "agencyParticipation", propOrder = {"agencyName", "placeholderId", "startedOn", "custodian"} )
/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/5/13
 * Time: 8:58 AM
 */
public class AgencyParticipationData implements Serializable {

    private String agencyName;
    private long placeholderId;
    private Date startedOn;
    private UserData custodian;

    public AgencyParticipationData() {
        // required
    }

    public AgencyParticipationData( String serverUrl,
                                    OrganizationParticipation orgParticipation,
                                    CommunityService communityService ) {
        agencyName = orgParticipation.getRegisteredOrganization().getName( communityService );
        placeholderId = orgParticipation.getPlaceholderOrgId();
        startedOn = orgParticipation.getCreated();
        ChannelsUser custodianUser =  communityService.getUserDao().getUserNamed( orgParticipation.getUsername() );
        if ( custodianUser != null )
            custodian = new UserData(  custodianUser, communityService );
    }

    @XmlElement
    public String getAgencyName() {
        return agencyName;
    }

    @XmlElement
    public UserData getCustodian() {
        return custodian;
    }

    @XmlElement
    public long getPlaceholderId() {
        return placeholderId;
    }

    @XmlElement
    public Date getStartedOn() {
        return startedOn;
    }
}
