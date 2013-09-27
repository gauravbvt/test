package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.model.AbstractUnicastChannelable;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.Nameable;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.db.data.communities.OrganizationParticipation;
import com.mindalliance.channels.db.data.communities.RegisteredOrganization;
import com.mindalliance.channels.db.services.communities.OrganizationParticipationService;
import com.mindalliance.channels.db.services.communities.RegisteredOrganizationService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An organization participating in a plan, either fixed or registered.
 * This is a transient object which state is set from database.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/12
 * Time: 10:49 AM
 */
public class Agency extends AbstractUnicastChannelable implements Nameable, Identifiable {

    public static final Agency UNKNOWN = new Agency();
    // Only one of fixedOrganization or registeredOrganization must be set.
    private Organization fixedOrganization;
    private RegisteredOrganization registeredOrganization; // registered into community, not from plan

    private List<OrganizationParticipation> organizationParticipationList; // how the agency participates in the community
    // computed and cached
    private String name;
    private String description;
    private String mission;
    private String parentName;
    private String address;
    private boolean editable = false;
    private List<Organization> planOrganizations; // can have multiple plan organizations from participation

    public Agency() {
        name = "?";
        description = "Unknown agency";
        mission = "";
        fixedOrganization = Organization.UNKNOWN;
        organizationParticipationList = new ArrayList<OrganizationParticipation>();
        planOrganizations = new ArrayList<Organization>();
    }

    public Agency( String name ) {
        this();
        this.name = name;
    }

    public Agency( Organization fixedOrganization, CommunityService communityService ) {
        assert fixedOrganization.isActual();
        this.fixedOrganization = fixedOrganization;
        initializeFromParticipation( communityService );
        name = fixedOrganization.getName();
        description = fixedOrganization.getDescription();
        mission = fixedOrganization.getMission();
        parentName = fixedOrganization.getParent() != null
                ? fixedOrganization.getParent().getName()
                : null;
    }

    public Agency( OrganizationParticipation organizationParticipation, CommunityService communityService ) {
        RegisteredOrganization registeredOrg = organizationParticipation.getRegisteredOrganization( communityService );
        if ( registeredOrg.isFixedOrganization() ) {
            fixedOrganization = registeredOrg.getFixedOrganization( communityService );
        } else {
            registeredOrganization = registeredOrg;
        }
        initializeFromParticipation( communityService );
        name = registeredOrg.getName( communityService );
        description = registeredOrg.getEffectiveDescription( communityService );
        mission = registeredOrg.getEffectiveMission( communityService );
        parentName = registeredOrg.getParentName( communityService );
    }

    public Agency( RegisteredOrganization registeredOrganization, CommunityService communityService ) {
        this.registeredOrganization = registeredOrganization;
        initializeFromParticipation( communityService );
        name = registeredOrganization.getName( communityService );
        description = registeredOrganization.getEffectiveDescription( communityService );
        mission = registeredOrganization.getEffectiveMission( communityService );
        parentName = registeredOrganization.getParentName( communityService );
    }

    public Agency( Agency agency ) {
        fixedOrganization = agency.getFixedOrganization();
        registeredOrganization = agency.getRegisteredOrganization();
        organizationParticipationList = agency.getOrganizationParticipationList();
        name = agency.getName();
        description = agency.getDescription();
        mission = agency.getMission();
        parentName = agency.getParentName();
        planOrganizations = agency.getPlanOrganizations();
        address = agency.getAddress();
    }

    private void initializeFromParticipation( CommunityService communityService ) {
        OrganizationParticipationService organizationParticipationService
                = communityService.getOrganizationParticipationService();
        planOrganizations = new ArrayList<Organization>();
        if ( registeredOrganization != null ) {
            organizationParticipationList = organizationParticipationService.findAllParticipationBy(
                    registeredOrganization,
                    communityService );
            address = registeredOrganization.getAddress();

        } else if ( fixedOrganization != null ) {
            organizationParticipationList = organizationParticipationService.findAllParticipationBy(
                    fixedOrganization,
                    communityService );
            planOrganizations.add( fixedOrganization );
            address = fixedOrganization.getFullAddress();

        } else {
            organizationParticipationList = new ArrayList<OrganizationParticipation>();
        }
        for ( OrganizationParticipation organizationParticipation : organizationParticipationList ) {
            Organization placeholder = organizationParticipation.getPlaceholderOrganization( communityService );
            if ( !planOrganizations.contains( placeholder ) )
                planOrganizations.add( placeholder );
        }
    }

