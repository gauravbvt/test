package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.PlanCommunity;
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

    List<OrganizationParticipation> getAllOrganizationParticipations( PlanCommunity planCommunity );

    boolean isValid( OrganizationParticipation registration, PlanCommunity planCommunity );

    List<Agency> listParticipatingAgencies( PlanCommunity planCommunity );

    List<Agency> listAgenciesParticipatingAs( Organization placeholder, PlanCommunity planCommunity );

    OrganizationParticipation findOrganizationParticipation(
            String orgName,
            Organization placeholder,
            PlanCommunity planCommunity );

    boolean canUnassignOrganizationFrom(
            ChannelsUser user,
            Organization placeholder,
            PlanCommunity planCommunity );

    boolean unassignOrganizationAs(
            ChannelsUser user,
            RegisteredOrganization registeredOrganization,
            Organization placeholder,
            PlanCommunity planCommunity );

    List<OrganizationParticipation> findAllParticipationBy(
            RegisteredOrganization registeredOrganization,
            PlanCommunity planCommunity );

    OrganizationParticipation assignOrganizationAs(
            ChannelsUser user,
            RegisteredOrganization registeredOrganization,
            Organization placeholder,
            PlanCommunity planCommunity );
}
