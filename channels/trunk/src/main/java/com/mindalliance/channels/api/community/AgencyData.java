package com.mindalliance.channels.api.community;

import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.api.procedures.ChannelData;
import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.protocols.CommunityEmployment;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.asset.AssetConnection;

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
@XmlType(propOrder = {"name", "local", "registeredGlobally", "description", "mission", "address", "channels", "parentName", "employments", "modelOrganizationIds", "documentation"})
public class AgencyData implements Serializable {

    private Agency agency;
    private String parentName;
    private List<EmploymentData> employments;
    private List<ChannelData> channelDataList;
    private List<Long> modelOrganizationIds;
    private AgencyData parentData;
    private boolean local;
    private boolean registeredGlobally;
    private DocumentationData documentationData;

    public AgencyData() {
        // required
    }

    public AgencyData( String serverUrl, Agency agency, CommunityService communityService ) {
        this.agency = agency;
        local = agency.isLocal(  );
        initPlanOrganization( serverUrl, communityService );
        initChannels( serverUrl, communityService );
        initParent( serverUrl, communityService );
        initEmployments( serverUrl, communityService );
        if ( agency.isFixedOrganization() ) { // todo - always use agency documentation data?
            documentationData = new DocumentationData( serverUrl, agency.getRegisteredOrganization().getFixedOrganization( communityService ) );
        } else {
            documentationData = new DocumentationData( serverUrl, agency );
        }
    }

    private void initChannels( String serverUrl, CommunityService communityService ) {
        channelDataList = new ArrayList<ChannelData>();
        for ( Channel channel : agency.getEffectiveChannels() ) {
            channelDataList.add( new ChannelData( channel, communityService ) );
        }
    }

    private void initPlanOrganization( String serverUrl, CommunityService communityService ) {
        modelOrganizationIds = new ArrayList<Long>(  );
        for ( Organization organization : agency.getPlanOrganizations() ) {
            modelOrganizationIds.add( organization.getId() );
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
    public boolean getLocal() {
        return local;
    }

    @XmlElement
    public boolean getRegisteredGlobally() {
        return registeredGlobally;
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

    @XmlElement(name = "organizationId" )
    public List<Long> getModelOrganizationIds() {
        return modelOrganizationIds;
    }

    public Set<Long> allOrganizationIds() {
        return new HashSet<Long>( modelOrganizationIds );
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

    @XmlElement
    public DocumentationData getDocumentation() {
        return documentationData;
    }

    public Set<Long> allAssetIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( Organization organization : agency.getPlanOrganizations() ) {
            for ( AssetConnection assetConnection : organization.getAssetConnections().getAll() ) {
                ids.add( assetConnection.getAsset().getId() );
            }
        }
        return ids;
    }
}
