package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.CommunityService;
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

 //   @Autowired
    OrganizationParticipationService organizationParticipationService;

//    @Autowired
    OrganizationContactInfoService organizationContactInfoService;

    public RegisteredOrganizationServiceImpl() {
    }

    @Override
    @Transactional( readOnly = true )
    public RegisteredOrganization find( final String orgName, final CommunityService communityService ) {
        return (RegisteredOrganization) CollectionUtils.find(
                getAllRegisteredOrganizations( communityService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (RegisteredOrganization) object ).getName( communityService ).equals( orgName );
                    }
                }
        );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<RegisteredOrganization> getAllRegisteredOrganizations( CommunityService communityService ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", communityService.getPlanCommunity().getUri() ) );
        criteria.addOrder( Order.desc( "created" ) );
        return validate( (List<RegisteredOrganization>) criteria.list(), communityService );
    }

    @Override
    @Transactional
    public RegisteredOrganization findOrAdd( ChannelsUser user, String orgName, CommunityService communityService ) {
        RegisteredOrganization registered = find( orgName, communityService );
        if ( registered == null ) {
            Organization fixedOrg = communityService.getPlanService().findActualEntity( Organization.class, orgName );
            if ( fixedOrg != null ) {
                registered = new RegisteredOrganization(
                        user.getUsername(),
                        fixedOrg.getId(),
                        communityService.getPlanCommunity()
                );
            } else {
                registered = new RegisteredOrganization(
                        user.getUsername(),
                        orgName,
                        communityService.getPlanCommunity()
                );
            }
            save( registered );
            communityService.clearCache();
        }
        return registered;
    }

    @Override
    @Transactional
    public boolean removeIfUnused( ChannelsUser user, String orgName, CommunityService communityService ) {
        RegisteredOrganization registered = find( orgName, communityService );
        if ( registered != null ) {
            boolean inParticipation = !organizationParticipationService
                    .findAllParticipationBy( registered, communityService ).isEmpty();
            boolean inOrgContacts = !organizationContactInfoService
                    .findAllContactInfo( registered, communityService ).isEmpty();
            if ( !inParticipation && !inOrgContacts ) {
                delete( registered );
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public List<String> getAllRegisteredNames( CommunityService communityService ) {
        List<String> allNames = new ArrayList<String>();
        for ( RegisteredOrganization registered : getAllRegisteredOrganizations( communityService ) ) {
            String name = registered.getName( communityService );
            if ( name != null ) allNames.add( name );
        }
        return allNames;
    }

    @Override
    @Transactional
    public boolean updateWith( ChannelsUser user,
                               String orgName,
                               Agency agency,
                               CommunityService communityService ) {
        boolean success = false;
        RegisteredOrganization registered = find( orgName, communityService );
        if ( registered != null ) {
            String parentName = agency.getParentName();
            if ( parentName != null ) {
                if ( communityService.canHaveParentAgency(
                        agency.getName(),
                        parentName ) ) {
                    RegisteredOrganization registeredParent = find( parentName, communityService );
                    if ( registeredParent == null ) {
                        registeredParent = new RegisteredOrganization(
                                user.getUsername(),
                                parentName,
                                communityService.getPlanCommunity() );
                        save( registeredParent );
                    }
                    registered.setParent( registeredParent );
                }
            } else {
                registered.setParent( null );
            }
            registered.updateWith( agency );
            organizationContactInfoService.setChannels( user, registered, agency.getEffectiveChannels(), communityService );
            save( registered );
            success = true;
        }
        return success;
    }

    @Override
    @Transactional( readOnly = true )
    public List<Channel> getAllChannels( RegisteredOrganization registered, CommunityService communityService ) {
        return organizationContactInfoService.getChannels( registered, communityService );
    }

    public List<RegisteredOrganization> findAncestors( String orgName, CommunityService communityService ) {
        List<RegisteredOrganization> visited = new ArrayList<RegisteredOrganization>();
        RegisteredOrganization registered = find( orgName, communityService );
        if ( registered != null )
            return safeFindAncestors( registered, communityService, visited );
        else
            return new ArrayList<RegisteredOrganization>();
    }

    private List<RegisteredOrganization> safeFindAncestors(
            RegisteredOrganization registered,
            CommunityService communityService,
            List<RegisteredOrganization> visited ) {
        List<RegisteredOrganization> ancestors = new ArrayList<RegisteredOrganization>();
        if ( !visited.contains( registered ) ) {
            if ( registered != null ) {
                RegisteredOrganization registeredParent = registered.getEffectiveParent( communityService );
                if ( registeredParent != null && !visited.contains( registeredParent ) ) {
                    visited.add( registeredParent );
                    ancestors.add( registeredParent );
                    ancestors.addAll( safeFindAncestors( registeredParent, communityService, visited ) );
                }
            }
        }
        return ancestors;
    }

    @Override
    @Transactional( readOnly = true )
    public boolean isValid( RegisteredOrganization registeredOrg, CommunityService communityService ) {
        return registeredOrg != null
                && !( registeredOrg.isFixedOrganization() && registeredOrg.getFixedOrganization( communityService ) == null )
                && ( registeredOrg.getParent() == null || isValid( registeredOrg.getParent(), communityService ) );
    }


    @SuppressWarnings( "unchecked" )
    private List<RegisteredOrganization> validate(
            List<RegisteredOrganization> registeredOrganizations,
            final CommunityService communityService ) {
        return (List<RegisteredOrganization>) CollectionUtils.select(
                registeredOrganizations,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return isValid( (RegisteredOrganization) object, communityService );
                    }
                }
        );
    }

}
