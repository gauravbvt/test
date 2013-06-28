package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.model.AbstractUnicastChannelable;
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

    public Agency( String name ) {
        this();
        this.name = name;
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

    public Agency( OrganizationParticipation organizationParticipation, CommunityService communityService ) {
        this.organizationParticipation = organizationParticipation;
        RegisteredOrganization registeredOrg = organizationParticipation.getRegisteredOrganization( communityService );
        name = registeredOrg.getName( communityService );
        description = registeredOrg.getEffectiveDescription( communityService );
        mission = registeredOrg.getEffectiveMission( communityService );
        parentName = registeredOrg.getParentName( communityService );
        planOrganization = findPlanOrganization( communityService );
        address = registeredOrg.getAddress();
    }

    public Agency( RegisteredOrganization registeredOrganization, CommunityService communityService ) {
        this.registeredOrganization = registeredOrganization;
        name = registeredOrganization.getName( communityService );
        description = registeredOrganization.getEffectiveDescription( communityService );
        mission = registeredOrganization.getEffectiveMission( communityService );
        parentName = registeredOrganization.getParentName( communityService );
        planOrganization = findPlanOrganization( communityService );
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
            setChannels( registeredOrganizationService.getAllChannels( getRegistration( communityService ), communityService ) );
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

    public RegisteredOrganization getParentRegistration( CommunityService communityService ) {
        if ( isFixedOrganization() ) {
            return null;
        } else {
            RegisteredOrganization registered = getRegistration( communityService );
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

    public OrganizationParticipation getOrganizationParticipation() {
        return organizationParticipation;
    }

    public RegisteredOrganization getRegistration( CommunityService communityService ) {
        return fixedOrganization != null
                ? null
                : registeredOrganization != null
                ? registeredOrganization
                : organizationParticipation.getRegisteredOrganization( communityService );
    }

    public RegisteredOrganization getRegisteredOrganization() {
        return registeredOrganization;
    }

    public boolean isFixedOrganization() {
        return fixedOrganization != null;
    }

    public boolean hasPlaceholder( CommunityService communityService ) {
        return organizationParticipation != null
                && organizationParticipation.getPlaceholderOrganization( communityService ) != null;
    }

    public boolean isRegisteredByCommunity( CommunityService communityService ) {
        if ( fixedOrganization != null ) return false;
        if ( registeredOrganization != null )
            return !registeredOrganization.isFixedOrganization();
        if ( organizationParticipation != null )
            return !organizationParticipation.getRegisteredOrganization(communityService ).isFixedOrganization();
        else
            throw new IllegalStateException();
    }

    public boolean isParticipatingAsPlaceholder() {
        return organizationParticipation != null;
    }

    public List<Agent> getAgents( CommunityService communityService ) {
        Set<Agent> agents = new HashSet<Agent>();
        for ( Job job : getPlaceholderJobs( communityService ) ) {
            Agent agent = new Agent( job.getActor(), organizationParticipation, communityService );
            agents.add( agent );
        }
        for ( Job job : getFixedJobs( communityService ) ) {   // if any
            Agent agent = new Agent( job.getActor() );
            agents.add( agent );
        }
        return Collections.unmodifiableList( new ArrayList<Agent>( agents ) );
    }

    public Place getJurisdiction( CommunityService communityService ) {
        if ( fixedOrganization != null )
            return fixedOrganization.getJurisdiction();
        else if ( registeredOrganization != null )
            return registeredOrganization.getJurisdiction( communityService );
        else {
            Organization placeholder = organizationParticipation.getPlaceholderOrganization( communityService );
            if ( placeholder != null )
                return placeholder.getJurisdiction();
        }
        return null;
    }

    public Agency getTopAgency( CommunityService communityService ) {
        if ( isFixedOrganization() ) {
            Organization org = getFixedOrganization();
            return new Agency( org.getTopOrganization() );
        } else {
            RegisteredOrganization regOrg = getEffectiveRegistereOrganization( communityService );
            assert regOrg != null;
            RegisteredOrganization topAgency = communityService.getParticipationManager()
                    .getTopRegisteredOrganization( regOrg, communityService );
            return new Agency( topAgency, communityService );
        }
    }

    private RegisteredOrganization getEffectiveRegistereOrganization( CommunityService communityService ) {
        return registeredOrganization != null
                ? registeredOrganization
                : organizationParticipation != null
                ? organizationParticipation.getRegisteredOrganization( communityService )
                : null;
    }

    private Organization findPlanOrganization( CommunityService communityService ) {
        if ( isFixedOrganization() ) {
            return getFixedOrganization();
        } else if ( isParticipatingAsPlaceholder() ) {
            return getPlaceholder( communityService );
        } else {
            return null;
        }
    }

    public Agency getParent( CommunityService communityService ) {
        if ( parentName != null ) {
            if ( isFixedOrganization() ) {
                return new Agency( getFixedOrganization().getParent() );
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
        if ( isRegisteredByCommunity( communityService ) ) {
            for ( RegisteredOrganization registered
                    : communityService.getParticipationManager().ancestorsOf( getRegistration( communityService ), communityService ) ) {
                ancestors.add( new Agency( registered, communityService ) );
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
                ? ( Long.MAX_VALUE / 2 ) + organizationParticipation.getId()
                : 0;
    }

    public String getUid() {
        String uid = isFixedOrganization()
                ? Long.toString( getFixedOrganization().getId() )
                : organizationParticipation != null
                ? organizationParticipation.getUid()
                : "0";
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

    /**
     * Get the jobs inherited from placeholder organization.
     *
     * @param communityService a plan community service
     * @return a list of jobs
     */
    public List<Job> getPlaceholderJobs( CommunityService communityService ) {
        if ( organizationParticipation != null ) {
            return organizationParticipation.getPlaceholderJobs( communityService );
        } else {
            return new ArrayList<Job>();
        }
    }

    public List<Job> getFixedJobs( CommunityService communityService ) {
        if ( organizationParticipation != null ) {
            return organizationParticipation.getFixedJobs( communityService );
        } else if ( fixedOrganization != null ) {
            return fixedOrganization.getJobs();
        } else {
            return new ArrayList<Job>();
        }
    }

    public List<Job> getAllJobs( CommunityService communityService ) {
        List<Job> jobs = new ArrayList<Job>();
        jobs.addAll( getFixedJobs( communityService ) );
        jobs.addAll( getPlaceholderJobs( communityService ) );
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

    /////////

    public Long getOrganizationId() {
        return getPlanOrganization() != null ? getPlanOrganization().getId() : null;
    }

    public Organization getPlaceholder( CommunityService communityService ) {
        if ( organizationParticipation != null ) {
            return organizationParticipation.getPlaceholderOrganization( communityService );
        } else {
            return null;
        }
    }

    public Organization getOrganizationParticipatedAs( CommunityService communityService ) {
        if ( organizationParticipation != null ) {
            return organizationParticipation.getOrganizationParticipatedAs( communityService );
        } else {
            return null;
        }
    }

    public long getPlaceholderId() {
        return organizationParticipation.getPlaceholderOrgId();
    }

    public boolean hasAncestor( final Agency agency, final CommunityService communityService ) {
        return CollectionUtils.exists(
                ancestors( communityService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Agency ancestor = (Agency) object;
                        return ancestor.sameAs( agency, communityService );
                    }
                }
        );
    }

    private boolean sameAs( Agency other, CommunityService communityService ) {
        return this.equals( other )
                || ( getRegistration(communityService) != null
                && other.getRegistration(communityService) != null
                && getRegistration(communityService).equals( other.getRegistration(communityService) ) );
    }

    public boolean hasAncestorWithPlaceholder( final Organization placeholder, final CommunityService communityService ) {
        final OrganizationParticipationService organizationParticipationService
                = communityService.getOrganizationParticipationService();
        return isRegisteredByCommunity(communityService)
                && CollectionUtils.exists(
                ancestors( communityService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        RegisteredOrganization registration = ( (Agency) object ).getRegistration(communityService);
                        return registration != null
                                && organizationParticipationService.isAgencyRegisteredAs( registration, placeholder, communityService );
                    }
                }
        );
    }
}

