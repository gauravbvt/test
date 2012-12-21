package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
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

    public OrganizationContactInfoServiceImpl() {
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<Channel> getChannels( RegisteredOrganization registered, PlanCommunity planCommunity ) {
        List<Channel> channels = new ArrayList<Channel>();
        for ( OrganizationContactInfo contactInfo : findAllContactInfo( registered ) ) {
            Channel channel = contactInfo.asChannel( planCommunity );
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
                             PlanCommunity planCommunity ) {
        removeAllContactInfoOf( registered );
        for ( Channel channel : channels ) {
            OrganizationContactInfo contactInfo = new OrganizationContactInfo(
                    user.getUsername(),
                    registered,
                    channel,
                    planCommunity);
            save( contactInfo );
        }
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<OrganizationContactInfo> findAllContactInfo( RegisteredOrganization registered ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "registeredOrganization", registered ) );
        return (List<OrganizationContactInfo>) criteria.list();
    }

    @Override
    @Transactional
    public void removeAllContactInfoOf( RegisteredOrganization registered ) {
        for ( OrganizationContactInfo contactInfo : findAllContactInfo( registered ) ) {
            delete( contactInfo );
        }
    }

}
