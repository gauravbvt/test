package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.orm.model.AbstractPersistentChannelsObject;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
     * The id of a plan-defined organization this proxies. Null if the organization is not plan-defined.
     */
    private long fixedOrganizationId = -1;
    @Column(length=1000)
    private String name;
    @Column(length=2000)
    private String description;
    @Column(length=2000)
    private String mission;
    private long parentId;
    /**
     * Indicates if parentId is for participating organization of in-model organization.
     */
    private boolean parentRegistered;
    @OneToMany( mappedBy = "registeredOrganization", cascade = CascadeType.ALL )
    @Transient
    private List<OrganizationContactInfo> contactInfoList;
    @OneToMany( mappedBy = "registeredOrganization", cascade = CascadeType.ALL )
    @Transient
    private List<OrganizationRegistration> registrationList;

    public RegisteredOrganization( String username, String name, PlanCommunity planCommunity ) {
        super( planCommunity.getUri(), planCommunity.getPlanUri(), planCommunity.getPlanVersion(), username );
        this.name = name;
    }

    public RegisteredOrganization( String username, long fixedOrganizationId, PlanCommunity planCommunity ) {
        super( planCommunity.getUri(), planCommunity.getPlanUri(), planCommunity.getPlanVersion(), username );
        this.fixedOrganizationId = fixedOrganizationId;
    }

    public long getFixedOrganizationId() {
        return fixedOrganizationId;
    }

    public boolean isFixedOrganization() {
        return fixedOrganizationId == -1;
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

    public String getName( PlanCommunity planCommunity ) {
        if ( name != null ) {
            return name;
        }
        else {
            Organization org = getFixedOrganization( planCommunity );
                assert org == null || org.isActual();
                return org == null ? "?" : org.getName();
        }
    }

    private Organization getFixedOrganization( PlanCommunity planCommunity ) {
        try {
            return planCommunity.getPlanService().find( Organization.class, fixedOrganizationId );
        } catch ( NotFoundException e ) {
            LOG.warn( "Organization not found at " + fixedOrganizationId );
            return null;
        }
    }

    public void setName( String name ) {
        assert fixedOrganizationId != -1;
        this.name = name;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId( long parentId ) {
        this.parentId = parentId;
    }

    public boolean isParentRegistered() {
        return parentRegistered;
    }

    public void setParentRegistered( boolean parentRegistered ) {
        this.parentRegistered = parentRegistered;
    }

    public List<OrganizationRegistration> getRegistrationList() {
        return registrationList;
    }

    public void setRegistrationList( List<OrganizationRegistration> registrationList ) {
        this.registrationList = registrationList;
    }

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof RegisteredOrganization ) {
            RegisteredOrganization other = (RegisteredOrganization)object;
            return fixedOrganizationId == other.getFixedOrganizationId()
                    && ChannelsUtils.bothNullOrEqual( name, other.getName( ) );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + new Long( fixedOrganizationId ).hashCode();
        if ( name != null ) hash = hash * 31 + name.hashCode();
        return hash;
    }


    public List<Job> getFixedJobs( PlanCommunity planCommunity ) {
        if ( isFixedOrganization() ) {
            Organization org = getFixedOrganization( planCommunity );
            if ( org != null )
                return Collections.unmodifiableList( org.getJobs() );
            else
                return new ArrayList<Job>(  );
        } else {
            return new ArrayList<Job>(  );
        }
    }

    public boolean isValid( PlanCommunity planCommunity ) {
        return !isFixedOrganization() || getFixedOrganization( planCommunity ) != null;
    }
}
