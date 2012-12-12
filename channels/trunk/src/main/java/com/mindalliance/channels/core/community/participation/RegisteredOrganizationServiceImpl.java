package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Registered organization service implementation.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/3/12
 * Time: 8:43 PM
 */
public class RegisteredOrganizationServiceImpl
        extends GenericSqlServiceImpl<RegisteredOrganization, Long>
        implements RegisteredOrganizationService {

    @Autowired
    OrganizationRegistrationService organizationRegistrationService;

    public RegisteredOrganizationServiceImpl() {
    }

    @Override
    @Transactional( readOnly = true )
    public RegisteredOrganization find( final String orgName, final PlanCommunity planCommunity ) {
        return (RegisteredOrganization) CollectionUtils.find(
                getAllRegisteredOrganizations( planCommunity ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (RegisteredOrganization) object ).getName( planCommunity ).equals( orgName );
                    }
                }
        );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<RegisteredOrganization> getAllRegisteredOrganizations( PlanCommunity planCommunity ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", planCommunity.getUri() ) );
        criteria.addOrder( Order.desc( "created" ) );
        return (List<RegisteredOrganization>) criteria.list();
    }

    @Override
    @Transactional
    public RegisteredOrganization findOrAdd( ChannelsUser user, String orgName, PlanCommunity planCommunity ) {
        RegisteredOrganization registered = find( orgName, planCommunity );
        if ( registered == null ) {
            Organization fixedOrg = planCommunity.getPlanService().findActualEntity( Organization.class, orgName );
            if ( fixedOrg != null ) {
                registered = new RegisteredOrganization(
                        user.getUsername(),
                        fixedOrg.getId(),
                        planCommunity
                );
            } else {
                registered = new RegisteredOrganization(
                        user.getUsername(),
                        orgName,
                        planCommunity
                );
            }
            save( registered );
        }
        return registered;
    }

    @Override
    @Transactional
    public void removeIfUnused( ChannelsUser user, String orgName, PlanCommunity planCommunity ) {
        RegisteredOrganization registered = find( orgName, planCommunity );
        if ( registered != null ) {
            if ( organizationRegistrationService.findRegistrationsFor( registered, planCommunity ).isEmpty() ) {
                delete( registered );
            }
        }
    }
}
