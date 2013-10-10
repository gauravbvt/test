package com.mindalliance.channels.db.services.communities;

import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.db.data.communities.OrganizationParticipation;
import com.mindalliance.channels.db.data.communities.RegisteredOrganization;
import com.mindalliance.channels.db.services.DataService;

import java.util.List;

/**
 * Organization participation service.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/19/13
 * Time: 2:44 PM
 */

public interface OrganizationParticipationService extends DataService<OrganizationParticipation> {

    List<OrganizationParticipation> getAllOrganizationParticipations( CommunityService communityService );

    Boolean isValid( OrganizationParticipation registration, CommunityService communityService );

    List<Agency> listAgenciesParticipatingAs( Organization placeholder, CommunityService communityService );

    OrganizationParticipation findOrganizationParticipation(
            String orgName,
            Organization placeholder,
            CommunityService communityService );

    Boolean canUnassignOrganizationFrom(
            ChannelsUser user,
            Organization placeholder,
            CommunityService communityService );

    Boolean unassignOrganizationAs(
            ChannelsUser user,
            RegisteredOrganization registeredOrganization,
            Organization placeholder,
            CommunityService communityService );

    List<OrganizationParticipation> findAllParticipationByGlobal(
            RegisteredOrganization registeredOrganization ); // anywhere

    List<OrganizationParticipation> findAllParticipationBy(
            RegisteredOrganization registeredOrganization,
            CommunityService communityService ); // in community

    List<OrganizationParticipation> findAllParticipationBy(
            Organization fixedOrganization,
            CommunityService communityService );

    OrganizationParticipation assignOrganizationAs(
            ChannelsUser user,
            RegisteredOrganization registeredOrganization,
            Organization placeholder,
            CommunityService communityService );

    Boolean isAgencyRegisteredAs(
            RegisteredOrganization registeredOrg,
            Organization placeholder,
            CommunityService communityService );

    Boolean isUsersParticipatingDirectlyInOrganizationParticipation( RegisteredOrganization registeredOrg,
                                                                     Organization placeholder,
                                                                     CommunityService communityService );

}
