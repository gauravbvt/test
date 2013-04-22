package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.orm.service.GenericSqlService;

import java.util.List;

/**
 * Organization contact info service.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/3/12
 * Time: 8:33 PM
 */
public interface OrganizationContactInfoService extends GenericSqlService<OrganizationContactInfo, Long> {

    List<Channel> getChannels( RegisteredOrganization registered,
                               CommunityService communityService );

    void setChannels( ChannelsUser user,
                      RegisteredOrganization registered,
                      List<Channel> channels,
                      CommunityService communityService );

    void removeAllContactInfoOf( RegisteredOrganization registered, CommunityService communityService );

    List<OrganizationContactInfo> findAllContactInfo( RegisteredOrganization registered,
                                                      CommunityService communityService );

    boolean isValid( OrganizationContactInfo orgContactInfo, CommunityService communityService );
}
