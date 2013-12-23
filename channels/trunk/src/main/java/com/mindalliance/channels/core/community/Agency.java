package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.model.AbstractUnicastChannelable;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Waivable;
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
public class Agency extends AbstractUnicastChannelable implements Waivable {

    public static final Agency UNKNOWN = new Agency();
    private RegisteredOrganization registeredOrganization;
    private List<OrganizationParticipation> organizationParticipationList; // how the agency participates in the collaboration plan
    // computed and cached
    private String name;
    private String description;
    private String mission;
    private String parentName;
    private String address;
    private boolean editable = false;
    private List<Organization> planOrganizations; // can be multiple template organizations from participation
    private List<Agent> agents;

    public Agency() {
        name = "?";
        description = "Unknown agency";
        mission = "";
        organizationParticipationList = new ArrayList<OrganizationParticipation>();
        planOrganizations = new ArrayList<Organization>();
    }

    public Agency( String name ) {
        this();
        this.name = name;
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
        setChannels( registeredOrganizationService.getAllChannels( getRegisteredOrganization(), communityService ) );
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
        RegisteredOrganization registered = getRegisteredOrganization();
        if ( registered == null )
            return null;
        RegisteredOrganization parentRegistration = registered.getParent( communityService );
        if ( parentRegistration == null ) {
            return null;
        } else {
            return parentRegistration;
        }
    }

    public String getParentName() {       // can return null
        return parentName;
    }

    public List<OrganizationParticipation> getOrganizationParticipationList() {
        return organizationParticipationList;
    }

    public RegisteredOrganization getRegisteredOrganization() {
        return registeredOrganization;
    }

    public List<Agent> getAgents( CommunityService communityService ) {
        if ( agents == null ) {
            // There's an agent in an agency if the agency for each actor that has one or more jobs in fixed org
            // the agency represents (each agent represents an actor)
            // or that has one or more jobs in a placeholder org the agency participates as
            // (each agent represents a copy of the actor)
            Set<Agent> results = new HashSet<Agent>();
            List<Actor> actors = new ArrayList<Actor>(  );
            if ( isFixedOrganization() ) {
                Organization fixedOrg = getRegisteredOrganization().getFixedOrganization( communityService );
                actors.addAll( communityService.getPlanService().findAllActorsEmployedBy( fixedOrg ) );
            } else {
                for ( OrganizationParticipation organizationParticipation : getOrganizationParticipationList() ) {
                    actors.addAll( organizationParticipation.getAllActors( communityService ) );
                }
            }
            for ( Actor actor : actors ) {
                Agent agent = new Agent( actor, registeredOrganization, communityService );
                results.add( agent );
            }
            agents = Collections.unmodifiableList( new ArrayList<Agent>( results ) );
        }
        return agents;
    }

    public Place getJurisdiction( CommunityService communityService ) {
        if ( registeredOrganization != null )
            return registeredOrganization.getJurisdiction( communityService );
        else {
            return null;
        }
    }

    public Agency getTopAgency( CommunityService communityService ) {
        assert registeredOrganization != null;
        RegisteredOrganization topAgency = communityService.getParticipationManager()
                .getTopRegisteredOrganization( registeredOrganization, communityService );
        return new Agency( topAgency, communityService );
    }

    public Agency getParent( CommunityService communityService ) {
        if ( parentName != null ) {
            RegisteredOrganization registeredParent = getParentRegistration( communityService );
            if ( registeredParent != null ) {
                return new Agency( registeredParent, communityService );
            }
        }
        return null;
    }

    public List<Agency> ancestors( CommunityService communityService ) {
        List<Agency> ancestors = new ArrayList<Agency>();
        for ( RegisteredOrganization registered
                : communityService.getParticipationManager().ancestorsOf( getRegisteredOrganization(), communityService ) ) {
            ancestors.add( new Agency( registered, communityService ) );
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
        return ( Long.MAX_VALUE / 2 ) + registeredOrganization.getId();
    }

    public String getUid() {
        String uid = registeredOrganization.getUid();
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

    public boolean isFixedOrganization() {
        return getRegisteredOrganization().isFixedOrganization();
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
            return ChannelsUtils.areEqualOrNull( registeredOrganization, other.getRegisteredOrganization() );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        if ( registeredOrganization != null ) hash = hash * 31 + registeredOrganization.hashCode();
        return hash;
    }

    public boolean isParticipatingAsSelf() {
        return isFixedOrganization();
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
        return registeredOrganization != null
                && CollectionUtils.exists(
                ancestors( communityService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        RegisteredOrganization registration = ( (Agency) object ).getRegisteredOrganization();
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
        return getRegisteredOrganization().isLocal();
    }

    public boolean isGlobal() {
        return isFixedOrganization() || !getRegisteredOrganization().isLocal();
    }

    @SuppressWarnings("unchecked")
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

    public String getJobTitleOf( Agent agent, boolean includeEmployer, CommunityService communityService ) { // Assumes agent works for the agency
        Job namingJob = Agent.selectNamingJob( getAllJobsFor( agent, communityService ) );
        if ( namingJob == null ) {
            return agent.getActor().getName();
        } else if ( includeEmployer ) {
            if ( namingJob.isPrimary() ) {
                return namingJob.getTitle() + " at " + getName();
            } else {
                Job primaryJob = findPrimaryJob( namingJob, communityService );
                if ( primaryJob == null )
                    return namingJob.getTitle()
                            + " at "
                            + getName();
                else
                    return namingJob.getTitle()
                            + " at "
                            + getName()
                            + ", as "
                            + primaryJob.getTitle();
            }
        } else {
            return namingJob.getTitle();
        }
    }

    public boolean isLinked( Agent agent, CommunityService communityService ) {
        Job namingJob = Agent.selectNamingJob( getAllJobsFor( agent, communityService ) );
        return namingJob != null && namingJob.isLinked();
    }

    private Job findPrimaryJob( final Job linkedJob, CommunityService communityService ) {
        return (Job) CollectionUtils.find(
                communityService.getPlanService().findAllConfirmedJobs( linkedJob.getActor() ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Job job = (Job) object;
                        return job.isPrimary() && job.getActor().equals( linkedJob.getActor() );
                    }
                } );
    }

    public String getRegisteredOrganizationUid() {
        return registeredOrganization == null
                ? null
                : registeredOrganization.getUid();
    }

    public boolean participatesAs( Organization organization ) {
        return getPlanOrganizations().contains( organization );
    }

    public Organization findParticipatedAsOrganizationWithJob( final Job job ) {
        return (Organization) CollectionUtils.find(
                getPlanOrganizations(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Organization) object ).getJobs().contains( job );
                    }
                } );
    }

    // Waivable

    @Override
    public boolean isWaived( String detector, CommunityService communityService ) {
        return communityService.getPlanCommunity().hasIssueDetectionWaiver( this, detector );
    }

    @Override
    public void waiveIssueDetection( String detector, CommunityService communityService ) {
        communityService.getPlanCommunity().addIssueDetectionWaiver( this, detector );
    }

    @Override
    public void unwaiveIssueDetection( String detector, CommunityService communityService ) {
        communityService.getPlanCommunity().removeIssueDetectionWaiver( this, detector );
    }

}

