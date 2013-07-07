/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.CommanderFactory;
import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.ParticipationManager;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.db.data.communities.UserParticipation;
import com.mindalliance.channels.db.services.communities.UserParticipationService;
import com.mindalliance.channels.db.services.users.UserRecordService;
import com.mindalliance.channels.pages.AbstractChannelsBasicPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Abstract reports index page on participants and agents.
 */
public abstract class AbstractAllParticipantsPage extends AbstractChannelsBasicPage {


    @SpringBean
    private PlanManager planManager;

    @SpringBean
    private AttachmentManager attachmentManager;

    @SpringBean
    private CommanderFactory commanderFactory;

    @SpringBean
    private UserRecordService userInfoService;

    @SpringBean
    private UserParticipationService userParticipationService;

    @SpringBean
    private ParticipationManager participationManager;

    private List<UserParticipation> participations;

    private List<Agent> agents;

    public AbstractAllParticipantsPage( PageParameters parameters ) {
        super( parameters );
    }

    @Override
    protected String getContentsCssClass() {
        return "allParticipants-contents";
    }

    @Override
    protected String getDefaultUserRoleId() {
        return "participant";
    }


    protected int getPlanVersion() {
        return getPlanCommunity().getPlanVersion();
    }

    protected List<UserParticipation> getParticipations() {
        return participations;
    }

    protected List<Agent> getAgents() {
        return agents;
    }

    protected void addContent(  ) {
        CommunityService communityService = getCommunityService();
        QueryService queryService = getQueryService();
        boolean isPlanner = getUser().isPlannerOrAdmin( communityService.getPlan().getUri() );
        participations = isPlanner
                            ? userParticipationService.getAllActiveParticipations( communityService )
                            : userParticipationService.getActiveUserParticipations( getUser(), communityService );
        agents = findAssignedAgents();
        initComponents( queryService, communityService );
    }

    protected abstract void initComponents( QueryService service, CommunityService communityService );

    protected List<Agent> findAssignedAgents( ) {
        return participationManager.getAllKnownAgents( getCommunityService() );
    }

}
