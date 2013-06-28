package com.mindalliance.channels.api.community;

import com.mindalliance.channels.api.entities.ActorData;
import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.db.data.communities.OrganizationParticipation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Web service data for an agent in a community.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/5/13
 * Time: 9:14 AM
 */
@XmlType( propOrder= {"name", "agencyName", "actor", "open", "restrictedToEmployed", "singular", "supervised"} )

public class AgentData implements Serializable {

    private ActorData actor;
    private String agencyName;
    private String name;
    private boolean open;
    private boolean restrictedToEmployed;
    private boolean singular;
    private boolean supervised;

    public AgentData() {
    }

    public AgentData( String serverUrl, Agent agent, CommunityService communityService ) {
        name = agent.getName();
        open = agent.isOpenParticipation();
        restrictedToEmployed = agent.isParticipationRestrictedToEmployed();
        singular = agent.isSingularParticipation();
        supervised = agent.isSupervisedParticipation();
        actor = new ActorData( serverUrl, agent.getActor(), communityService );
        OrganizationParticipation orgParticipation = agent.getOrganizationParticipation();
        if ( orgParticipation != null ) {
            agencyName = orgParticipation.getRegisteredOrganization( communityService ).getName( communityService );
        }
    }

    @XmlElement
    public ActorData getActor() {
        return actor;
    }

    @XmlElement
    public String getAgencyName() {
        return agencyName;
    }

    @XmlElement
    public String getName() {
        return name;
    }

    @XmlElement
    public boolean isOpen() {
        return open;
    }

    @XmlElement
    public boolean isRestrictedToEmployed() {
        return restrictedToEmployed;
    }

    @XmlElement
    public boolean isSingular() {
        return singular;
    }

    @XmlElement
    public boolean isSupervised() {
        return supervised;
    }
}
