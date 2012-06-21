/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.CommanderFactory;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.dao.user.PlanParticipation;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.pages.AbstractChannelsBasicPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Abstract reports index page on participants and agents.
 */
public abstract class AbstractAllParticipantsPage extends AbstractChannelsBasicPage {

    protected static final String PLAN = "plan";

    protected static final String VERSION = "v";

    @SpringBean
    private PlanManager planManager;

    @SpringBean
    private AttachmentManager attachmentManager;

    @SpringBean
    private CommanderFactory commanderFactory;

    @SpringBean
    private ChannelsUserDao userDao;

    @SpringBean
    private PlanParticipationService planParticipationService;

    private String uri;

    private int version;

    private List<PlanParticipation> participations;

    private List<Actor> actors;

    public AbstractAllParticipantsPage( PageParameters parameters ) {
        super( parameters );
    }

    protected String getUri() {
        return uri;
    }

    protected int getVersion() {
        return version;
    }

    protected List<PlanParticipation> getParticipations() {
        return participations;
    }

    protected List<Actor> getActors() {
        return actors;
    }

    protected void addContent(  ) {
        Plan plan = getPlan();
        QueryService queryService = getQueryService();
        boolean isPlanner = getUser().isPlanner( plan.getUri() );
        uri = plan.getUri();
        version = plan.getVersion();
        participations = isPlanner
                            ? planParticipationService.getAllParticipations( plan, queryService )
                            : planParticipationService.getParticipations( plan, getUser().getUserInfo(), queryService );
        actors = findAssignedActors();
        initComponents( queryService, plan );
    }

    protected abstract void initComponents( QueryService service, Plan plan );

    protected List<Actor> findAssignedActors( ) {
        return getQueryService().getAssignments().getActualKnownActors();
    }

}
