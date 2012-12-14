package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
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

    List<RegisteredOrganization> getAllRegisteredOrganizations( PlanCommunity planCommunity );

    RegisteredOrganization find( String orgName, PlanCommunity planCommunity );

    RegisteredOrganization findOrAdd( ChannelsUser user, String orgName, PlanCommunity planCommunity );

    boolean removeIfUnused( ChannelsUser user, String orgName, PlanCommunity planCommunity );
}
