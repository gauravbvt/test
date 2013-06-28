package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Organization participation service implementation.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/3/12
 * Time: 8:40 PM
 */
public class OrganizationParticipationServiceImpl
        extends GenericSqlServiceImpl<OrganizationParticipation, Long>
        implements OrganizationParticipationService {

//    @Autowired
    private RegisteredOrganizationService registeredOrganizationService;

//    @Autowired
    private UserParticipationService userParticipationService;

    public OrganizationParticipationServiceImpl() {
    }

    @Override
    @Transactional( readOnly = true )
    public List<Agency> listParticipatingAgencies( CommunityService communityService ) {
        Set<Agency> agencies = new HashSet<Agency>();
        for ( OrganizationParticipation orgParticipation : getAllOrganizationParticipations( communityService ) ) {
            if ( isValid( orgParticipation, communityService ) ) {
                Agency agency = new Agency( /*orgParticipation, communityService*/ );
                agencies.add( agency );
            }
        }
        return Collections.unmodifiableList( new ArrayList<Agency>( agencies ) );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public List<OrganizationParticipation> getAllOrganizationParticipations( CommunityService communityService ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", communityService.getPlanCommunity().getUri() ) );
        return validate( (List<OrganizationParticipation>) criteria.list(), communityService );
    }

    @Override
    @Transactional( readOnly = true )
    public boolean isValid( OrganizationParticipation orgParticipation, CommunityService communityService ) {
        if ( orgParticipation == null ) return false;
        Organization placeholder = orgParticipation.getPlaceholderOrganization( communityService );
        return placeholder != null
                && placeholder.isPlaceHolder()
                && orgParticipation.getRegisteredOrganization().isValid( communityService );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public List<Agency> listAgenciesParticipatingAs( Organization placeholder, CommunityService communityService ) {
        if ( placeholder.isPlaceHolder() ) {
            Session session = getSession();
            Criteria criteria = session.createCriteria( getPersistentClass() );
            criteria.add( Restrictions.eq( "communityUri", communityService.getPlanCommunity().getUri() ) );
            criteria.add( Restrictions.eq( "placeholderOrgId", placeholder.getId() ) );
            List<Agency> agencies = new ArrayList<Agency>();
            for ( OrganizationParticipation orgParticipation
                    : validate( (List<OrganizationParticipation>) criteria.list(), communityService ) ) {
                agencies.add( new Agency( /*orgParticipation, communityService*/ ) );
            }
            return agencies;
        } else {
            return new ArrayList<Agency>();
        }
    }

    @Override
    @Transactional( readOnly = true )
    public boolean canUnassignOrganizationFrom(
            ChannelsUser user,
            Organization placeholder,
            CommunityService communityService ) {
        return placeholder.isPlaceHolder()
                && communityService.isCustodianOf( user, placeholder );
    }

    @Override
    @Transactional
    public OrganizationParticipation assignOrganizationAs(
            ChannelsUser user,
            RegisteredOrganization registeredOrganization,
            Organization placeholder,
            CommunityService communityService ) {
        if ( communityService.isCustodianOf( user, placeholder ) ) {
            if ( !isAgencyRegisteredAs( registeredOrganization, placeholder, communityService ) ) {
                OrganizationParticipation registration = new OrganizationParticipation(
                        user.getUsername(),
                        registeredOrganization,
                        placeholder,
                        communityService.getPlanCommunity() );
                save( registration );
                communityService.clearCache();
                return registration;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


    @SuppressWarnings( "unchecked" )
    public boolean isAgencyRegisteredAs(
            RegisteredOrganization registeredOrg,
            Organization placeholder,
            CommunityService communityService ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", communityService.getPlanCommunity().getUri() ) );
        criteria.add( Restrictions.eq( "placeholderOrgId", placeholder.getId() ) );
        criteria.add( Restrictions.eq( "registeredOrganization", registeredOrg ) );
        return !validate( (List<OrganizationParticipation>) criteria.list(), communityService ).isEmpty();
    }

    @Override
    @Transactional
    public boolean unassignOrganizationAs(
            ChannelsUser user,
            RegisteredOrganization registeredOrg,
            Organization placeholder,
            CommunityService communityService ) {
        if ( canUnassignOrganizationFrom( user, placeholder, communityService ) ) {
            OrganizationParticipation organizationParticipation = findOrganizationParticipation(
                    registeredOrg.getName( communityService ),
                    placeholder,
                    communityService );
            if ( organizationParticipation != null
                    && userParticipationService.listUserParticipationIn(
                    organizationParticipation,
                    communityService ).isEmpty() ) {
                delete( organizationParticipation );
                communityService.clearCache();
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<OrganizationParticipation> findAllParticipationBy( RegisteredOrganization registeredOrganization, CommunityService communityService ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", communityService.getPlanCommunity().getUri() ) );
        criteria.add( Restrictions.eq( "registeredOrganization", registeredOrganization ) );
        return validate( (List<OrganizationParticipation>) criteria.list(), communityService );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public OrganizationParticipation findOrganizationParticipation(
            String orgName,
            Organization placeholder,
            CommunityService communityService ) {
        RegisteredOrganization registeredOrg = registeredOrganizationService.find( orgName, communityService );
        if ( registeredOrg != null ) {
            Session session = getSession();
            Criteria criteria = session.createCriteria( getPersistentClass() );
            criteria.add( Restrictions.eq( "communityUri", communityService.getPlanCommunity().getUri() ) );
            criteria.add( Restrictions.eq( "placeholderOrgId", placeholder.getId() ) );
            criteria.add( Restrictions.eq( "registeredOrganization", registeredOrg ) );
            List<OrganizationParticipation> registrations = validate( (List<OrganizationParticipation>) criteria.list(), communityService );
            if ( !registrations.isEmpty() ) {
                return registrations.get( 0 );
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @SuppressWarnings( "unchecked" )
    private List<OrganizationParticipation> validate(
            List<OrganizationParticipation> organizationRegistrations,
            final CommunityService communityService ) {
        return (List<OrganizationParticipation>) CollectionUtils.select(
                organizationRegistrations,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return isValid( (OrganizationParticipation) object, communityService );
                    }
                }
        );
    }
}
