/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.CommanderFactory;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.participation.Agent;
import com.mindalliance.channels.core.community.participation.ParticipationManager;
import com.mindalliance.channels.core.community.participation.UserParticipation;
import com.mindalliance.channels.core.community.participation.UserParticipationService;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.pages.AbstractChannelsBasicPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Abstract reports index page on participants and agents.
 */
public abstract class AbstractAllParticipantsPage extends AbstractChannelsBasicPage {

    public static final String AGENT = "agent";

    public static final String ORG = "org";

    public static final String USER = "user";

    @SpringBean
    private PlanManager planManager;

    @SpringBean
    private AttachmentManager attachmentManager;

    @SpringBean
    private CommanderFactory commanderFactory;

    @SpringBean
    private ChannelsUserDao userDao;

    @SpringBean
    private UserParticipationService userParticipationService;

    @SpringBean
    private ParticipationManager participationManager;


    private String planCommunityUri;

    private int planVersion;

    private List<UserParticipation> participations;

    private List<Agent> agents;

    public AbstractAllParticipantsPage( PageParameters parameters ) {
        super( parameters );
    }

    @Override
    protected String getContentsCssClass() {
        return "allParticipants-contents";
    }


    public String getPlanCommunityUri() {
        return planCommunityUri;
    }

    protected int getPlanVersion() {
        return planVersion;
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
        boolean isPlanner = getUser().isPlanner( communityService.getPlan().getUri() );
        planCommunityUri = communityService.getPlanCommunity().getUri();
        planVersion = communityService.getPlan().getVersion();
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
