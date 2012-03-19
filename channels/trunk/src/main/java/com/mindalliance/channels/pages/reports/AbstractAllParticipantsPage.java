/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.CommanderFactory;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.dao.user.PlanParticipation;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.pages.AbstractChannelsWebPage;
import com.mindalliance.channels.pages.UserPage;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValueConversionException;

import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

/**
 * Abstract reports index page on participants and agents.
 */
public abstract class AbstractAllParticipantsPage extends AbstractChannelsWebPage {

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
        try {
            if ( parameters.getNamedKeys().contains( PLAN ) && parameters.getNamedKeys().contains( VERSION ) ) {
                Plan plan = planManager.getPlan(
                        parameters.get( PLAN ).toString(),
                        parameters.get( VERSION ).toInt() );

                if ( plan == null )
                    throw new AbortWithHttpErrorCodeException( SC_NOT_FOUND, "Not found" );

                Commander commander = getCommander( plan );
                init( commander.getQueryService(), plan );
            }
        } catch ( StringValueConversionException ignored ) {
            throw new AbortWithHttpErrorCodeException( SC_NOT_FOUND, "Not found" );
        }
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

    private void init( QueryService queryService, Plan plan ) {
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

    protected void addChannelsLogo() {
        WebMarkupContainer channels_logo = new WebMarkupContainer( "channelsHome" );
        channels_logo.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                setResponsePage( UserPage.class, AbstractChannelsWebPage.planParameters( getPlan() ) );
            }
        } );
        add( channels_logo );
    }

    protected List<Actor> findAssignedActors( ) {
        return getQueryService().getAssignments().getActualKnownActors();
    }

    /**
     * Commander needs some tending to prior to use.
     *
     * @param plan a plan
     * @return a commander
     */
    protected Commander getCommander( Plan plan ) {
        // Adjust so commander actually behave as expected
        getUser().setPlan( plan );

        return commanderFactory.getCommander( plan );
    }

}
