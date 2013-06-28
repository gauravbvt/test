package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Organization contact info service implementation.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/3/12
 * Time: 8:42 PM
 */
public class OrganizationContactInfoServiceImpl
        extends GenericSqlServiceImpl<OrganizationContactInfo, Long>
        implements OrganizationContactInfoService {

//    @Autowired
    private RegisteredOrganizationService registeredOrganizationService;

    public OrganizationContactInfoServiceImpl() {
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<Channel> getChannels( RegisteredOrganization registered, CommunityService communityService ) {
        List<Channel> channels = new ArrayList<Channel>();
        for ( OrganizationContactInfo contactInfo : findAllContactInfo( registered, communityService ) ) {
            Channel channel = contactInfo.asChannel( communityService );
            if ( channel != null )
                channels.add( channel );

        }
        return channels;
    }


    @Override
    @Transactional
    @SuppressWarnings( "unchecked" )
    public void setChannels( ChannelsUser user,
                             RegisteredOrganization registered,
                             List<Channel> channels,
                             CommunityService communityService ) {
        removeAllContactInfoOf( registered, communityService );
        for ( Channel channel : channels ) {
            OrganizationContactInfo contactInfo = new OrganizationContactInfo(
                    user.getUsername(),
                    registered,
                    channel,
                    communityService.getPlanCommunity() );
            save( contactInfo );
        }
        communityService.clearCache();
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<OrganizationContactInfo> findAllContactInfo( RegisteredOrganization registered,
                                                             CommunityService communityService ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "registeredOrganization", registered ) );
        return validate( (List<OrganizationContactInfo>) criteria.list(), communityService );
    }

    @Override
    @Transactional
    public void removeAllContactInfoOf( RegisteredOrganization registered, CommunityService communityService ) {
        for ( OrganizationContactInfo contactInfo : findAllContactInfo( registered, communityService ) ) {
            delete( contactInfo );
        }
        communityService.clearCache();
    }

    @Override
    @Transactional( readOnly = true )
    public boolean isValid( OrganizationContactInfo orgContactInfo, CommunityService communityService ) {
        return orgContactInfo != null
                && registeredOrganizationService.isValid( orgContactInfo.getRegisteredOrganization(), communityService )
                && communityService.exists( TransmissionMedium.class,
                orgContactInfo.getTransmissionMediumId(),
                orgContactInfo.getCreated() );
    }


    @SuppressWarnings( "unchecked" )
    private List<OrganizationContactInfo> validate(
            List<OrganizationContactInfo> orgContactInfos,
            final CommunityService communityService ) {
        return (List<OrganizationContactInfo>) CollectionUtils.select(
                orgContactInfos,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return isValid( (OrganizationContactInfo) object, communityService );
                    }
                }
        );
    }


}
