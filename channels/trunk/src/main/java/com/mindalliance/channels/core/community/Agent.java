package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.db.data.communities.RegisteredOrganization;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.List;

/**
 * What a user participates as.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/12
 * Time: 10:12 AM
 */
public class Agent extends AbstractWaivableIdentifiable {

    private Actor actor;
    private Agency agency;
    private String name;
    private boolean linked = false;

    // if is an actor in plan organization participating as placeholder from template
    public Agent( Actor actor,
                  RegisteredOrganization registeredOrganization,
                  CommunityService communityService ) {
        this.actor = actor;
        if ( registeredOrganization == null )
            throw new IllegalArgumentException(  );
        agency = new Agency( registeredOrganization, communityService );
        name = agency.getJobTitleOf( this, true, communityService );
        linked = agency.isLinked( this, communityService );
    }

    public Agent( Actor actor, String registeredOrganizationUid, CommunityService communityService ) {
        this( actor,
                communityService.getRegisteredOrganizationService().load( registeredOrganizationUid ),
                communityService );
    }

    public Actor getActor() {
        return actor;
    }

    public long getActorId() {
        return actor.getId();
    }

    public Agency getAgency() {
        return agency;
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean isLinked() {
        return linked;
    }

    public void setLinked( boolean linked ) {
        this.linked = linked;
    }

    @Override
    public String getClassLabel() {
        return "agents";
    }

    @Override
    public String getKindLabel() {
        return getTypeName();
    }

    @Override
    public String getUid() {
        return getRegisteredOrganizationUid() + "_" + getActorId();
    }

    @Override
    public long getId() {
        return actor.getId();
    }

    @Override
    public String getDescription() {
        return actor.getDescription();
    }

    @Override
    public String getTypeName() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean isModifiableInProduction() {
        return false;
    }

    public boolean isValid( CommunityService communityService ) {
        return actor != null
                && ( communityService.getModelService().listActualEntities( Actor.class ).contains( actor ) )/*
                && ( organizationParticipation == null || organizationParticipation.isValidAgent( this, planCommunity ) )*/;
    }

    public boolean isParticipationUserAssignable() {
        return actor != null && actor.isParticipationUserAssignable();
    }

    public int getMaxParticipation() {
        return actor == null ? 0 : actor.getMaxParticipation();
    }

    public boolean isSingularParticipation() {
        return actor != null && actor.isSingularParticipation();
    }

    public boolean isParticipationRestrictedToEmployed() {
        return actor != null && actor.isParticipationRestrictedToEmployed();
    }

    public boolean isOpenParticipation() {
        return actor != null && actor.isOpenParticipation();
    }

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof Agent ) {
            Agent other = (Agent) object;
            return actor.equals( other.getActor() )
                    && agency.equals( other.getAgency() );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + actor.hashCode();
        hash = hash * 31 + agency.hashCode();
        return hash;
    }

    public boolean isUnconstrainedParticipation() {
        return actor.isUnconstrainedParticipation();
    }

    public String getChannelsString() {
        return actor.getChannelsString();
    }

    public String toString() {
        return getName();
    }

    public String getActorName() {
        return actor.getName();
    }

    public boolean isSupervisedParticipation() {
        return actor.isSupervisedParticipation();
    }

    public String getRequirementsDescription( CommunityService communityService ) {
        return getActor().getRequirementsDescription( communityService.getPlan() );   // todo - COMMUNITY - move to community
    }

    public boolean isAnonymousParticipation() {
        return actor.isAnonymousParticipation();
    }

    public boolean isAnyNumberOfParticipants() {
        return actor != null && actor.isAnyNumberOfParticipants();
    }

    public static Job selectNamingJob( List<Job> actorJobs ) {
        if ( actorJobs.isEmpty() ) {
            return null;
        } else {
            Job job = (Job) CollectionUtils.find(
                    actorJobs,
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return ( (Job) object ).isPrimary();
                        }
                    } // pick the primary job, if any, to provide the title
            );
            if ( job == null ) { // no primary job - pick first job with an explicit job title
                job = (Job) CollectionUtils.find(
                        actorJobs,
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return !( (Job) object ).getRawTitle().isEmpty();
                            }
                        } );
            }
            if ( job == null )
                job = actorJobs.get( 0 );
            return job;
        }
    }

    public String getRegisteredOrganizationUid() {
        return getAgency().getRegisteredOrganizationUid();
    }
}
