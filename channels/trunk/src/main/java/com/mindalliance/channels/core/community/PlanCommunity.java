package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.community.participation.PlanParticipationService;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.model.Nameable;
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
public class PlanCommunity implements Nameable {

    private static final String UNNAMED = "UNNAMED";

    private CommunityServiceImpl communityService;
    private String name;
    private String description;
    private Place communityLocale;

    public PlanCommunity( PlanService planService, Analyst analyst, PlanParticipationService planParticipationService ) {
        communityService = new CommunityServiceImpl( this, planService, analyst,  planParticipationService );
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

    public PlanParticipationService getPlanParticipationService() {
        return communityService.getPlanParticipationService();
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

 }
