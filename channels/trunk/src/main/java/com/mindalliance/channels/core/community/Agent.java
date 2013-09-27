package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Nameable;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.db.data.communities.OrganizationParticipation;

/**
 * What a user participates as.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/12
 * Time: 10:12 AM
 */
public class Agent implements Nameable, Identifiable {

    private Actor actor;
    private OrganizationParticipation organizationParticipation;  // can be null
    private String name;

    public Agent( Actor actor ) {
        this.actor = actor;
        name = actor.getName();
    }

    public Agent( Actor actor,
                  OrganizationParticipation organizationParticipation,
                  CommunityService communityService ) {
        this.actor = actor;
        this.organizationParticipation = organizationParticipation;
        if ( organizationParticipation != null ) {
            String jobTitle = organizationParticipation.getJobTitle( actor, communityService );
            name = (jobTitle.isEmpty() ? actor.getName() : jobTitle)
                    + " at "
                    + new Agency( organizationParticipation, communityService ).getName();
        } else {
            name = actor.getName();
        }
    }
    public Actor getActor() {
        return actor;
    }

    public long getActorId() {
        return actor.getId();
    }

    public OrganizationParticipation getOrganizationParticipation() {
        return organizationParticipation;
    }

    /**
     * Is agent in registered organization.
     *
     * @return a boolean
     */
    public boolean isFromOrganizationParticipation() {
        return organizationParticipation != null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getClassLabel() {
        return "agents";
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
                && ( communityService.getPlanService().listActualEntities( Actor.class ).contains(  actor ))/*
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
                    && ChannelsUtils.areEqualOrNull( organizationParticipation, other.getOrganizationParticipation() );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + actor.hashCode();
        if ( organizationParticipation != null ) hash = hash * 31 + organizationParticipation.hashCode();
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

    public boolean isRegisteredInPlaceholder( Organization organization, CommunityService communityService ) {
        return organization.isPlaceHolder()
                && isFromOrganizationParticipation()
                && getOrganizationParticipation().getPlaceholderOrganization( communityService )
                .equals( organization );
    }

    public boolean isAnonymousParticipation() {
        return actor.isAnonymousParticipation();
    }

    public boolean isAnyNumberOfParticipants() {
        return actor != null && actor.isAnyNumberOfParticipants();
    }

    public String getOrganizationParticipationUid() {
        return organizationParticipation != null
                ? organizationParticipation.getUid()
                : null;
    }
}
