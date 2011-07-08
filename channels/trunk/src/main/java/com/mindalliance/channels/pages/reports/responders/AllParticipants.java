// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.pages.reports.responders;

import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.LockManager;
import com.mindalliance.channels.command.LockingException;
import com.mindalliance.channels.command.commands.CreateEntityIfNew;
import com.mindalliance.channels.command.commands.UpdateObject.Action;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.dao.UserService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Participation;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.pages.AbstractChannelsWebPage;
import com.mindalliance.channels.query.PlanService;
import com.mindalliance.channels.query.QueryService;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValueConversionException;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

/** A planner's table of contents to responders pages. */

public class AllParticipants extends WebPage {

    private static final String PLAN = "plan";

    private static final String VERSION = "v";

    @SpringBean
    private User user;

    @SpringBean
    private PlanManager planManager;

    @SpringBean
    private Commander commander;

    @SpringBean
    private LockManager lockManager;

    public AllParticipants() {

        List<Plan> plans = planManager.getPlannablePlans( user );
        if ( plans.isEmpty() )
            throw new AbortWithWebErrorCodeException( SC_FORBIDDEN );

        Plan plan = plans.get( 0 );
        PageParameters pageParameters = new PageParameters();
        pageParameters.put( PLAN, plan.getUri() );
        pageParameters.put( VERSION, plan.getVersion() );

        setRedirect( true );
        setResponsePage( ParticipantPage.class, pageParameters );
    }

    public AllParticipants( PageParameters parameters ) {
        super( parameters );

        try {
            if ( parameters.containsKey( PLAN ) && parameters.containsKey( VERSION ) ) {
                Plan plan = planManager.getPlan( parameters.getString( PLAN ),
                                                 parameters.getInt( VERSION ) );

                if ( plan == null )
                    throw new AbortWithWebErrorCodeException( SC_NOT_FOUND );

                if ( !user.isPlanner( plan.getUri() ) )
                    throw new AbortWithWebErrorCodeException( SC_FORBIDDEN );

                init( createService( plan ), plan );
            }

        } catch ( StringValueConversionException ignored ) {
            throw new AbortWithWebErrorCodeException( SC_NOT_FOUND );
        }
    }

    private PlanService createService( Plan plan ) {
        return new PlanService( planManager, null, planManager.getUserService(), plan );
    }

