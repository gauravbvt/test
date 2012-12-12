package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.community.participation.Agent;
import com.mindalliance.channels.core.community.participation.ParticipationManager;
import com.mindalliance.channels.core.community.participation.UserParticipationConfirmationService;
import com.mindalliance.channels.core.community.participation.UserParticipationService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Nameable;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.engine.analysis.Analyst;

/**
 * A plan community.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/29/12
 * Time: 3:23 PM
 */
public class PlanCommunity implements Nameable, Identifiable {

    private static final String UNNAMED = "UNNAMED";

    private CommunityServiceImpl communityService;
    private String name;
    private String description;
    private Place communityLocale;
    private ParticipationManager participationManager;

    public PlanCommunity(  // todo - should only be invoked once per community instantiated.
            PlanService planService,
            Analyst analyst,
            UserParticipationService userParticipationService,
            UserParticipationConfirmationService userParticipationConfirmationService,
            ParticipationManager participationManager ) {
        this.participationManager = participationManager;
        communityService = new CommunityServiceImpl( this, planService, analyst, userParticipationService, userParticipationConfirmationService );
    }

    public Place getCommunityLocale() {
        return communityLocale;
    }

    public void setCommunityLocale( Place communityLocale ) {
        this.communityLocale = communityLocale;
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public String getName() {
        return name == null ? UNNAMED : name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public CommunityServiceImpl getCommunityService() {
        return communityService;
    }

    public UserParticipationService getUserParticipationService() {
        return communityService.getUserParticipationService();
    }

    public PlanService getPlanService() {
        return communityService.getPlanService();
    }

    public ChannelsUserDao getUserDao() {
        return getPlanService().getUserDao();
    }

    public Analyst getAnalyst() {
        return communityService.getAnalyst();
    }

    public Plan getPlan() {
        return getPlanService().getPlan();
    }

    public UserParticipationConfirmationService getUserParticipationConfirmationService() {
        return communityService.getUserParticipationConfirmationService();
    }

    public boolean isCustodianOf( ChannelsUser user, Organization placeholder ) {
        if ( !placeholder.isPlaceHolder() ) return false;
        if ( user.isPlanner( getPlan().getUri() ) ) return true;
        Actor custodian = placeholder.getCustodian();
        return custodian != null
                && getUserParticipationService().isUserParticipatingAs( user, new Agent( custodian ), this );
    }

    public String getUri() {
        return getPlan().getUri(); // todo - change when not only one implied community per plan
    }

    public int getPlanVersion() {
        return getPlan().getVersion();
    }

    public ParticipationManager getParticipationManager() {
        return participationManager;
    }

    public String getPlanUri() {
        return getPlan().getUri();
    }

    ////////


    @Override
    public String getClassLabel() {
        return getClass().getSimpleName();
    }

    @Override
    public long getId() {
        return getPlan().getId();
    }

    @Override
    public String getTypeName() {
        return "Plan community";
    }

    @Override
    public boolean isModifiableInProduction() {
        return false;
    }
}
