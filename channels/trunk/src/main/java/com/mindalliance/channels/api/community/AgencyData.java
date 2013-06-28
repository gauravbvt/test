package com.mindalliance.channels.api.community;

import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.api.procedures.ChannelData;
import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.protocols.CommunityEmployment;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Organization;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Web service data for an agency in a community.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/5/13
 * Time: 9:14 AM
 */
@XmlType(propOrder = {"name", "registeredByCommunity", "description", "mission", "address", "channels", "parentName", "employments", "planOrganizationId"})
public class AgencyData implements Serializable {

    private Agency agency;
    private String parentName;
    private List<EmploymentData> employments;
    private List<ChannelData> channelDataList;
    private Long planOrganizationId;
    private AgencyData parentData;
    private DocumentationData documentationData;
    private boolean registeredByCommunity;

    public AgencyData() {
        // required
    }

    public AgencyData( String serverUrl, Agency agency, CommunityService communityService ) {
        this.agency = agency;
        registeredByCommunity = agency.isRegisteredByCommunity( communityService );
        initPlanOrganization( serverUrl, communityService );
        initChannels( serverUrl, communityService );
        initParent( serverUrl, communityService );
        initEmployments( serverUrl, communityService );
    }

    private void initChannels( String serverUrl, CommunityService communityService ) {
        channelDataList = new ArrayList<ChannelData>();
        for ( Channel channel : agency.getEffectiveChannels() ) {
            channelDataList.add( new ChannelData( channel, communityService ) );
        }
    }

    private void initPlanOrganization( String serverUrl, CommunityService communityService ) {
        Organization organization = agency.getPlanOrganization();
        if ( organization != null ) {
            planOrganizationId = organization.getId();
            documentationData = new DocumentationData( serverUrl, organization );
        }
    }

    private void initParent( String serverUrl, CommunityService communityService ) {
        if ( agency.getParentName() != null ) {
            Agency parentAgency = communityService.getParticipationManager()
                    .findAgencyNamed( agency.getParentName(), communityService );
            if ( parentAgency != null ) {
                parentName = parentAgency.getName();
                parentData = new AgencyData( serverUrl, parentAgency, communityService );
            }
        }
    }

    private void initEmployments( String serverUrl, CommunityService communityService ) {
        employments = new ArrayList<EmploymentData>();
        for ( CommunityEmployment employment :
                communityService.getParticipationManager().findAllEmploymentsBy( agency, communityService ) ) {
            employments.add( new EmploymentData( employment ) );
        }
    }

    @XmlElement
    public String getName() {
        return agency.getName();
    }

    @XmlElement
    public boolean getRegisteredByCommunity() {
        return registeredByCommunity;
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
    public String getAddress() {
        return agency.getAddress();
    }

    @XmlElement
    public String getParentName() {
        return parentName;
    }

    @XmlElement(name = "employment")
    public List<EmploymentData> getEmployments() {
        return employments;
    }

    @XmlElement
    public Long getPlanOrganizationId() {
        return planOrganizationId;
    }

    public Set<Long> allOrganizationIds() {
        Set<Long> ids = new HashSet<Long>();
        if ( planOrganizationId != null ) {
            ids.add( planOrganizationId );
        }
        return ids;
    }

    public Set<Long> allMediumIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( ChannelData channelData : channelDataList ) {
            ids.add( channelData.getMediumId() );
        }
        return ids;

    }

    public Set<Long> allActorIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( EmploymentData employmentData : getEmployments() ) {
            ids.addAll( employmentData.allActorIds() );
        }
        return ids;
    }

    public Set<Long> allRoleIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( EmploymentData employmentData : getEmployments() ) {
            ids.add( employmentData.getRoleId() );
        }
        return ids;
    }

    public Set<Long> allPlaceIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( EmploymentData employmentData : getEmployments() ) {
            if ( employmentData.getJurisdictionId() != null )
                ids.add( employmentData.getJurisdictionId() );
        }
        return ids;
    }


    @XmlElement( name = "channel" )
    public List<ChannelData> getChannels() {
        return channelDataList;
    }

    public AgencyData getParent() {
        return parentData;
    }

    public DocumentationData getDocumentation() {
        return documentationData;
    }
}
