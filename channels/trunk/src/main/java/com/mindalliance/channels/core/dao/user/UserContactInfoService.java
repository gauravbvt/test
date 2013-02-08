package com.mindalliance.channels.core.dao.user;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.orm.service.GenericSqlService;

import java.util.List;

/**
 * User contact info service.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/14/12
 * Time: 10:11 AM
 */
public interface UserContactInfoService extends GenericSqlService<UserContactInfo, Long> {

    List<Channel> findChannels( ChannelsUserInfo channelsUserInfo, CommunityService communityService );

    /**
     * Change the address in an existing contact info.
     * @param userInfo user info
     * @param channel a channel
     * @param address new address
     */
    void setAddress( ChannelsUserInfo userInfo, Channel channel, String address );

    /**
     * Adding a contact info from user.
     * @param username user name of user doing the adding
     * @param userInfo user to be added channel to
     * @param channel a channel
     */
    void addChannel( String username, ChannelsUserInfo userInfo, Channel channel );

    /**
     * Remove a contact info from user.
     * @param userInfo user to be removed channel from
     * @param channel a channel
     */
    void removeChannel( ChannelsUserInfo userInfo, Channel channel );

    /**
     * Remove all user's contact info.
     * @param userInfo a user info
     */
    void removeAllChannels( ChannelsUserInfo userInfo );
}
