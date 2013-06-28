package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.orm.model.AbstractPersistentChannelsObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.CascadeType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * Registration of an organization as an organization placeholder.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/3/12
 * Time: 2:35 PM
 */
// @Entity
public class OrganizationParticipation extends AbstractPersistentChannelsObject {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( OrganizationParticipation.class );

    private long placeholderOrgId;
    @ManyToOne
    private RegisteredOrganization registeredOrganization;

    @Transient
    @OneToMany( mappedBy = "organizationParticipation", cascade = CascadeType.ALL )
    private List<UserParticipation> userParticipationList;

    @Transient
    @OneToMany( mappedBy = "organizationParticipation", cascade = CascadeType.ALL )
    private List<UserParticipationConfirmation> userParticipationConfirmationList;

    public OrganizationParticipation() {
    }

    public OrganizationParticipation(
            String username,
            RegisteredOrganization registeredOrganization,
            Organization placeholder,
            PlanCommunity planCommunity ) {
        super( planCommunity.getUri(), planCommunity.getPlanUri(), planCommunity.getPlanVersion(), username );
        this.registeredOrganization = registeredOrganization;
        this.placeholderOrgId = placeholder.getId();
    }

    public RegisteredOrganization getRegisteredOrganization() {
        return registeredOrganization;
    }

    public long getPlaceholderOrgId() {
        return placeholderOrgId;
    }

    public List<UserParticipation> getUserParticipationList() {
        return userParticipationList;
    }

    public void setUserParticipationList( List<UserParticipation> userParticipationList ) {
        this.userParticipationList = userParticipationList;
    }

    public List<UserParticipationConfirmation> getUserParticipationConfirmationList() {
        return userParticipationConfirmationList;
    }

    public void setUserParticipationConfirmationList( List<UserParticipationConfirmation> userParticipationConfirmationList ) {
        this.userParticipationConfirmationList = userParticipationConfirmationList;
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
                    && registeredOrganization.equals( other.getRegisteredOrganization() );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + Long.valueOf( placeholderOrgId ).hashCode();
        hash = hash * 31 + registeredOrganization.hashCode();
        return hash;
    }


    public List<Job> getFixedJobs( CommunityService communityService ) {
        return registeredOrganization.getFixedJobs( communityService );
    }

 /*   public boolean isValidAgent( final Agent agent, PlanCommunity planCommunity ) {
        return CollectionUtils.exists(
                new Agency( this, planCommunity ).getAgents( planCommunity ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ((Agent)object).equals( agent );
                    }
                }
        );
    }
*/
    public List<Job> getPlaceholderJobs( CommunityService communityService ) {
        Organization placeholder = getPlaceholderOrganization( communityService );
        if ( placeholder != null ) {
            return placeholder.getJobs();
        } else {
            return new ArrayList<Job>(  );
        }
    }

    public String asString( CommunityService communityService) {
        StringBuilder sb = new StringBuilder(  );
        sb.append( getRegisteredOrganization().getName( communityService ) );
        sb.append( " registered as " );
        sb.append( getPlaceholderOrganization( communityService ).getName() );
        return sb.toString();
    }

    public String getJobTitle( final Actor actor, CommunityService communityService ) {
        List<Job> jobs = getRegisteredOrganization().isFixedOrganization()
                            ? getFixedJobs( communityService )
                            : getPlaceholderJobs( communityService );
        Job job = (Job)CollectionUtils.find(
                jobs,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ((Job)object).getActor().equals( actor );
                    }
                }
        );
        return job == null ? "" : job.getTitle();
    }
}