    private void init( PlanService service, final Plan plan ) {
        final String uri = plan.getUri();
        final int version = plan.getVersion();

        final List<Participation> users = validate( planManager.getUserService(),
                                              service.list( Participation.class ) );

        List<Actor> actors = findFreeActors( findAssignedActors( users ),
                                             service.getAssignments().getActualActors() );

        final List<User> unassigned = findUnassignedUsers( service );

        List<Plan> otherPlans = findOtherPlans( plan );
        List<User> otherPlanners = findOtherPlanners( planManager.getUserService(), plan );
        addChannelsLogo();
        add(
            new Label( "userName", user.getUsername() ),
            new Label( "planName", plan.toString() ),

            new WebMarkupContainer( "activeDiv" ).add(
                new ListView<Participation>( "activeResponders", users ) {
                    @Override
                    protected void populateItem( ListItem<Participation> item ) {
                        PageParameters parameters = new PageParameters();
                        Participation p = item.getModelObject();
                        parameters.put( PLAN, uri );
                        parameters.put( VERSION, version );
                        Actor actor = p.getActor();
                        parameters.put( "agent", actor.getId() );
                        String userName = p.getUsername();
                        parameters.put( "user", userName );
                        User otherUser = planManager.getUserService().getUserNamed( userName );
                        item.add(
                            new BookmarkablePageLink<ParticipantPage>(
                                "responder", ParticipantPage.class, parameters )
                                  .add( new Label( "responderName", otherUser.getFullName() )
                                            .setRenderBodyOnly( true ) ),
                            new ExternalLink( "userName", "mailTo:" + otherUser.getEmail(), userName ),
                            new WebMarkupContainer( "detachItem" )
                                .add( new Link<Participation>( "removeLink", item.getModel() ) {
                                    @Override
                                    public void onClick() {
                                        Participation participation = getModelObject();
                                        try {
                                            lockManager.lock( user.getUsername(), plan.getId() );
                                            assign( getCommander( plan ), participation, null );
                                            lockManager.release( user.getUsername(), plan.getId() );
                                        } catch ( LockingException e ) {
                                            LoggerFactory.getLogger( getClass() ).warn(
                                                "Unable to get plan lock",
                                                e );
                                        }
                                        getPage().detachModels();
                                        setRedirect( true );
                                        setResponsePage( ParticipantPage.class, getPage().getPageParameters() );
                                    }
                                } )
                                .setOutputMarkupId( true ),
                            new Label( "profile", actor.toString() )
                                            .setRenderBodyOnly( true )
                        );

                        if ( item.getIndex() == getViewSize() - 1 )
                            item.add( new AttributeAppender( "class", true,
                                                             new Model<String>( "last" ), " " ) );
                    }
                }
            ).setVisible( !users.isEmpty() ),

            new WebMarkupContainer( "agentsDiv" ).add(
                new ListView<Actor>( "agents", actors ) {
                    @Override
                    protected void populateItem( ListItem<Actor> item ) {
                        final Actor actor = item.getModelObject();
                        PageParameters parameters =
                            ParticipantPage.createParameters( actor, uri, version );

                        item.add(
                            new WebMarkupContainer( "assign" )
                                .add( new ListView<User>( "addUser", unassigned ) {
                                    @Override
                                    protected void populateItem( ListItem<User> tListItem ) {
                                        User u = tListItem.getModelObject();
                                        tListItem.add( new Link<User>( "addLink", tListItem.getModel() ) {
                                            @Override
                                            public void onClick() {
                                                try {
                                                    Commander cmdr = getCommander( plan );
                                                    String username = getModelObject().getUsername();
                                                    lockManager.lock( user.getUsername(), plan.getId() );
                                                    assign( cmdr,
                                                            findParticipation( cmdr, username ),
                                                            actor );
                                                    lockManager.release( user.getUsername(), plan.getId() );
                                                } catch ( LockingException e ) {
                                                    LoggerFactory.getLogger( getClass() ).warn( "Unable to get plan lock", e );
                                                }
                                                getPage().detachModels();
                                                setRedirect( true );
                                                setResponsePage( ParticipantPage.class,
                                                                 getPage().getPageParameters() );
                                            }
                                        }.add( new Label( "userLink", u.getFullName() ) ) );
                                    }
                                } )
                                .setVisible( !unassigned.isEmpty() ),

                            new BookmarkablePageLink<ParticipantPage>(
                                "responder", ParticipantPage.class, parameters )
                                    .add( new Label( "responderName", actor.getNormalizedName() )
                                              .setRenderBodyOnly( true ) ),

                            new WebMarkupContainer( "many" )
                                            .setVisible( !actor.isSingular() )
                        ).setOutputMarkupId( true );

                        if ( item.getIndex() == getViewSize() - 1 )
                            item.add( new AttributeAppender( "class", true,
                                                             new Model<String>( "last" ), " " ) );

                    }
                }
            ).setVisible( !actors.isEmpty() ),

            new WebMarkupContainer( "plansDiv" ).add(
                new ListView<Plan>( "otherPlans", otherPlans ) {
                    @Override
                    protected void populateItem( ListItem<Plan> item ) {
                        PageParameters parameters = new PageParameters();
                        Plan p = item.getModelObject();
                        parameters.put( PLAN, p.getUri() );
                        parameters.put( VERSION, p.getVersion() );

                        item.add( new BookmarkablePageLink<ParticipantPage>(
                            "responderList", ParticipantPage.class, parameters )
                                      .add( new Label( "otherPlanName", p.toString() )
                                                .setRenderBodyOnly( true ) ) );

                        if ( item.getIndex() == getViewSize() - 1 )
                            item.add( new AttributeAppender( "class", true,
                                                             new Model<String>( "last" ), " " ) );
                    }
                }
            ).setVisible( !otherPlans.isEmpty() ),

            new WebMarkupContainer( "plannersDiv" ).add(
                new ListView<User>( "otherPlanners", otherPlanners ) {
                    @Override
                    protected void populateItem( ListItem<User> item ) {
                        User user = item.getModelObject();
                        item.add(
                            new ExternalLink( "planner", "mailTo:" + user.getEmail(),
                                              user.getFullName() ) );
                        if ( item.getIndex() == getViewSize() - 1 )
                            item.add( new AttributeAppender( "class", true,
                                                             new Model<String>( "last" ), " " ) );
                    }
                }
            ).setVisible( !otherPlanners.isEmpty() )

        );
    }

