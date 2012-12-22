package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.orm.model.AbstractPersistentChannelsObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An organization participating dynamically in a plan as an instance of a placeholder organization.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/3/12
 * Time: 2:41 PM
 */
@Entity
public class RegisteredOrganization extends AbstractPersistentChannelsObject {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( RegisteredOrganization.class );

    /**
     * The id of a plan-defined organization this proxies. -1 if the organization is not plan-defined.
     */
    private long fixedOrganizationId = -1;
    @Column( length = 1000 )
    /**
     * If not plan defined, then name must be uniquely set.
     */
    private String name;
    @Column( length = 2000 )
    private String description;
    @Column( length = 2000 )
    private String mission;
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
        this.name = name;
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
        name = registeredOrganization.getName();
        description = registeredOrganization.getDescription();
        mission = registeredOrganization.getMission();
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
        this.description = description;
    }

    public String getMission() {
        return mission;
    }

    public void setMission( String mission ) {
        this.mission = mission;
    }

    public String getCommunityGivenName() {
        return name;
    }

    public String getName( PlanCommunity planCommunity ) {
        if ( name != null ) {
            return name;
        } else {
            Organization org = getFixedOrganization( planCommunity );
            assert org == null || org.isActual();
            return org == null ? "?" : org.getName();
        }
    }

    public void updateWith( Agency update ) {
        name = update.getName();
        description = update.getDescription();
        mission = update.getMission();
    }

    public Organization getFixedOrganization( PlanCommunity planCommunity ) {
        try {
            return planCommunity.getPlanService().find( Organization.class, fixedOrganizationId );
        } catch ( NotFoundException e ) {
            if ( fixedOrganizationId > -1 )
                LOG.warn( "Organization not found at " + fixedOrganizationId );
            return null;
        }
    }

    public void setName( String name ) {
        assert fixedOrganizationId != -1;
        this.name = name;
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
            hash = hash * 31 + new Long( fixedOrganizationId ).hashCode();
        }
        return hash;
    }


    public List<Job> getFixedJobs( PlanCommunity planCommunity ) {
        if ( isFixedOrganization() ) {
            Organization org = getFixedOrganization( planCommunity );
            if ( org != null )
                return Collections.unmodifiableList( org.getJobs() );
            else
                return new ArrayList<Job>();
        } else {
            return new ArrayList<Job>();
        }
    }

    public List<Job> getPlaceHolderJobs( PlanCommunity planCommunity ) {
        if ( isParentRegistered() ) {
            Organization org = getFixedOrganization( planCommunity );
            if ( org != null )
                return Collections.unmodifiableList( org.getJobs() );
            else
                return new ArrayList<Job>();
        } else {
            return new ArrayList<Job>();
        }
    }


    public boolean isValid( PlanCommunity planCommunity ) {
        return !isFixedOrganization() || getFixedOrganization( planCommunity ) != null;
    }

    // Note: Can return a non-persisted registered organization.
    public RegisteredOrganization getEffectiveParent( PlanCommunity planCommunity ) {
        if ( isFixedOrganization() ) {
            Organization parent = getFixedOrganization( planCommunity ).getEffectiveParent();
            if ( parent == null ) {
                return null;
            } else {
                return new RegisteredOrganization(
                        getUsername(),
                        parent.getId(),
                        planCommunity
                );
            }
        } else {
            RegisteredOrganization parent = getParent();
            return parent == null ? null : parent;
        }
    }

    public String getEffectiveDescription( PlanCommunity planCommunity ) {
        if ( isFixedOrganization() ) {
            Organization org = getFixedOrganization( planCommunity );
            return org == null ? "" : org.getDescription();
        } else {
            return description == null ? "" : description;
        }
    }

    public String getEffectiveMission( PlanCommunity planCommunity ) {
        if ( isFixedOrganization() ) {
            Organization org = getFixedOrganization( planCommunity );
            return org == null ? "" : org.getMission();
        } else {
            return mission == null ? "" : mission;
        }
    }

    public String getParentName( PlanCommunity planCommunity ) {
        RegisteredOrganization parentRegistration = getParent();
        return parentRegistration == null ? null : parentRegistration.getName( planCommunity );

    }

}
