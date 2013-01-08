package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.entities.OrganizationData;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.participation.Agency;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Organization;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Agency data.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/4/13
 * Time: 5:20 PM
 */
@XmlType(propOrder = {"name", "description", "mission", "parent", "registeredByCommunity", "planOrganization"})
public class AgencyData implements Serializable {

    private Agency agency;
    private AgencyData parentData;
    private List<ChannelData> channelDataList;
    private OrganizationData organizationData;

    public AgencyData() {
        // required
    }

    public AgencyData( String serverUrl, Agency agency, PlanCommunity planCommunity ) {
        this.agency = agency;
        init( serverUrl, planCommunity );
    }

    private void init( String serverUrl, PlanCommunity planCommunity ) {
        Agency parentAgency = agency.getParent( planCommunity );
        if ( parentAgency != null ) {
            parentData = new AgencyData( serverUrl, parentAgency, planCommunity );
        }
        Organization organization = agency.getPlanOrganization();
        if ( organization != null ) {
            organizationData = new OrganizationData( serverUrl, organization, planCommunity );
        }
        channelDataList = new ArrayList<ChannelData>();
        for ( Channel channel : agency.getEffectiveChannels() ) {
            channelDataList.add( new ChannelData( channel, planCommunity.getPlanService() ) );
        }
    }

    @XmlElement
    public String getName() {
        return agency.getName();
    }

    @XmlElement
    public String getDescription() {
        return agency.getDescription();
    }

    @XmlElement
    public String getMission() {
        return agency.getMission();
    }

    @XmlElement
    public AgencyData getParent() {
        return parentData;
    }

    @XmlElement
    public boolean getRegisteredByCommunity() {
        return agency.isRegisteredByCommunity();
    }

    @XmlElement
    public OrganizationData getPlanOrganization() {
        return organizationData;
    }

    public List<Long> allOrganizationIds() {
        List<Long> ids = new ArrayList<Long>();
        if ( organizationData != null ) {
            ids.add( organizationData.getId() );
        }
        return ids;
    }

    public String getAddress() {
        return agency.getAddress();
    }

    public List<ChannelData> getChannels() {
        return channelDataList;
    }

    public DocumentationData getDocumentation() {
        return organizationData != null
                ? organizationData.getDocumentation()
                : new DocumentationData();
    }
}
