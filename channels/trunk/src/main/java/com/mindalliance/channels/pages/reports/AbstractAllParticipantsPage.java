/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.CommanderFactory;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.command.commands.CreateEntityIfNew;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.dao.UserDao;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Participation;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.pages.AbstractChannelsWebPage;
import com.mindalliance.channels.pages.UserPage;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValueConversionException;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

/**
 * Abstract reports index page on participants and agents.
 */
public abstract class AbstractAllParticipantsPage extends WebPage {

    protected static final String PLAN = "plan";

    protected static final String VERSION = "v";

    @SpringBean
    protected User user;

    @SpringBean
    private PlanManager planManager;

    @SpringBean
    private AttachmentManager attachmentManager;

    @SpringBean
    private CommanderFactory commanderFactory;

    @SpringBean
    private UserDao userDao;

    private String uri;

    private int version;

    private List<Participation> users;

    private List<Actor> actors;

    private List<User> unassigned;

 /*   public AbstractAllParticipantsPage( Class<? extends AbstractParticipantPage> clazz ) {

        List<Plan> plans = planManager.getPlannablePlans( user );
        if ( plans.isEmpty() )
            throw new AbortWithHttpErrorCodeException( SC_FORBIDDEN, "Unauthorized access" );

        Plan plan = plans.get( 0 );
        PageParameters pageParameters = new PageParameters();
        pageParameters.set( PLAN, plan.getUri() );
        pageParameters.set( VERSION, plan.getVersion() );
        setResponsePage( clazz, pageParameters );
    }
*/
    public AbstractAllParticipantsPage( PageParameters parameters ) {
        super( parameters );
        try {
            if ( parameters.getNamedKeys().contains( PLAN ) && parameters.getNamedKeys().contains( VERSION ) ) {
                Plan plan = planManager.getPlan(
                        parameters.get( PLAN ).toString(),
                        parameters.get( VERSION ).toInt() );

                if ( plan == null )
                    throw new AbortWithHttpErrorCodeException( SC_NOT_FOUND, "Not found" );

                if ( !user.isPlanner( plan.getUri() ) )
                    throw new AbortWithHttpErrorCodeException( SC_FORBIDDEN, "Unauthorized access" );

                Commander commander = getCommander( plan );
                init( commander.getQueryService(), plan );
            }
        } catch ( StringValueConversionException ignored ) {
            throw new AbortWithHttpErrorCodeException( SC_NOT_FOUND, "Not found" );
        }
    }

    protected User getUser() {
        return user;
    }

    protected String getUri() {
        return uri;
    }

    protected int getVersion() {
        return version;
    }

    protected List<Participation> getUsers() {
        return users;
    }

    protected List<Actor> getActors() {
        return actors;
    }

    protected List<User> getUnassigned() {
        return unassigned;
    }

    protected PlanManager getPlanManager() {
        return planManager;
    }

    protected UserDao getUserDao() {
        return userDao;
    }

    private void init( QueryService service, Plan plan ) {
        uri = plan.getUri();
        version = plan.getVersion();

        users = validate( userDao, service.list( Participation.class ) );

        actors = findFreeActors( findAssignedActors( users ), service.getAssignments().getActualActors() );

        unassigned = findUnassignedUsers( service );
        if ( !user.isPlanner( uri ) ) {
            throw new AbortWithHttpErrorCodeException( HttpServletResponse.SC_FORBIDDEN, "Unauthorized access" );
        }
        initComponents( service, plan );
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

    protected Plan getPlan() {
        return user.getPlan();
    }

    protected List<User> findUnassignedUsers( QueryService service ) {
        String uri = service.getPlan().getUri();
        Collection<User> inputCollection = service.getUserDao().getUsers( uri );
        List<User> answer = new ArrayList<User>( inputCollection.size() );
        for ( User u : inputCollection ) {
            Participation participation = service.findParticipation( u.getUsername() );
            if ( participation == null || participation.getActor() == null )
                answer.add( u );
        }

        Collections.sort( answer, new Comparator<User>() {
            @Override
            public int compare( User o1, User o2 ) {
                return o1.getFullName().compareTo( o2.getFullName() );
            }
        } );
        return answer;
    }

    protected List<Actor> findFreeActors( Set<Actor> assigned, Collection<Actor> actors ) {
        List<Actor> answer = new ArrayList<Actor>( actors.size() );
        for ( Actor a : actors )
            if ( !a.isUnknown() && !assigned.contains( a ) )
                answer.add( a );

        Collections.sort( answer, new Comparator<Actor>() {
            @Override
            public int compare( Actor o1, Actor o2 ) {
                return o1.getNormalizedName().compareToIgnoreCase( o2.getNormalizedName() );
            }
        } );
        return answer;
    }

    protected Set<Actor> findAssignedActors( List<Participation> users ) {
        Set<Actor> result = new HashSet<Actor>( users.size() );
        for ( Participation p : users ) {
            Actor actor = p.getActor();
            if ( actor.isSingular() )
                result.add( actor );
        }

        return result;
    }

    /**
     * Filter invalid participations.
     *
     * @param userDao to check if users are still valid
     * @param participations the participations to check
     * @return filtered list
     */
    protected static List<Participation> validate(
            UserDao userDao, Collection<Participation> participations ) {

        List<Participation> answer = new ArrayList<Participation>( participations.size() );
        for ( Participation item : participations ) {
            String userName = item.getUsername();
            if ( item.getActor() != null && userName != null && userDao.getUserNamed( userName ) != null )
                answer.add( item );
        }

        return answer;
    }

    /**
     * Commander needs some tending to prior to use.
     *
     * @param plan a plan
     * @return a commander
     */
    protected Commander getCommander( Plan plan ) {
        // Adjust so commander actually behave as expected
        user.setPlan( plan );

        return commanderFactory.getCommander( plan );
    }

    protected static Participation findParticipation( Commander cmdr, String username ) {
        QueryService planService = cmdr.getQueryService();
        Participation participation = planService.findParticipation( username );
        if ( participation == null ) {
            Participation newPart = (Participation) cmdr
                    .doUnsafeCommand( new CreateEntityIfNew( User.current().getUsername(),
                                                             Participation.class, username, ModelEntity.Kind.Actual ) )
                    .getSubject( planService );

            newPart.setActual();
            return newPart;
        } else
            return participation;
    }
}
