package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.orm.service.GenericSqlService;

import java.util.List;

/**
 * Organization registration service.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/3/12
 * Time: 8:35 PM
 */
public interface OrganizationRegistrationService extends GenericSqlService<OrganizationRegistration, Long> {

    boolean isValid( OrganizationRegistration registration, PlanCommunity planCommunity );

    List<Agency> listRegisteredAgencies( PlanCommunity planCommunity );

    List<Agency> listAgenciesRegisteredAs( Organization placeholder, PlanCommunity planCommunity );

    OrganizationRegistration findOrganizationRegistration(
            String orgName,
            Organization placeholder,
            PlanCommunity planCommunity );

    boolean canRegisterOrganizationAs( ChannelsUser user, Organization placeholder, PlanCommunity planCommunity);

    boolean canUnregisterOrganizationAs(
            ChannelsUser user,
            String orgName,
            Organization placeholder,
            PlanCommunity planCommunity );

    RegisteredOrganization registerOrganizationAs(
            ChannelsUser user,
            String orgName,
            Organization placeholder,
            PlanCommunity planCommunity );

    void unregisterOrganizationAs(
            ChannelsUser user,
            String orgName,
            Organization placeholder,
            PlanCommunity planCommunity );

    List<OrganizationRegistration> findRegistrationsFor(
            RegisteredOrganization registeredOrganization,
            PlanCommunity planCommunity );
}
