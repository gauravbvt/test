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

    private List<UserParticipation> visibleParticipations;

    private List<Agent> knownAgents;

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

    protected List<UserParticipation> getVisibleParticipations() {
        return visibleParticipations;
    }

    protected List<Agent> getKnownAgents() {
        return knownAgents;
    }

    protected void addContent(  ) {
        CommunityService communityService = getCommunityService();
        QueryService queryService = getQueryService();
        boolean isPlanner = getUser().isPlannerOrAdmin( communityService.getPlan().getUri() );
        if ( isPlanner ) {
            visibleParticipations = participationManager.getAllActiveParticipations( communityService );
        } else {
            visibleParticipations = participationManager.getActiveUserParticipations( getUser(), communityService );
            visibleParticipations.addAll( participationManager.getActiveUserSupervisedParticipations( getUser(), communityService ) );
        }
        knownAgents = findAssignedAgents();
        initComponents( queryService, communityService );
    }

    protected abstract void initComponents( QueryService service, CommunityService communityService );

    protected List<Agent> findAssignedAgents( ) {
        return participationManager.getAllKnownAgents( getCommunityService() );
    }

}
