package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.orm.model.AbstractPersistentChannelsObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An organization registered by a plan community for participation.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/3/12
 * Time: 2:41 PM
 */
// @Entity
public class RegisteredOrganization extends AbstractPersistentChannelsObject {

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
    @Column( length = 2000 )
    private String name;
    @Column( length = 10000 )
    private String description;
    @Column( length = 10000 )
    private String mission;
    @Column( length = 5000 )
    private String address;
    @ManyToOne
    private RegisteredOrganization parent;
    /**
     * Indicates if parentId is for participating organization of in-model organization.
     */
    private boolean parentRegistered;

    @OneToMany( mappedBy = "registeredOrganization", cascade = CascadeType.ALL )
    @Transient
    private List<OrganizationContactInfo> contactInfoList;

    @OneToMany( mappedBy = "registeredOrganization", cascade = CascadeType.ALL )
    @Transient
    private List<OrganizationParticipation> registrationList;

    @Transient
    @OneToMany( mappedBy = "parent", cascade = CascadeType.ALL )
    List<RegisteredOrganization> children;

    public RegisteredOrganization() {
    }

    public RegisteredOrganization( String username, String name, PlanCommunity planCommunity ) {
        super( planCommunity.getUri(), planCommunity.getPlanUri(), planCommunity.getPlanVersion(), username );
        this.name = StringUtils.abbreviate( name, 2000 );
    }

    public RegisteredOrganization( String username, long fixedOrganizationId, PlanCommunity planCommunity ) {
        super( planCommunity.getUri(), planCommunity.getPlanUri(), planCommunity.getPlanVersion(), username );
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
        parent = registeredOrganization.getParent();
    }

    public long getFixedOrganizationId() {
        return fixedOrganizationId;
    }

    public boolean isFixedOrganization() {
        return fixedOrganizationId != -1;
    }

    public List<OrganizationContactInfo> getContactInfoList() {
        return contactInfoList;
    }

    public void setContactInfoList( List<OrganizationContactInfo> contactInfoList ) {
        this.contactInfoList = contactInfoList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = StringUtils.abbreviate( description, 10000);
    }

    public String getMission() {
        return mission;
    }

    public void setMission( String mission ) {
        this.mission = StringUtils.abbreviate( mission, 10000 );
    }

    public String getAddress() {
        return address;
    }

    public void setAddress( String address ) {
        this.address = StringUtils.abbreviate( address, 5000 );
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
        this.name = StringUtils.abbreviate( name, 2000 );
    }

    public RegisteredOrganization getParent() {
        return parent;
    }

    public void setParent( RegisteredOrganization parent ) {
        this.parent = parent;
    }

    public boolean isParentRegistered() {
        return parentRegistered;
    }

    public void setParentRegistered( boolean parentRegistered ) {
        this.parentRegistered = parentRegistered;
    }

    public List<OrganizationParticipation> getRegistrationList() {
        return registrationList;
    }

    public void setRegistrationList( List<OrganizationParticipation> registrationList ) {
        this.registrationList = registrationList;
    }

    public List<RegisteredOrganization> getChildren() {
        return children;
    }

    public void setChildren( List<RegisteredOrganization> children ) {
        this.children = children;
    }

    public boolean isCommunityDefined() {
        return fixedOrganizationId == -1;
    }

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof RegisteredOrganization ) {
            RegisteredOrganization other = (RegisteredOrganization) object;
            if ( isCommunityDefined() ) {
                return name.equals( other.getCommunityGivenName() );
            } else {
                return fixedOrganizationId == other.getFixedOrganizationId();
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        if ( isCommunityDefined() ) {
            hash = hash * 31 + name.hashCode();
        } else {
            hash = hash * 31 + Long.valueOf( fixedOrganizationId ).hashCode();
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
            RegisteredOrganization parent = getParent();
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
        RegisteredOrganization parentRegistration = getParent();
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

}
