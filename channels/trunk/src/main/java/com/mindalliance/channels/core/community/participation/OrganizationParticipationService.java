package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.orm.service.GenericSqlService;

import java.util.List;

/**
 * Organization participation service.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/3/12
 * Time: 8:35 PM
 */
public interface OrganizationParticipationService extends GenericSqlService<OrganizationParticipation, Long> {

    List<OrganizationParticipation> getAllOrganizationParticipations( CommunityService communityService );

    boolean isValid( OrganizationParticipation registration, CommunityService communityService );

    List<Agency> listParticipatingAgencies( CommunityService communityService );

    List<Agency> listAgenciesParticipatingAs( Organization placeholder, CommunityService communityService );

    OrganizationParticipation findOrganizationParticipation(
            String orgName,
            Organization placeholder,
            CommunityService communityService );

    boolean canUnassignOrganizationFrom(
            ChannelsUser user,
            Organization placeholder,
            CommunityService communityService );

    boolean unassignOrganizationAs(
            ChannelsUser user,
            RegisteredOrganization registeredOrganization,
            Organization placeholder,
            CommunityService communityService );

    List<OrganizationParticipation> findAllParticipationBy(
            RegisteredOrganization registeredOrganization,
            CommunityService communityService );

    OrganizationParticipation assignOrganizationAs(
            ChannelsUser user,
            RegisteredOrganization registeredOrganization,
            Organization placeholder,
            CommunityService communityService );

    boolean isAgencyRegisteredAs(
            RegisteredOrganization registeredOrg,
            Organization placeholder,
            CommunityService communityService );
}
