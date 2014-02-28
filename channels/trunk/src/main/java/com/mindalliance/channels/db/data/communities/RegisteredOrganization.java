package com.mindalliance.channels.db.data.communities;

import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.db.data.AbstractChannelsDocument;
import com.mindalliance.channels.db.data.ContactInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An organization registered by a plan community for participation.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/19/13
 * Time: 1:11 PM
 */
@Document( collection = "communities" )
public class RegisteredOrganization extends AbstractChannelsDocument {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( RegisteredOrganization.class );

    /**
     * The id of a plan-defined organization this proxies. -1 if the organization is not plan-defined.
     */
    private long fixedOrganizationId = -1;
    /**
     * If not plan defined, then name must be uniquely set.
     */
    private String name;
    private String description;
    private String mission;
    private String address;
    private String parentUid;

    private List<ContactInfo> contactInfoList = new ArrayList<ContactInfo>();

    public RegisteredOrganization() {
    }

    /**
     * Organization registered across collaboration plans.
     *
     * @param username the username of who is doing the registration
     * @param name     the unique name of the organization
     */
    public RegisteredOrganization( String username, String name ) {
        super( PlanCommunity.ANY_URI, CollaborationModel.ANY_URI, 0, username );
        this.name = name;
    }

    public RegisteredOrganization( String username, String name, PlanCommunity planCommunity ) {
        super( planCommunity.getUri(), planCommunity.getModelUri(), planCommunity.getModelVersion(), username );
        this.name = name;
    }

    /**
     * Organization registered across collaboration plans.
     *
     * @param username            the username of who is doing the registration
     * @param fixedOrganizationId the unique id of the fixed organization
     */
    public RegisteredOrganization( String username, long fixedOrganizationId ) {
        super( PlanCommunity.ANY_URI, CollaborationModel.ANY_URI, 0, username );
        this.fixedOrganizationId = fixedOrganizationId;
    }

    public RegisteredOrganization( String username, long fixedOrganizationId, PlanCommunity planCommunity ) {
        super( planCommunity.getUri(), planCommunity.getModelUri(), planCommunity.getModelVersion(), username );
        this.fixedOrganizationId = fixedOrganizationId;
    }

    public RegisteredOrganization( RegisteredOrganization registeredOrganization ) {
        super( registeredOrganization.getCommunityUri(),
                registeredOrganization.getPlanUri(),
                registeredOrganization.getPlanVersion(),
                registeredOrganization.getUsername() );
        setName( registeredOrganization.getName() );
        setDescription( registeredOrganization.getDescription() );
        setMission( registeredOrganization.getMission() );
        parentUid = registeredOrganization.getParentUid();
    }

    public long getFixedOrganizationId() {
        return fixedOrganizationId;
    }

    public boolean isFixedOrganization() {
        return fixedOrganizationId != -1;
    }

    public String getParentUid() {
        return parentUid;
    }

    public void setParentUid( String parentUid ) {
        this.parentUid = parentUid;
    }

    public List<ContactInfo> getContactInfoList() {
        return contactInfoList;
    }

    public void setContactInfoList( List<ContactInfo> contactInfoList ) {
        this.contactInfoList = contactInfoList;
    }