    @Override
    public boolean isTransient() {
        return true;
    }

    @Override
    public boolean hasAddresses() {
        return true;
    }

    public void setEditable( boolean editable ) {
        this.editable = editable;
    }

    public void initChannels( RegisteredOrganizationService registeredOrganizationService,
                              CommunityService communityService ) {
        if ( isFixedOrganization() ) {
            setChannels( getFixedOrganization().getEffectiveChannels() );
        } else {
            setChannels( registeredOrganizationService.getAllChannels( getRegistration(), communityService ) );
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

    public List<Organization> getPlanOrganizations() {
        return planOrganizations;
    }

    public RegisteredOrganization getParentRegistration( CommunityService communityService ) {
        if ( isFixedOrganization() ) {
            return null;
        } else {
            RegisteredOrganization registered = getRegistration();
            if ( registered == null )
                return null;
            RegisteredOrganization parentRegistration = registered.getParent( communityService );
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

    public List<OrganizationParticipation> getOrganizationParticipationList() {
        return organizationParticipationList;
    }


    public RegisteredOrganization getRegistration() {
        return registeredOrganization;
    }

    public RegisteredOrganization getRegisteredOrganization() {
        return registeredOrganization;
    }

    public boolean isFixedOrganization() {
        return fixedOrganization != null;
    }

    public boolean isRegistered() {
        return !isFixedOrganization();
    }

    public boolean isRegisteredByCommunity() {
        return isRegistered() && isLocal();
    }

    public List<Agent> getAgents( CommunityService communityService ) {
        Set<Agent> agents = new HashSet<Agent>();
        for ( OrganizationParticipation organizationParticipation : getOrganizationParticipationList() ) {
            for ( Job job : getPlaceholderJobs( organizationParticipation, communityService ) ) {
                Agent agent = new Agent( job.getActor(), organizationParticipation, communityService );
                agents.add( agent );
            }
        }
        for ( Job job : getFixedJobs( communityService ) ) {   // if any
            Agent agent = new Agent( job.getActor() );
            agents.add( agent );
        }
        return Collections.unmodifiableList( new ArrayList<Agent>( agents ) );
    }

    public Place getJurisdiction( OrganizationParticipation participation, CommunityService communityService ) {
        if ( fixedOrganization != null )
            return fixedOrganization.getJurisdiction();
        else if ( registeredOrganization != null )
            return registeredOrganization.getJurisdiction( communityService );
        else {
            Organization placeholder = participation.getPlaceholderOrganization( communityService );
            if ( placeholder != null )
                return placeholder.getJurisdiction();
        }
        return null;
    }

    public Agency getTopAgency( CommunityService communityService ) {
        if ( isFixedOrganization() ) {
            Organization org = getFixedOrganization();
            return new Agency( org.getTopOrganization(), communityService );
        } else {
            assert registeredOrganization != null;
            RegisteredOrganization topAgency = communityService.getParticipationManager()
                    .getTopRegisteredOrganization( registeredOrganization, communityService );
            return new Agency( topAgency, communityService );
        }
    }

    public Agency getParent( CommunityService communityService ) {
        if ( parentName != null ) {
            if ( isFixedOrganization() ) {
                return new Agency( getFixedOrganization().getParent(), communityService );
            } else {
                RegisteredOrganization registeredParent = getParentRegistration( communityService );
                if ( registeredParent != null ) {
                    return new Agency( registeredParent, communityService );
                }
            }
        }
        return null;
    }

    public List<Agency> ancestors( CommunityService communityService ) {
        List<Agency> ancestors = new ArrayList<Agency>();
        if ( isRegistered() ) {
            for ( RegisteredOrganization registered
                    : communityService.getParticipationManager().ancestorsOf( getRegistration(), communityService ) ) {
                ancestors.add( new Agency( registered, communityService ) );
            }
        } else {
            for ( Organization org : getFixedOrganization().ancestors() ) {
                ancestors.add( new Agency( org, communityService ) );
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
                : ( Long.MAX_VALUE / 2 ) + registeredOrganization.getId();
    }

    public String getUid() {
        String uid = isFixedOrganization()
                ? Long.toString( getFixedOrganization().getId() )
                : registeredOrganization.getUid();
        assert !uid.contains( "," ); // as per assumption in RequirementRelationship
        return uid;
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


    public List<Job> getFixedJobs( CommunityService communityService ) {
        List<Job> jobs = new ArrayList<Job>();
        for ( OrganizationParticipation organizationParticipation : getOrganizationParticipationList() ) {
            jobs.addAll( organizationParticipation.getFixedJobs( communityService ) );
        }
        if ( fixedOrganization != null ) {
            jobs.addAll( fixedOrganization.getEffectiveJobs() );
        }
        return jobs;
    }

    public List<Job> getAllJobs( CommunityService communityService ) {
        List<Job> jobs = new ArrayList<Job>();
        jobs.addAll( getFixedJobs( communityService ) );
        for ( OrganizationParticipation organizationParticipation : getOrganizationParticipationList() ) {
            jobs.addAll( getPlaceholderJobs( organizationParticipation, communityService ) );
        }
        return jobs;
    }

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof Agency ) {
            Agency other = (Agency) object;
            return ChannelsUtils.areEqualOrNull( fixedOrganization, other.getFixedOrganization() )
                    && ChannelsUtils.areEqualOrNull( registeredOrganization, other.getRegisteredOrganization() );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        if ( fixedOrganization != null ) hash = hash * 31 + fixedOrganization.hashCode();
        if ( registeredOrganization != null ) hash = hash * 31 + registeredOrganization.hashCode();
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

    /////////

    public List<Long> getOrganizationIds() {
        List<Long> ids = new ArrayList<Long>();
        for ( Organization organization : getPlanOrganizations() ) {
            ids.add( organization.getId() );
        }
        return ids;
    }

    public boolean hasAncestor( final Agency agency, final CommunityService communityService ) {
        return CollectionUtils.exists(
                ancestors( communityService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return object.equals( agency );
                    }
                }
        );
    }

    public boolean hasAncestorWithPlaceholder( final Organization placeholder, final CommunityService communityService ) {
        final OrganizationParticipationService organizationParticipationService
                = communityService.getOrganizationParticipationService();
        return isRegisteredByCommunity()
                && CollectionUtils.exists(
                ancestors( communityService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        RegisteredOrganization registration = ( (Agency) object ).getRegistration();
                        return registration != null
                                && organizationParticipationService.isAgencyRegisteredAs( registration, placeholder, communityService );
                    }
                }
        );
    }

    public boolean hasAssignableAgents( CommunityService communityService ) {
        return !getAgents( communityService ).isEmpty();
    }

    public List<Job> getPlaceholderJobs(
            OrganizationParticipation organizationParticipation,
            CommunityService communityService ) {
        return organizationParticipation.getPlaceholderJobs( communityService );
    }

    public boolean participatesAsPlaceholder( final Organization placeholder ) {
        return CollectionUtils.exists(
                getOrganizationParticipationList(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (OrganizationParticipation) object ).getPlaceholderOrgId() == placeholder.getId();
                    }
                }
        );
    }

    public List<Organization> getPlaceholders( CommunityService communityService ) {
        List<Organization> placeholders = new ArrayList<Organization>();
        for ( OrganizationParticipation organizationParticipation : getOrganizationParticipationList() ) {
            Organization placeholder = organizationParticipation.getPlaceholderOrganization( communityService );
            if ( placeholder != null && !placeholders.contains( placeholder ) )
                placeholders.add( placeholder );
        }
        return placeholders;
    }

    public boolean isParticipatingAsAPlaceholder() {
        return !getOrganizationParticipationList().isEmpty();
    }

    // Agency represents an organization only visible to the plan community.
    public boolean isLocal() {
        return isFixedOrganization() || getRegisteredOrganization().isLocal();
    }

    public boolean isGlobal() {
        return !isFixedOrganization() && !getRegisteredOrganization().isLocal();
    }

    @SuppressWarnings( "unchecked" )
    public List<Job> getAllJobsFor( Agent agent, CommunityService communityService ) {
        final Actor actor = agent.getActor();
        return (List<Job>) CollectionUtils.select(
                getAllJobs( communityService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Job) object ).getActor().equals( actor );
                    }
                }
        );
    }

}

