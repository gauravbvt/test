package com.mindalliance.channels.core.dao.user;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of user contact info service.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/14/12
 * Time: 10:11 AM
 */
@Repository
public class UserContactInfoServiceImpl
        extends GenericSqlServiceImpl<UserContactInfo, Long>
        implements UserContactInfoService {

    public UserContactInfoServiceImpl() {
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<Channel> findChannels( ChannelsUserInfo channelsUserInfo, PlanCommunity planCommunity ) {
        List<Channel> channels = new ArrayList<Channel>();
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "user", channelsUserInfo ) );
        for ( UserContactInfo contactInfo : (List<UserContactInfo>) criteria.list() ) {
            try {
                TransmissionMedium medium = TransmissionMedium.getUNKNOWN();
                if ( planCommunity != null ) {
                    // check if medium still valid
                    medium = planCommunity.find(
                            TransmissionMedium.class,
                            contactInfo.getTransmissionMediumId(),
                            contactInfo.getCreated() );
                }
                channels.add( new Channel( medium, contactInfo.getAddress() ) );
            } catch ( NotFoundException e ) {
                // ignore
            }
        }
        return channels;
    }

    @Override
    @Transactional
    @SuppressWarnings( "unchecked" )
    public void setAddress( ChannelsUserInfo user, Channel channel, String address ) {
        UserContactInfo contactInfo = findContactInfo( user, channel );
        if ( contactInfo != null ) {
            contactInfo.setAddress( address );
            save( contactInfo );
        }
    }

    @Override
    @Transactional
    public void addChannel( String username, ChannelsUserInfo user, Channel channel ) {
        UserContactInfo contactInfo = new UserContactInfo(
                username,
                user,
                channel );
        save( contactInfo );
    }

    @Override
    @Transactional
    public void removeChannel( ChannelsUserInfo user, Channel channel ) {
        UserContactInfo contactInfo = findContactInfo( user, channel );
        if ( contactInfo != null ) {
            delete( contactInfo );
        }
    }

    @Override
    @Transactional
    public void removeAllChannels( ChannelsUserInfo userInfo ) {
        for ( Channel channel : findChannels( userInfo, null ) ) {
            // null = don't do validation of channel media
            removeChannel( userInfo, channel );
        }
    }

    @SuppressWarnings( "unchecked" )
    private UserContactInfo findContactInfo( ChannelsUserInfo user, Channel channel ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "user", user ) );
        if ( !channel.getMedium().isUnknown() ) {
            // unknow medium = wild card
            criteria.add( Restrictions.eq( "transmissionMediumId", channel.getMedium().getId() ) );
            criteria.add( Restrictions.eq( "address", channel.getAddress() ) );
        }
        List<UserContactInfo> results = (List<UserContactInfo>) criteria.list();
        if ( !results.isEmpty() ) {
            return results.get( 0 );
        } else {
            return null;
        }
    }

}
