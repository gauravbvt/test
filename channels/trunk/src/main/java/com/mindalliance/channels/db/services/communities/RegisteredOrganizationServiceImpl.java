package com.mindalliance.channels.db.services.communities;

import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.db.data.ContactInfo;
import com.mindalliance.channels.db.data.communities.QRegisteredOrganization;
import com.mindalliance.channels.db.data.communities.RegisteredOrganization;
import com.mindalliance.channels.db.repositories.RegisteredOrganizationRepository;
import com.mindalliance.channels.db.services.AbstractDataService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Registered organization service implementation.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/19/13
 * Time: 1:45 PM
 */
@Component
public class RegisteredOrganizationServiceImpl
        extends AbstractDataService<RegisteredOrganization>
        implements RegisteredOrganizationService {

    @Autowired
    private RegisteredOrganizationRepository repository;

    @Autowired
    OrganizationParticipationService organizationParticipationService;

    public RegisteredOrganizationServiceImpl() {
    }

    @Override
    public void save( RegisteredOrganization registeredOrganization ) {
        repository.save( registeredOrganization );
    }

    private void delete( RegisteredOrganization registeredOrganization ) {
        repository.delete( registeredOrganization );
    }


    @Override
    public RegisteredOrganization load( String uid ) {
        return repository.findOne( uid );
    }

    @Override
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
    public List<RegisteredOrganization> getAllRegisteredOrganizations( CommunityService communityService ) {
        QRegisteredOrganization qRegisteredOrganization = QRegisteredOrganization.registeredOrganization;
        return validate(
                toList(
                        repository.findAll(
                                qRegisteredOrganization.classLabel.eq( RegisteredOrganization.class.getSimpleName() )
                                        .and( qRegisteredOrganization.communityUri.eq( communityService.getPlanCommunity().getUri() ) ),
                                qRegisteredOrganization.created.desc()
                        )
                ),
                communityService );
    }

    @Override
    public RegisteredOrganization findOrAdd( ChannelsUser user, String orgName, CommunityService communityService ) {
        synchronized ( communityService.getPlanCommunity() ) {
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
    }

    @Override
    public boolean removeIfUnused( ChannelsUser user, String orgName, CommunityService communityService ) {
        synchronized ( communityService.getPlanCommunity() ) {
            RegisteredOrganization registered = find( orgName, communityService );
            if ( registered != null ) {
                boolean inParticipation = !organizationParticipationService
                        .findAllParticipationBy( registered, communityService ).isEmpty();
                if ( !inParticipation ) {
                    delete( registered );
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public List<String> getAllRegisteredNames( CommunityService communityService ) {
        List<String> allNames = new ArrayList<String>();
        for ( RegisteredOrganization registered : getAllRegisteredOrganizations( communityService ) ) {
            String name = registered.getName( communityService );
            if ( name != null ) allNames.add( name );
        }
        return allNames;
    }

    @Override
    public boolean updateWith( ChannelsUser user, String orgName, Agency agency, CommunityService communityService ) {
        synchronized ( communityService.getPlanCommunity() ) {
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
                setChannels( user, registered, agency.getEffectiveChannels(), communityService );
                save( registered );
                success = true;
            }
            return success;
        }
    }

    @Override
    public List<Channel> getAllChannels( RegisteredOrganization registered, CommunityService communityService ) {
        List<Channel> channels = new ArrayList<Channel>();
        for ( ContactInfo contactInfo : registered.getContactInfoList() ) {
            Channel channel = contactInfo.asChannel( communityService );
            if ( channel != null ) {
                channels.add( channel );
            }
        }
        return channels;
    }

    @Override
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
    public boolean isValid( RegisteredOrganization registeredOrg, CommunityService communityService ) {
        return registeredOrg != null
                && !( registeredOrg.isFixedOrganization() && registeredOrg.getFixedOrganization( communityService ) == null )
                && ( registeredOrg.getParent( communityService ) == null || isValid( registeredOrg.getParent( communityService ), communityService ) );
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


    @Override
    public void setChannels(
            ChannelsUser user,
            RegisteredOrganization registered,
            List<Channel> channels,
            CommunityService communityService ) {
        List<ContactInfo> contactInfoList = new ArrayList<ContactInfo>();
        for ( Channel channel : channels ) {
            ContactInfo contactInfo = new ContactInfo( channel );
            if ( isValid( contactInfo, communityService ) ) {
                contactInfoList.add( contactInfo );
            }
        }
        registered.setContactInfoList( contactInfoList );
        save( registered );
        communityService.clearCache();
    }

    @Override
    public boolean isValid( ContactInfo orgContactInfo, CommunityService communityService ) {
        return orgContactInfo.isValid( communityService );
    }

}
