package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.orm.service.GenericSqlService;

import java.util.List;

/**
 * Registered organization service.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/3/12
 * Time: 8:31 PM
 */
public interface RegisteredOrganizationService extends GenericSqlService<RegisteredOrganization, Long> {

    List<RegisteredOrganization> getAllRegisteredOrganizations( CommunityService communityService );

    RegisteredOrganization find( String orgName, CommunityService communityService );

    RegisteredOrganization findOrAdd( ChannelsUser user, String orgName, CommunityService communityService );

    boolean removeIfUnused( ChannelsUser user, String orgName, CommunityService communityService );

    List<String> getAllRegisteredNames( CommunityService communityService );

    boolean updateWith( ChannelsUser user,
                        String orgName,
                        Agency agency,
                        CommunityService communityService );

    List<Channel> getAllChannels( RegisteredOrganization registered,
                                  CommunityService communityService );

    List<RegisteredOrganization> findAncestors( String orgName, CommunityService communityService );

    boolean isValid( RegisteredOrganization registeredOrg, CommunityService communityService );
}
