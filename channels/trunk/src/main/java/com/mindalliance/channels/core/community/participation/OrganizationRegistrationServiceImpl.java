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
 * Organization registration service implementation.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/3/12
 * Time: 8:40 PM
 */
public class OrganizationRegistrationServiceImpl
        extends GenericSqlServiceImpl<OrganizationRegistration, Long>
        implements OrganizationRegistrationService {

    @Autowired
    private RegisteredOrganizationService registeredOrganizationService;

    public OrganizationRegistrationServiceImpl() {
    }

    @Override
    @Transactional( readOnly = true )
    public List<Agency> listRegisteredAgencies( PlanCommunity planCommunity ) {
        Set<Agency> agencies = new HashSet<Agency>();
        for ( OrganizationRegistration registration : list() ) {
            if ( isValid( registration, planCommunity ) ) {
                Agency agency = new Agency( registration, planCommunity );
                agencies.add( agency );
            }
        }
        return Collections.unmodifiableList( new ArrayList<Agency>( agencies ) );
    }

    @Override
    @Transactional( readOnly = true )
    public boolean isValid( OrganizationRegistration registration, PlanCommunity planCommunity ) {
        Organization placeholder = registration.getPlaceholderOrganization( planCommunity );
        return placeholder != null
                && placeholder.isPlaceHolder()
                && registration.getRegisteredOrganization().isValid( planCommunity );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public List<Agency> listAgenciesRegisteredAs( Organization placeholder, PlanCommunity planCommunity ) {
        if ( placeholder.isPlaceHolder() ) {
            Session session = getSession();
            Plan plan = planCommunity.getPlan();
            Criteria criteria = session.createCriteria( getPersistentClass() );
            criteria.add( Restrictions.eq( "communityUri", planCommunity.getUri() ) );
            criteria.add( Restrictions.eq( "placeholderOrgId", placeholder.getId() ) );
            List<Agency> agencies = new ArrayList<Agency>();
            for ( OrganizationRegistration registration : (List<OrganizationRegistration>) criteria.list() ) {
                agencies.add( new Agency( registration, planCommunity ) );
            }
            return agencies;
        } else {
            return new ArrayList<Agency>();
        }
    }

    @Override
    public boolean canRegisterOrganizationAs(
            ChannelsUser user,
            Organization placeholder,
            PlanCommunity planCommunity ) {
        return planCommunity.isCustodianOf( user, placeholder );
    }

    @Override
    @Transactional( readOnly = true )
    public boolean canUnregisterOrganizationAs(
            ChannelsUser user,
            String orgName,
            Organization placeholder,
            PlanCommunity planCommunity ) {
        return !placeholder.isPlaceHolder() || canRegisterOrganizationAs( user, placeholder, planCommunity );
    }

    @Override
    @Transactional
    public RegisteredOrganization registerOrganizationAs(
            ChannelsUser user,
            String orgName,
            Organization placeholder,
            PlanCommunity planCommunity ) {
        if ( canRegisterOrganizationAs( user, placeholder, planCommunity ) ) {
            RegisteredOrganization registeredOrg =
                    registeredOrganizationService.findOrAdd( user, orgName, planCommunity );
            if ( !isAgencyRegisteredAs( registeredOrg, placeholder, planCommunity ) ) {
                OrganizationRegistration registration = new OrganizationRegistration(
                        user.getUsername(),
                        registeredOrg,
                        placeholder,
                        planCommunity );
                save( registration );
            }
            return registeredOrg;
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
        return !validate( (List<OrganizationRegistration>) criteria.list(), planCommunity ).isEmpty();
    }

    @Override
    @Transactional
    public void unregisterOrganizationAs(
            ChannelsUser user,
            String orgName,
            Organization placeholder,
            PlanCommunity planCommunity ) {
        if ( canUnregisterOrganizationAs( user, orgName, placeholder, planCommunity ) ) {
            OrganizationRegistration registration = findOrganizationRegistration( orgName, placeholder, planCommunity );
            if ( registration != null ) {
                delete( registration );
            }
        }
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<OrganizationRegistration> findRegistrationsFor( RegisteredOrganization registeredOrganization, PlanCommunity planCommunity ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", planCommunity.getUri() ) );
        criteria.add( Restrictions.eq( "registeredOrganization", registeredOrganization ) );
        return validate( (List<OrganizationRegistration>) criteria.list(), planCommunity );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public OrganizationRegistration findOrganizationRegistration(
            String orgName,
            Organization placeholder,
            PlanCommunity planCommunity ) {
        RegisteredOrganization registeredOrg = registeredOrganizationService.find( orgName, planCommunity );
        if ( registeredOrg != null ) {
            Session session = getSession();
            Plan plan = planCommunity.getPlan();
            Criteria criteria = session.createCriteria( getPersistentClass() );
            criteria.add( Restrictions.eq( "communityUri", planCommunity.getUri() ) );
            criteria.add( Restrictions.eq( "placeholderOrgId", placeholder.getId() ) );
            criteria.add( Restrictions.eq( "registeredOrganization", registeredOrg ) );
            List<OrganizationRegistration> registrations = validate( (List<OrganizationRegistration>) criteria.list(), planCommunity );
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
    private List<OrganizationRegistration> validate(
            List<OrganizationRegistration> organizationRegistrations,
            final PlanCommunity planCommunity ) {
        return (List<OrganizationRegistration>)CollectionUtils.select(
                organizationRegistrations,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return isValid( ( OrganizationRegistration) object , planCommunity );
                    }
                }
        );
    }
}