    public void addContactInfo( Channel channel ) {
        ContactInfo contactInfo = new ContactInfo( channel );
        if ( !contactInfoList.contains( contactInfo ) ) {
            contactInfoList.add( contactInfo );
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public String getMission() {
        return mission;
    }

    public void setMission( String mission ) {
        this.mission = mission;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress( String address ) {
        this.address = address;
    }

    public String getCommunityGivenName() {
        return name;
    }

    public String getName( CommunityService communityService ) {
        if ( name != null ) {
            return name;
        } else {
            Organization org = getFixedOrganization( communityService );
            assert org == null || org.isActual();
            return org == null ? "?" : org.getName();
        }
    }

    public void updateWith( Agency update ) {
        assert fixedOrganizationId == -1;  // not a plan-defined, fixed organization
        setName( update.getName() );
        setDescription( update.getDescription() );
        setMission( update.getMission() );
        setAddress( update.getAddress() );
    }

    public Organization getFixedOrganization( CommunityService communityService ) {
        if ( fixedOrganizationId == -1 ) return null;
        try {
            return communityService.find( Organization.class, fixedOrganizationId, getCreated() );
        } catch ( NotFoundException e ) {
            if ( fixedOrganizationId > -1 )
                LOG.warn( "Organization not found at " + fixedOrganizationId );
            return null;
        }
    }

    public void setName( String name ) {
        this.name = name;
    }

    public RegisteredOrganization getParent( CommunityService communityService ) {
        return parentUid == null
                ? null
                : communityService.getParticipationManager().getRegisteredOrganization( parentUid );
    }

    public void setParent( RegisteredOrganization parent ) {
        parentUid = parent == null ? null : parent.getUid();
    }

    public boolean isCommunityDefined() {
        return fixedOrganizationId == -1;
    }

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof RegisteredOrganization ) {
            RegisteredOrganization other = (RegisteredOrganization) object;
            if ( isFixedOrganization() ) {
                return fixedOrganizationId == other.getFixedOrganizationId();
            } else {
                return getCommunityUri().equals( other.getCommunityUri() )
                        && name.equals( other.getCommunityGivenName() );
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        if ( isFixedOrganization() ) {
            hash = hash * 31 + Long.valueOf( fixedOrganizationId ).hashCode();
        } else {
            hash = hash * 31 + name.hashCode();
            hash = hash * 31 + getCommunityUri().hashCode();
        }
        return hash;
    }


    public List<Job> getFixedJobs( CommunityService communityService ) {
        if ( isFixedOrganization() ) {
            Organization org = getFixedOrganization( communityService );
            if ( org != null )
                return Collections.unmodifiableList( org.getJobs() );
            else
                return new ArrayList<Job>();
        } else {
            return new ArrayList<Job>();
        }
    }

    public boolean isValid( CommunityService communityService ) {
        return !isFixedOrganization() || getFixedOrganization( communityService ) != null;
    }

    // Note: Can return a non-persisted registered organization.
    public RegisteredOrganization getEffectiveParent( CommunityService communityService ) {
        if ( isFixedOrganization() ) {
            Organization parent = getFixedOrganization( communityService ).getParent();
            if ( parent == null ) {
                return null;
            } else {
                return new RegisteredOrganization(
                        getUsername(),
                        parent.getId(),
                        communityService.getPlanCommunity()
                );
            }
        } else {
            RegisteredOrganization parent = getParent( communityService );
            return parent == null ? null : parent;
        }
    }

    public String getEffectiveDescription( CommunityService communityService ) {
        if ( isFixedOrganization() ) {
            Organization org = getFixedOrganization( communityService );
            return org == null ? "" : org.getDescription();
        } else {
            return description == null ? "" : description;
        }
    }

    public String getEffectiveMission( CommunityService communityService ) {
        if ( isFixedOrganization() ) {
            Organization org = getFixedOrganization( communityService );
            return org == null ? "" : org.getMission();
        } else {
            return mission == null ? "" : mission;
        }
    }

    public String getParentName( CommunityService communityService ) {
        RegisteredOrganization parentRegistration = getParent( communityService );
        return parentRegistration == null ? null : parentRegistration.getName( communityService );

    }

    public Place getJurisdiction( CommunityService communityService ) {
        if ( isFixedOrganization() ) {
            Organization org = getFixedOrganization( communityService );
            if ( org != null )
                return org.getJurisdiction();
        }
        return null;
    }

    public String getFullAddress() {
        return address;
    }

    public String asString( CommunityService communityService ) {
        return getName( communityService );
    }

    public boolean isLocal() {
        return !isFixedOrganization() && !getCommunityUri().equals( PlanCommunity.ANY_URI );
    }

}