    private static void assign( Commander commander, Participation participation, Actor actor ) {
        participation.setActor( actor );
        commander.doUnsafeCommand( new UpdatePlanObject( participation,
                                                   "actor",
                                                   actor,
                                                   Action.Set ) );

    }

    /**
     * Commander needs some tending to prior to use.
     * @param plan a plan
     * @return a commander
     * */
    private Commander getCommander( Plan plan ) {
        // Adjust so commander actually behave as expected
        user.setPlan( plan );
        PlanService service = createService( plan );
        commander.setPlanDao( service.getDao() );
        commander.setLockManager( lockManager );

        return commander;
    }

    private static Participation findParticipation( Commander cmdr, String username ) {
        QueryService planService = cmdr.getQueryService();
        Participation participation = planService.findParticipation( username );
        if ( participation == null ) {
            Participation newPart = (Participation) cmdr.doUnsafeCommand(
                new CreateEntityIfNew(
                        Participation.class,
                        username,
                        ModelEntity.Kind.Actual )
                ).getSubject( planService );

            newPart.setActual();
            return newPart;
        }
        else
            return participation;

    }

    private void addChannelsLogo() {
        WebMarkupContainer channels_logo = new WebMarkupContainer( "channelsHome");
        channels_logo.add( new AjaxEventBehavior( "onclick") {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                String homeUrl =  AbstractChannelsWebPage.redirectUrl( "home", getPlan() );
                RedirectPage page =  new RedirectPage( homeUrl );
                setResponsePage( page );
            }
        });
        add( channels_logo );
    }

    private Plan getPlan() {
        return user.getPlan();
    }


    private static List<User> findUnassignedUsers( PlanService service ) {
        String uri = service.getPlan().getUri();
        Collection<User> inputCollection = service.getUserService().getUsers( uri );
        List<User> answer = new ArrayList<User>( inputCollection.size());
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

    private List<Plan> findOtherPlans( Plan current ) {
        Collection<Plan> allPlans = planManager.getPlannablePlans( user );
        List<Plan> answer = new ArrayList<Plan>( allPlans.size());
        for ( Plan other : allPlans )
            if ( !current.equals( other ) )
                answer.add( other );

        return answer;
    }

    private List<User> findOtherPlanners(
        UserService userService, Plan plan ) {
        String me = user.getUsername();

        Collection<User> planners = userService.getPlanners( plan.getUri() );
        List<User> answer = new ArrayList<User>( planners.size());
        for ( User u : planners )
            if ( !me.equals( u.getUsername() ) )
                answer.add( u );

        return answer;
    }

    private static List<Actor> findFreeActors( Set<Actor> assigned, Collection<Actor> actors ) {
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

    private static Set<Actor> findAssignedActors( List<Participation> users ) {
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
     * @param userService to check if users are still valid
     * @param participations the participations to check
     * @return filtered list
     */
    private static List<Participation> validate(
        UserService userService, Collection<Participation> participations ) {

        List<Participation> answer = new ArrayList<Participation>( participations.size() );
        for ( Participation item : participations ) {
            String userName = item.getUsername();
            if ( item.getActor() != null
                    && userName != null
                    && userService.getUserNamed( userName ) != null )
                answer.add( item );
        }

        return answer;
    }
}
