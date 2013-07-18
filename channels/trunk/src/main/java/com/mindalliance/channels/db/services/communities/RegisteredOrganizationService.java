package com.mindalliance.channels.db.services.communities;

import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.db.data.ContactInfo;
import com.mindalliance.channels.db.data.communities.RegisteredOrganization;
import com.mindalliance.channels.db.services.DataService;

import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/19/13
 * Time: 1:41 PM
 */
public interface RegisteredOrganizationService extends DataService<RegisteredOrganization> {

    List<RegisteredOrganization> getAllRegisteredOrganizations( CommunityService communityService );

    RegisteredOrganization find( String orgName, CommunityService communityService );

    RegisteredOrganization findOrAdd( ChannelsUser user, String orgName, CommunityService communityService );

    Boolean removeIfUnused( ChannelsUser user, String orgName, CommunityService communityService );

    List<String> getAllRegisteredNames( CommunityService communityService );

    Boolean updateWith( ChannelsUser user,
                        String orgName,
                        Agency agency,
                        CommunityService communityService );

    List<Channel> getAllChannels( RegisteredOrganization registered,
                                  CommunityService communityService );

    List<RegisteredOrganization> findAncestors( String orgName, CommunityService communityService );

    Boolean isValid( RegisteredOrganization registeredOrg, CommunityService communityService );

    ///// Contact info

    void setChannels( ChannelsUser user,
                      RegisteredOrganization registered,
                      List<Channel> channels,
                      CommunityService communityService );

    Boolean isValid( ContactInfo orgContactInfo, CommunityService communityService );

}
