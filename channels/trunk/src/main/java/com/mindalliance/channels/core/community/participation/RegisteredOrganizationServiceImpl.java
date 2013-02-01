package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Channel;
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

import java.util.ArrayList;
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
    OrganizationParticipationService organizationParticipationService;

    @Autowired
    OrganizationContactInfoService organizationContactInfoService;

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
        return validate( (List<RegisteredOrganization>) criteria.list(), planCommunity );
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
            planCommunity.clearCache();
        }
        return registered;
    }

    @Override
    @Transactional
    public boolean removeIfUnused( ChannelsUser user, String orgName, PlanCommunity planCommunity ) {
        RegisteredOrganization registered = find( orgName, planCommunity );
        if ( registered != null ) {
            boolean inParticipation = !organizationParticipationService
                    .findAllParticipationBy( registered, planCommunity ).isEmpty();
            boolean inOrgContacts = !organizationContactInfoService
                    .findAllContactInfo( registered, planCommunity ).isEmpty();
            if ( !inParticipation && !inOrgContacts ) {
                delete( registered );
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public List<String> getAllRegisteredNames( PlanCommunity planCommunity ) {
        List<String> allNames = new ArrayList<String>();
        for ( RegisteredOrganization registered : getAllRegisteredOrganizations( planCommunity ) ) {
            String name = registered.getName( planCommunity );
            if ( name != null ) allNames.add( name );
        }
        return allNames;
    }

    @Override
    @Transactional
    public boolean updateWith( ChannelsUser user,
                               String orgName,
                               Agency agency,
                               PlanCommunity planCommunity ) {
        boolean success = false;
        RegisteredOrganization registered = find( orgName, planCommunity );
        if ( registered != null ) {
            String parentName = agency.getParentName();
            if ( parentName != null ) {
                if ( planCommunity.canHaveParentAgency(
                        agency.getName(),
                        parentName ) ) {
                    RegisteredOrganization registeredParent = find( parentName, planCommunity );
                    if ( registeredParent == null ) {
                        registeredParent = new RegisteredOrganization(
                                user.getUsername(),
                                parentName,
                                planCommunity );
                        save( registeredParent );
                    }
                    registered.setParent( registeredParent );
                }
            } else {
                registered.setParent( null );
            }
            registered.updateWith( agency );
            organizationContactInfoService.setChannels( user, registered, agency.getEffectiveChannels(), planCommunity );
            save( registered );
            success = true;
        }
        return success;
    }

    @Override
    @Transactional( readOnly = true )
    public List<Channel> getAllChannels( RegisteredOrganization registered, PlanCommunity planCommunity ) {
        return organizationContactInfoService.getChannels( registered, planCommunity );
    }

    public List<RegisteredOrganization> findAncestors( String orgName, PlanCommunity planCommunity ) {
        List<RegisteredOrganization> visited = new ArrayList<RegisteredOrganization>();
        RegisteredOrganization registered = find( orgName, planCommunity );
        if ( registered != null )
            return safeFindAncestors( registered, planCommunity, visited );
        else
            return new ArrayList<RegisteredOrganization>();
    }

    private List<RegisteredOrganization> safeFindAncestors(
            RegisteredOrganization registered,
            PlanCommunity planCommunity,
            List<RegisteredOrganization> visited ) {
        List<RegisteredOrganization> ancestors = new ArrayList<RegisteredOrganization>();
        if ( !visited.contains( registered ) ) {
            if ( registered != null ) {
                RegisteredOrganization registeredParent = registered.getEffectiveParent( planCommunity );
                if ( registeredParent != null && !visited.contains( registeredParent ) ) {
                    visited.add( registeredParent );
                    ancestors.add( registeredParent );
                    ancestors.addAll( safeFindAncestors( registeredParent, planCommunity, visited ) );
                }
            }
        }
        return ancestors;
    }

    @Override
    @Transactional( readOnly = true )
    public boolean isValid( RegisteredOrganization registeredOrg, PlanCommunity planCommunity ) {
        return registeredOrg != null
                && !( registeredOrg.isFixedOrganization() && registeredOrg.getFixedOrganization( planCommunity ) == null )
                && ( registeredOrg.getParent() == null || isValid( registeredOrg.getParent(), planCommunity ) );
    }


    @SuppressWarnings( "unchecked" )
    private List<RegisteredOrganization> validate(
            List<RegisteredOrganization> registeredOrganizations,
            final PlanCommunity planCommunity ) {
        return (List<RegisteredOrganization>) CollectionUtils.select(
                registeredOrganizations,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return isValid( (RegisteredOrganization) object, planCommunity );
                    }
                }
        );
    }

}
