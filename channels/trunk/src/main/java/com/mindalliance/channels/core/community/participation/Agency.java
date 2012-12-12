package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.PlanCommunity;
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
public class Agency implements Nameable, Identifiable {

    // Only one of organization or organizationRegistration must be set.
    private Organization fixedOrganization;
    private OrganizationRegistration organizationRegistration;
    private String name;

    public Agency( Organization fixedOrganization ) {
        assert fixedOrganization.isActual();
        this.fixedOrganization = fixedOrganization;
        name = fixedOrganization.getName();
    }

    public Agency( OrganizationRegistration organizationRegistration, PlanCommunity planCommunity ) {
        this.organizationRegistration = organizationRegistration;
        name = organizationRegistration.getRegisteredOrganization().getName( planCommunity );
    }

    public Organization getFixedOrganization() {
        return fixedOrganization;
    }

    public OrganizationRegistration getOrganizationRegistration() {
        return organizationRegistration;
    }

    public boolean isRegistered() {
        assert ( fixedOrganization == null || organizationRegistration == null )
                && !( fixedOrganization == null && organizationRegistration == null );
        return organizationRegistration != null;
    }

    public List<Agent> getAgents( PlanCommunity planCommunity ) {
        Set<Agent> agents = new HashSet<Agent>();
        for ( Job job : getPlaceholderJobs( planCommunity ) ) {
            Agent agent = new Agent( job.getActor(), organizationRegistration, planCommunity );
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
        return isRegistered() ? organizationRegistration.getId() : fixedOrganization.getId();
    }

    @Override
    public String getDescription() {
        return isRegistered() ? organizationRegistration.getDescription() : fixedOrganization.getDescription();
    }

    @Override
    public String getTypeName() {
        return "organization";
    }

    @Override
    public boolean isModifiableInProduction() {
        return false;
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
        if ( isRegistered() ) {
            return organizationRegistration.getPlaceholderJobs( planCommunity );
        } else {
            return new ArrayList<Job>();
        }
    }

    public List<Job> getFixedJobs( PlanCommunity planCommunity ) {
        if ( isRegistered() ) {
            return organizationRegistration.getFixedJobs( planCommunity );
        } else {
            return fixedOrganization.getJobs();
        }
    }

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof Agency ) {
            Agency other = (Agency) object;
            return ChannelsUtils.bothNullOrEqual( fixedOrganization, other.getFixedOrganization() )
                    && ChannelsUtils.bothNullOrEqual( organizationRegistration, other.getOrganizationRegistration() );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        if ( fixedOrganization != null ) hash = hash * 31 + fixedOrganization.hashCode();
        if ( organizationRegistration != null ) hash = hash * 31 + organizationRegistration.hashCode();
        return hash;
    }
}
