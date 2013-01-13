package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.AbstractUnicastChannelable;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.Nameable;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.util.ChannelsUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An organization participating in a plan, either fixed or registered.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/12
 * Time: 10:49 AM
 */
public class Agency extends AbstractUnicastChannelable implements Nameable, Identifiable {

    public static final Agency UNKNOWN = new Agency();
    // Only one of fixedOrganization or organizationParticipation or registeredOrganization must be set.
    private Organization fixedOrganization;
    private RegisteredOrganization registeredOrganization;
    private OrganizationParticipation organizationParticipation;
    private String name;  // computed and cached
    private String description;
    private String mission;
    private String parentName;
    private String address;
    private boolean editable = false;
    private Organization planOrganization;

    public Agency() {
        name = "?";
        description = "Unknown agency";
        mission = "";
        fixedOrganization = Organization.UNKNOWN;
    }

    public Agency( Organization fixedOrganization ) {
        assert fixedOrganization.isActual();
        this.fixedOrganization = fixedOrganization;
        name = fixedOrganization.getName();
        description = fixedOrganization.getDescription();
        mission = fixedOrganization.getMission();
        parentName = fixedOrganization.getParent() != null
                ? fixedOrganization.getParent().getName()
                : null;
        planOrganization = fixedOrganization;
        address = planOrganization.getFullAddress();
    }

    public Agency( OrganizationParticipation organizationParticipation, PlanCommunity planCommunity ) {
        this.organizationParticipation = organizationParticipation;
        RegisteredOrganization registeredOrg = organizationParticipation.getRegisteredOrganization();
        name = registeredOrg.getName( planCommunity );
        description = registeredOrg.getEffectiveDescription( planCommunity );
        mission = registeredOrg.getEffectiveMission( planCommunity );
        parentName = registeredOrg.getParentName( planCommunity );
        planOrganization = findPlanOrganization( planCommunity );
        address = registeredOrg.getAddress();
    }

    public Agency( RegisteredOrganization registeredOrganization, PlanCommunity planCommunity ) {
        this.registeredOrganization = registeredOrganization;
        name = registeredOrganization.getName( planCommunity );
        description = registeredOrganization.getEffectiveDescription( planCommunity );
        mission = registeredOrganization.getEffectiveMission( planCommunity );
        parentName = registeredOrganization.getParentName( planCommunity );
        planOrganization = findPlanOrganization( planCommunity );
        address = registeredOrganization.getAddress();
    }

    public Agency( Agency agency ) {
        fixedOrganization = agency.getFixedOrganization();
        registeredOrganization = agency.getRegisteredOrganization();
        organizationParticipation = agency.getOrganizationParticipation();
        name = agency.getName();
        description = agency.getDescription();
        mission = agency.getMission();
        parentName = agency.getParentName();
        planOrganization = agency.getPlanOrganization();
        address = agency.getAddress();
    }

    public void setEditable( boolean editable ) {
        this.editable = editable;
    }

    public void initChannels( RegisteredOrganizationService registeredOrganizationService,
                              PlanCommunity planCommunity ) {
        if ( isFixedOrganization() ) {
            setChannels( getFixedOrganization().getEffectiveChannels() );
        } else {
            setChannels( registeredOrganizationService.getAllChannels( getRegistration(), planCommunity ) );
        }
    }

