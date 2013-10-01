package com.mindalliance.channels.db.data.communities;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.db.data.AbstractChannelsDocument;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Registration of an organization as an organization placeholder.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/19/13
 * Time: 2:30 PM
 */
@Document( collection = "communities" )
public class OrganizationParticipation extends AbstractChannelsDocument {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( OrganizationParticipation.class );

    private long placeholderOrgId;
    private String registeredOrganizationUid;


    public OrganizationParticipation() {
    }

    public OrganizationParticipation(
            String username,
            RegisteredOrganization registeredOrganization,
            Organization placeholder,
            PlanCommunity planCommunity ) {
        super( planCommunity.getUri(), planCommunity.getPlanUri(), planCommunity.getPlanVersion(), username );
        registeredOrganizationUid = registeredOrganization.getUid();
        this.placeholderOrgId = placeholder.getId();
    }

    public RegisteredOrganization getRegisteredOrganization( CommunityService communityService ) {
        return communityService.getParticipationManager().getRegisteredOrganization( registeredOrganizationUid );
    }

    public String getRegisteredOrganizationUid() {
        return registeredOrganizationUid;
    }

    public long getPlaceholderOrgId() {
        return placeholderOrgId;
    }

    public Organization getPlaceholderOrganization( CommunityService communityService ) {
        try {
            Organization organization = communityService.find( Organization.class, placeholderOrgId, getCreated() );
            return organization.isPlaceHolder() ? organization : null;
        } catch ( NotFoundException e ) {
            LOG.warn( "Placeholder organization not found at " + placeholderOrgId );
            return null;
        }
    }

    public Organization getOrganizationParticipatedAs( CommunityService communityService ) {
        try {
            return communityService.find( Organization.class, placeholderOrgId, getCreated() );
        } catch ( NotFoundException e ) {
            LOG.warn( "Placeholder organization not found at " + placeholderOrgId );
            return null;
        }
    }

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof OrganizationParticipation ) {
            OrganizationParticipation other = (OrganizationParticipation) object;
            return placeholderOrgId == other.getPlaceholderOrgId()
                    && registeredOrganizationUid.equals( other.getRegisteredOrganizationUid() );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + Long.valueOf( placeholderOrgId ).hashCode();
        hash = hash * 31 + registeredOrganizationUid.hashCode();
        return hash;
    }


    public List<Job> getFixedJobs( CommunityService communityService ) {
        RegisteredOrganization registeredOrganization = getRegisteredOrganization( communityService );
        return registeredOrganization != null
                ? registeredOrganization.getFixedJobs( communityService )
                : new ArrayList<Job>();
    }

    public List<Job> getPlaceholderJobs( CommunityService communityService ) {
        Organization placeholder = getPlaceholderOrganization( communityService );
        if ( placeholder != null ) {
            return placeholder.getJobs();
        } else {
            return new ArrayList<Job>();
        }
    }

    public String asString( CommunityService communityService ) {
        RegisteredOrganization registeredOrganization = getRegisteredOrganization( communityService );
        if ( registeredOrganization != null ) {
            StringBuilder sb = new StringBuilder();
            sb.append( getRegisteredOrganization( communityService ).asString( communityService ) );
            sb.append( " registered as " );
            sb.append( getPlaceholderOrganization( communityService ).getName() );
            return sb.toString();
        } else {
            return "?";
        }
    }

    @SuppressWarnings( "unchecked" )
    public String getJobTitle( final Actor actor, CommunityService communityService ) {
        RegisteredOrganization registeredOrganization = getRegisteredOrganization( communityService );
        if ( registeredOrganization != null ) {
            List<Job> jobs = registeredOrganization.isFixedOrganization()
                    ? getFixedJobs( communityService )
                    : getPlaceholderJobs( communityService );
            List<Job> actorJobs = (List<Job>) CollectionUtils.select(
                    jobs,
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return ( (Job) object ).getActor().equals( actor );
                        }
                    }
            );
            if ( actorJobs.isEmpty() ) {
                return "";
            } else {
                Job job = (Job)CollectionUtils.find(
                        actorJobs,
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return ((Job)object).isPrimary();
                            }
                        }
                );
                if ( job == null ) {
                    job = (Job)CollectionUtils.find(
                            actorJobs,
                            new Predicate() {
                                @Override
                                public boolean evaluate( Object object ) {
                                    return !((Job)object).getRawTitle().isEmpty();
                                }
                            } );
                }
                return job == null ? actorJobs.get(0).getTitle() : job.getTitle();
            }
        } else {
            return "?";
        }
    }

}
