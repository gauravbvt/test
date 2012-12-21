package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private RegisteredOrganizationService registeredOrganizationService;

    @Autowired
    private UserParticipationService userParticipationService;

    public OrganizationParticipationServiceImpl() {
    }

    @Override
    @Transactional( readOnly = true )
    public List<Agency> listParticipatingAgencies( PlanCommunity planCommunity ) {
        Set<Agency> agencies = new HashSet<Agency>();
        for ( OrganizationParticipation registration : list() ) {
            if ( isValid( registration, planCommunity ) ) {
                Agency agency = new Agency( registration, planCommunity );
                agencies.add( agency );
            }
        }
        return Collections.unmodifiableList( new ArrayList<Agency>( agencies ) );
    }

    @Override
    @Transactional( readOnly = true )
    public boolean isValid( OrganizationParticipation registration, PlanCommunity planCommunity ) {
        Organization placeholder = registration.getPlaceholderOrganization( planCommunity );
        return placeholder != null
                && placeholder.isPlaceHolder()
                && registration.getRegisteredOrganization().isValid( planCommunity );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public List<Agency> listAgenciesParticipatingAs( Organization placeholder, PlanCommunity planCommunity ) {
        if ( placeholder.isPlaceHolder() ) {
            Session session = getSession();
            Plan plan = planCommunity.getPlan();
            Criteria criteria = session.createCriteria( getPersistentClass() );
            criteria.add( Restrictions.eq( "communityUri", planCommunity.getUri() ) );
            criteria.add( Restrictions.eq( "placeholderOrgId", placeholder.getId() ) );
            List<Agency> agencies = new ArrayList<Agency>();
            for ( OrganizationParticipation registration : (List<OrganizationParticipation>) criteria.list() ) {
                agencies.add( new Agency( registration, planCommunity ) );
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
            PlanCommunity planCommunity ) {
        return placeholder.isPlaceHolder()
                && planCommunity.isCustodianOf( user, placeholder );
    }

    @Override
    @Transactional
    public OrganizationParticipation assignOrganizationAs(
            ChannelsUser user,
            RegisteredOrganization registeredOrganization,
            Organization placeholder,
            PlanCommunity planCommunity ) {
        if ( planCommunity.isCustodianOf( user, placeholder )) {
            if ( !isAgencyRegisteredAs( registeredOrganization, placeholder, planCommunity ) ) {
                OrganizationParticipation registration = new OrganizationParticipation(
                        user.getUsername(),
                        registeredOrganization,
                        placeholder,
                        planCommunity );
                save( registration );
                return registration;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @SuppressWarnings( "unchecked" )
    private boolean isAgencyRegisteredAs(
            RegisteredOrganization registeredOrg,
            Organization placeholder,
            PlanCommunity planCommunity ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", planCommunity.getUri() ) );
        criteria.add( Restrictions.eq( "placeholderOrgId", placeholder.getId() ) );
        criteria.add( Restrictions.eq( "registeredOrganization", registeredOrg ) );
        return !validate( (List<OrganizationParticipation>) criteria.list(), planCommunity ).isEmpty();
    }

    @Override
    @Transactional
    public boolean unassignOrganizationAs(
            ChannelsUser user,
            RegisteredOrganization registeredOrg,
            Organization placeholder,
            PlanCommunity planCommunity ) {
        if ( canUnassignOrganizationFrom( user, placeholder, planCommunity ) ) {
            OrganizationParticipation organizationParticipation = findOrganizationParticipation(
                    registeredOrg.getName( planCommunity ),
                    placeholder,
                    planCommunity );
            if ( organizationParticipation != null
                    && userParticipationService.listUserParticipationIn(
                    organizationParticipation,
                    planCommunity ).isEmpty() ) {
                delete( organizationParticipation );
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<OrganizationParticipation> findAllParticipationBy( RegisteredOrganization registeredOrganization, PlanCommunity planCommunity ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", planCommunity.getUri() ) );
        criteria.add( Restrictions.eq( "registeredOrganization", registeredOrganization ) );
        return validate( (List<OrganizationParticipation>) criteria.list(), planCommunity );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public OrganizationParticipation findOrganizationParticipation(
            String orgName,
            Organization placeholder,
            PlanCommunity planCommunity ) {
        RegisteredOrganization registeredOrg = registeredOrganizationService.find( orgName, planCommunity );
        if ( registeredOrg != null ) {
            Session session = getSession();
            Criteria criteria = session.createCriteria( getPersistentClass() );
            criteria.add( Restrictions.eq( "communityUri", planCommunity.getUri() ) );
            criteria.add( Restrictions.eq( "placeholderOrgId", placeholder.getId() ) );
            criteria.add( Restrictions.eq( "registeredOrganization", registeredOrg ) );
            List<OrganizationParticipation> registrations = validate( (List<OrganizationParticipation>) criteria.list(), planCommunity );
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
            final PlanCommunity planCommunity ) {
        return (List<OrganizationParticipation>)CollectionUtils.select(
                organizationRegistrations,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return isValid( (OrganizationParticipation) object , planCommunity );
                    }
                }
        );
    }
}
