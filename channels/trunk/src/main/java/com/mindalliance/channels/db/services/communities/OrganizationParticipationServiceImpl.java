package com.mindalliance.channels.db.services.communities;

import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.db.data.communities.OrganizationParticipation;
import com.mindalliance.channels.db.data.communities.QOrganizationParticipation;
import com.mindalliance.channels.db.data.communities.RegisteredOrganization;
import com.mindalliance.channels.db.repositories.OrganizationParticipationRepository;
import com.mindalliance.channels.db.services.AbstractDataService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/19/13
 * Time: 2:48 PM
 */
@Component
public class OrganizationParticipationServiceImpl
        extends AbstractDataService<OrganizationParticipation>
        implements OrganizationParticipationService {

    @Autowired
    private OrganizationParticipationRepository repository;

    @Autowired
    private UserParticipationService userParticipationService;

    @Autowired
    private RegisteredOrganizationService registeredOrganizationService;


    public OrganizationParticipationServiceImpl() {
    }

    @Override
    public void save( OrganizationParticipation organizationParticipation ) {
        repository.save( organizationParticipation );
    }

    @Override
    public OrganizationParticipation load( String uid ) {
        return repository.findOne( uid );
    }

    private void delete( OrganizationParticipation organizationParticipation ) {
        repository.delete( organizationParticipation );
    }

    @Override
    public OrganizationParticipation assignOrganizationAs( ChannelsUser user, RegisteredOrganization registeredOrganization, Organization placeholder, CommunityService communityService ) {
        if ( !isAgencyRegisteredAs( registeredOrganization, placeholder, communityService )
                && communityService.isCustodianOf( user, placeholder )
                && ( !placeholder.isSingleParticipation()
                || findAllParticipationBy( registeredOrganization, communityService ).isEmpty() ) ) {
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
    }

    @Override
    public List<OrganizationParticipation> getAllOrganizationParticipations( CommunityService communityService ) {
        QOrganizationParticipation qOrganizationParticipation = QOrganizationParticipation.organizationParticipation;
        return validate( toList(
                repository.findAll(
                        qOrganizationParticipation.classLabel.eq( OrganizationParticipation.class.getSimpleName() )
                                .and( qOrganizationParticipation.communityUri.eq( communityService.getPlanCommunity().getUri() ) )
                )
        ), communityService );
    }

    @Override
    public Boolean isValid( OrganizationParticipation orgParticipation, CommunityService communityService ) {
        if ( orgParticipation == null ) return false;
        Organization placeholder = orgParticipation.getPlaceholderOrganization( communityService );
        return placeholder != null
                && placeholder.isPlaceHolder()
                && orgParticipation.getRegisteredOrganization( communityService ).isValid( communityService );
    }

    @Override
    public List<Agency> listParticipatingAgencies( CommunityService communityService ) {
        Set<Agency> agencies = new HashSet<Agency>();
        for ( OrganizationParticipation orgParticipation : getAllOrganizationParticipations( communityService ) ) {
            if ( isValid( orgParticipation, communityService ) ) {
                Agency agency = new Agency( orgParticipation, communityService );
                agencies.add( agency );
            }
        }
        return Collections.unmodifiableList( new ArrayList<Agency>( agencies ) );
    }

    @Override
    public List<Agency> listAgenciesParticipatingAs( Organization placeholder, CommunityService communityService ) {
        if ( placeholder.isPlaceHolder() ) {
            QOrganizationParticipation qOrganizationParticipation = QOrganizationParticipation.organizationParticipation;
            List<OrganizationParticipation> organizationParticipations =
                    validate( toList(
                            repository.findAll(
                                    qOrganizationParticipation.classLabel.eq( OrganizationParticipation.class.getSimpleName() )
                                            .and( qOrganizationParticipation.communityUri.eq( communityService.getPlanCommunity().getUri() ) )
                                            .and( qOrganizationParticipation.placeholderOrgId.eq( placeholder.getId() ) )
                            )
                    ), communityService );
            List<Agency> agencies = new ArrayList<Agency>();
            for ( OrganizationParticipation orgParticipation : organizationParticipations ) {
                agencies.add( new Agency( orgParticipation, communityService ) );
            }
            return agencies;
        } else {
            return new ArrayList<Agency>();
        }


    }

    @Override
    public OrganizationParticipation findOrganizationParticipation( String orgName,
                                                                    Organization placeholder,
                                                                    CommunityService communityService ) {
        RegisteredOrganization registeredOrg = registeredOrganizationService.find( orgName, communityService );
        if ( registeredOrg != null ) {
            List<OrganizationParticipation> organizationParticipations
                    = getOrganizationParticipations( registeredOrg, placeholder, communityService );
            if ( !organizationParticipations.isEmpty() ) {
                return organizationParticipations.get( 0 );
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private List<OrganizationParticipation> getOrganizationParticipations( RegisteredOrganization registeredOrg,
                                                                           Organization placeholder,
                                                                           CommunityService communityService ) {
        QOrganizationParticipation qOrganizationParticipation = QOrganizationParticipation.organizationParticipation;
        return validate( toList(
                repository.findAll(
                        qOrganizationParticipation.classLabel.eq( OrganizationParticipation.class.getSimpleName() )
                                .and( qOrganizationParticipation.communityUri.eq( communityService.getPlanCommunity().getUri() ) )
                                .and( qOrganizationParticipation.placeholderOrgId.eq( placeholder.getId() ) )
                                .and( qOrganizationParticipation.registeredOrganizationUid.eq( registeredOrg.getUid() ) )
                )
        ), communityService );

    }

    @Override
    public Boolean canUnassignOrganizationFrom( ChannelsUser user, Organization placeholder, CommunityService communityService ) {
        return placeholder.isPlaceHolder()
                && communityService.isCustodianOf( user, placeholder );
    }

    @Override
    public Boolean unassignOrganizationAs( ChannelsUser user,
                                           RegisteredOrganization registeredOrg,
                                           Organization placeholder,
                                           CommunityService communityService ) {
        synchronized ( communityService.getPlanCommunity() ) {
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
    }

    public Boolean isUsersParticipatingInOrganizationParticipation(RegisteredOrganization registeredOrg,
                                                                   Organization placeholder,
                                                                   CommunityService communityService ) {
        if ( registeredOrg == null ) return false;
        OrganizationParticipation organizationParticipation = findOrganizationParticipation(
                registeredOrg.getName( communityService ),
                placeholder,
                communityService );
        return organizationParticipation != null
                && !userParticipationService.listUserParticipationIn(
                organizationParticipation,
                communityService ).isEmpty();
    }

    @Override
    public List<OrganizationParticipation> findAllParticipationByGlobal( RegisteredOrganization registeredOrganization ) {
        assert !registeredOrganization.isLocal();
        QOrganizationParticipation qOrganizationParticipation = QOrganizationParticipation.organizationParticipation;
        return toList(
                repository.findAll(
                        qOrganizationParticipation.classLabel.eq( OrganizationParticipation.class.getSimpleName() )
                                .and( qOrganizationParticipation.registeredOrganizationUid.eq( registeredOrganization.getUid() ) )
                )
        );
    }

    @Override
    public List<OrganizationParticipation> findAllParticipationBy( RegisteredOrganization registeredOrganization,
                                                                   CommunityService communityService ) {
        QOrganizationParticipation qOrganizationParticipation = QOrganizationParticipation.organizationParticipation;
        return validate( toList(
                repository.findAll(
                        qOrganizationParticipation.classLabel.eq( OrganizationParticipation.class.getSimpleName() )
                                .and( qOrganizationParticipation.communityUri.eq( communityService.getPlanCommunity().getUri() ) )
                                .and( qOrganizationParticipation.registeredOrganizationUid.eq( registeredOrganization.getUid() ) )
                )
        ), communityService );

    }

    @Override
    public List<OrganizationParticipation> findAllParticipationBy( Organization fixedOrganization, CommunityService communityService ) {
        RegisteredOrganization registeredOrganization = registeredOrganizationService.find( fixedOrganization.getName(), communityService );
        return registeredOrganization == null
                ? new ArrayList<OrganizationParticipation>()
                : findAllParticipationBy( registeredOrganization, communityService );
    }

    @Override
    public Boolean isAgencyRegisteredAs( RegisteredOrganization registeredOrg, Organization placeholder, CommunityService communityService ) {
        return !getOrganizationParticipations( registeredOrg, placeholder, communityService ).isEmpty();
    }

    @SuppressWarnings( "unchecked" )
    private List<OrganizationParticipation> validate(
            List<OrganizationParticipation> organizationParticipationList,
            final CommunityService communityService ) {
        return (List<OrganizationParticipation>) CollectionUtils.select(
                organizationParticipationList,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return isValid( (OrganizationParticipation) object, communityService );
                    }
                }
        );
    }

}