    public void setName( String name ) {
        this.name = name;
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

    public Organization getPlanOrganization() {
        return planOrganization;
    }

    public RegisteredOrganization getParentRegistration( PlanCommunity planCommunity ) {
        if ( isFixedOrganization() ) {
            return null;
        } else {
            RegisteredOrganization registered = getRegistration();
            RegisteredOrganization parentRegistration = registered.getParent();
            if ( parentRegistration == null ) {
                return null;
            } else {
                return parentRegistration;
            }
        }
    }

    public String getParentName() {       // can return null
        return parentName;
    }

    public Organization getFixedOrganization() {
        return fixedOrganization;
    }

    public OrganizationParticipation getOrganizationParticipation() {
        return organizationParticipation;
    }

    public RegisteredOrganization getRegistration() {
        return fixedOrganization != null
                ? null
                : registeredOrganization != null
                ? registeredOrganization
                : organizationParticipation.getRegisteredOrganization();
    }

    public RegisteredOrganization getRegisteredOrganization() {
        return registeredOrganization;
    }

    public boolean isFixedOrganization() {
        return fixedOrganization != null;
    }

    public boolean hasPlaceholder( PlanCommunity planCommunity ) {
        return organizationParticipation != null
                && organizationParticipation.getPlaceholderOrganization( planCommunity ) != null;
    }

    public boolean isRegisteredByCommunity() {
        if ( fixedOrganization != null ) return false;
        if ( registeredOrganization != null )
            return !registeredOrganization.isFixedOrganization();
        if ( organizationParticipation != null )
            return !organizationParticipation.getRegisteredOrganization().isFixedOrganization();
        else
            throw new IllegalStateException();
    }

    public boolean isParticipatingAsPlaceholder() {
        return organizationParticipation != null;
    }

    public List<Agent> getAgents( PlanCommunity planCommunity ) {
        Set<Agent> agents = new HashSet<Agent>();
        for ( Job job : getPlaceholderJobs( planCommunity ) ) {
            Agent agent = new Agent( job.getActor(), organizationParticipation, planCommunity );
            agents.add( agent );
        }
        for ( Job job : getFixedJobs( planCommunity ) ) {   // if any
            Agent agent = new Agent( job.getActor() );
            agents.add( agent );
        }
        return Collections.unmodifiableList( new ArrayList<Agent>( agents ) );
    }

    public Place getJurisdiction( PlanCommunity planCommunity ) {
        if ( fixedOrganization != null )
            return fixedOrganization.getJurisdiction();
        else if ( registeredOrganization != null )
            return registeredOrganization.getJurisdiction( planCommunity );
        else {
            Organization placeholder = organizationParticipation.getPlaceholderOrganization( planCommunity );
            if ( placeholder != null )
                return placeholder.getJurisdiction();
        }
        return null;
    }

    public Agency getTopAgency( PlanCommunity planCommunity ) {
        if ( isFixedOrganization() ) {
            Organization org = getFixedOrganization();
            return new Agency( org.getTopOrganization() );
        } else {
            RegisteredOrganization regOrg = getEffectiveRegistereOrganization();
            assert regOrg != null;
            RegisteredOrganization topAgency = planCommunity.getParticipationManager()
                    .getTopRegisteredOrganization( regOrg, planCommunity );
            return new Agency( topAgency, planCommunity );
        }
    }

    private RegisteredOrganization getEffectiveRegistereOrganization() {
        return registeredOrganization != null
                ? registeredOrganization
                : organizationParticipation != null
                ? organizationParticipation.getRegisteredOrganization()
                : null;
    }

    private Organization findPlanOrganization( PlanCommunity planCommunity ) {
        if ( isFixedOrganization() ) {
            return getFixedOrganization();
        } else {
            RegisteredOrganization registered = getEffectiveRegistereOrganization();
            if ( registered != null ) {
                return registered.getFixedOrganization( planCommunity );
            } else {
                return null;
            }
        }
    }

    public Agency getParent( PlanCommunity planCommunity ) {
        if ( parentName != null ) {
            if ( isFixedOrganization() ) {
                return new Agency( getFixedOrganization().getParent() );
            } else {
                RegisteredOrganization registeredParent = getParentRegistration( planCommunity );
                if ( registeredParent != null ) {
                    return new Agency(registeredParent, planCommunity );
                }
            }
        }
        return null;
    }

    public List<Agency> ancestors( PlanCommunity planCommunity ) {
        List<Agency> ancestors = new ArrayList<Agency>(  );
        if ( isRegisteredByCommunity() ) {
            for ( RegisteredOrganization registered
                    : planCommunity.getParticipationManager().ancestorsOf( this.getRegistration(), planCommunity ) ) {
                ancestors.add( new Agency( registered, planCommunity ) );
            }
        } else {
            for ( Organization org : getPlanOrganization().ancestors() ) {
                ancestors.add( new Agency( org ) );
            }
        }
        return ancestors;
    }


    ////////////////////////

    @Override
    public String getClassLabel() {
        return getClass().getSimpleName();
    }

    @Override
    public long getId() {
        return isFixedOrganization()
                ? getFixedOrganization().getId()
                : organizationParticipation != null
                    ? organizationParticipation.getId() * -1
                    : 0;
    }

    @Override
    public String getTypeName() {
        return "organization";
    }

    @Override
    public boolean isModifiableInProduction() {
        return !isFixedOrganization() && editable;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Get the jobs inherited from placeholder organization.
     *
     * @param planCommunity a plan community
     * @return a list of jobs
     */
    public List<Job> getPlaceholderJobs( PlanCommunity planCommunity ) {
        if ( organizationParticipation != null ) {
            return organizationParticipation.getPlaceholderJobs( planCommunity );
        } else {
            return new ArrayList<Job>();
        }
    }

    public List<Job> getFixedJobs( PlanCommunity planCommunity ) {
        if ( organizationParticipation != null ) {
            return organizationParticipation.getFixedJobs( planCommunity );
        } else if ( fixedOrganization != null ) {
            return fixedOrganization.getJobs();
        } else {
            return new ArrayList<Job>();
        }
    }

    public List<Job> getAllJobs( PlanCommunity planCommunity ) {
        List<Job> jobs = new ArrayList<Job>(  );
        jobs.addAll( getFixedJobs( planCommunity ) );
        jobs.addAll( getPlaceholderJobs( planCommunity ) );
        return jobs;
    }

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof Agency ) {
            Agency other = (Agency) object;
            return ChannelsUtils.areEqualOrNull( fixedOrganization, other.getFixedOrganization() )
                    && ChannelsUtils.areEqualOrNull( organizationParticipation, other.getOrganizationParticipation() )
                    && ChannelsUtils.areEqualOrNull( registeredOrganization, other.getRegisteredOrganization() );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        if ( fixedOrganization != null ) hash = hash * 31 + fixedOrganization.hashCode();
        if ( organizationParticipation != null ) hash = hash * 31 + organizationParticipation.hashCode();
        if ( registeredOrganization != null ) hash = hash * 31 + getRegisteredOrganization().hashCode();
        return hash;
    }

    public boolean isParticipatingAsSelf() {
        return fixedOrganization != null && !fixedOrganization.isPlaceHolder();
    }

    public String toString() {
        return getName();
    }

    public void setParentName( String val ) {
         parentName = val == null || val.isEmpty()
                 ? null
                 : val;
    }

    // Channelable

    @Override
    public boolean canBeLocked() {
        return false;
    }

    @Override
    public boolean canSetChannels() {
        return !isFixedOrganization() && isModifiableInProduction();
    }

    @Override
    public boolean isModelObject() {
        return false;
    }

    public Long getOrganizationId() {
        return getPlanOrganization() != null ? getPlanOrganization().getId() : null;
    }

    public Organization getPlaceholder( PlanCommunity planCommunity ) {
        if ( organizationParticipation != null ) {
            return organizationParticipation.getPlaceholderOrganization(  planCommunity );
        } else {
            return null;
        }
    }

    public Organization getOrganizationParticipatedAs( PlanCommunity planCommunity ) {
        if ( organizationParticipation != null ) {
            return organizationParticipation.getOrganizationParticipatedAs(  planCommunity );
        } else {
            return null;
        }
    }

    public long getPlaceholderId() {
        return organizationParticipation.getPlaceholderOrgId();
    }
}
