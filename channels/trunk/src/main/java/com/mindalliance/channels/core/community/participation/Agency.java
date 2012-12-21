package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.AbstractUnicastChannelable;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.Nameable;
import com.mindalliance.channels.core.model.Organization;
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

    // Only one of fixedOrganization or organizationParticipation or registeredOrganization must be set.
    private Organization fixedOrganization;
    private RegisteredOrganization registeredOrganization;
    private OrganizationParticipation organizationParticipation;
    private String name;  // computed and cached
    private String description;
    private String mission;
    private String parentName;
    private boolean editable = false;

    public Agency( Organization fixedOrganization ) {
        assert fixedOrganization.isActual();
        this.fixedOrganization = fixedOrganization;
        name = fixedOrganization.getName();
        description = fixedOrganization.getDescription();
        mission = fixedOrganization.getMission();
        parentName = fixedOrganization.getEffectiveParent() != null
                ? fixedOrganization.getEffectiveParent().getName()
                : null;
    }

    public Agency( OrganizationParticipation organizationParticipation, PlanCommunity planCommunity ) {
        this.organizationParticipation = organizationParticipation;
        name = organizationParticipation.getRegisteredOrganization().getName( planCommunity );
        description = organizationParticipation.getRegisteredOrganization().getEffectiveDescription( planCommunity );
        mission = organizationParticipation.getRegisteredOrganization().getEffectiveMission( planCommunity );
        parentName = organizationParticipation.getRegisteredOrganization().getParentName( planCommunity );
    }

    public Agency( RegisteredOrganization registeredOrganization, PlanCommunity planCommunity ) {
        this.registeredOrganization = registeredOrganization;
        name = registeredOrganization.getName( planCommunity );
        description = registeredOrganization.getEffectiveDescription( planCommunity );
        mission = registeredOrganization.getEffectiveMission( planCommunity );
        parentName = registeredOrganization.getParentName( planCommunity );
    }

    public Agency( Agency agency ) {
        fixedOrganization = agency.getFixedOrganization();
        registeredOrganization = agency.getRegisteredOrganization();
        organizationParticipation = agency.getOrganizationParticipation();
        name = agency.getName();
        description = agency.getDescription();
        mission = agency.getMission();
        parentName = agency.getParentName();
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

    public RegisteredOrganization getParentRegistration( PlanCommunity planCommunity ) {
        if ( isFixedOrganization() ) {
            return null;
        } else {
            RegisteredOrganization registered = getRegistration();
            RegisteredOrganization parentRegistration = registered.getParent();
            return parentRegistration == null ? null : parentRegistration;
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

    public boolean isRegisteredByCommunity() {
        if ( fixedOrganization != null ) return false;
        if ( registeredOrganization != null )
            return !registeredOrganization.isFixedOrganization();
        if ( organizationParticipation != null )
            return !organizationParticipation.getRegisteredOrganization().isFixedOrganization();
        else
            throw new IllegalStateException();
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

    @Override
    public String getClassLabel() {
        return getClass().getSimpleName();
    }

    @Override
    public long getId() {
        return fixedOrganization != null
                ? fixedOrganization.getId()
                : registeredOrganization != null
                ? registeredOrganization.getId()
                : organizationParticipation.getId();
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
        return "Agency " + getName();
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
}
