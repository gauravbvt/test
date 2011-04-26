// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.pages.responders;

import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.dao.UserService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Participation;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.query.PlanService;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValueConversionException;

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

public class AllResponders extends WebPage {

    private static final String PLAN = "plan";

    private static final String VERSION = "v";

    @SpringBean
    private User user;

    @SpringBean
    private PlanManager planManager;

    public AllResponders() {

        List<Plan> plans = planManager.getPlannablePlans( user );
        if ( plans.isEmpty() )
            throw new AbortWithWebErrorCodeException( SC_FORBIDDEN );

        Plan plan = plans.get( 0 );
        PageParameters pageParameters = new PageParameters();
        pageParameters.put( PLAN, plan.getUri() );
        pageParameters.put( VERSION, plan.getVersion() );

        setRedirect( true );
        setResponsePage( ResponderPage.class, pageParameters );
    }

    public AllResponders( PageParameters parameters ) {
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
                        User user = planManager.getUserService().getUserNamed( userName );

                        item.add(
                            new BookmarkablePageLink<ResponderPage>(
                                "responder", ResponderPage.class, parameters )
                                  .add( new Label( "responderName", user.getFullName() )
                                            .setRenderBodyOnly( true ) ),
                            new ExternalLink( "userName", "mailTo:" + user.getEmail(), userName ),
                            new WebMarkupContainer( "detachItem" )
                                .add( new Link<Participation>( "removeLink", item.getModel() ) {
                                    @Override
                                    public void onClick() {
                                        createService( plan ).remove( getModelObject() );
                                        planManager.save( plan );
                                        getPage().detachModels();
                                        setRedirect( true );
                                        setResponsePage( ResponderPage.class, getPage().getPageParameters() );
                                    }
                                } )
                                .setOutputMarkupId( true ),
                            new Label( "profile", actor.toString() )
                                            .setRenderBodyOnly( true )
                        );
                    }
                }
            ).setVisible( !users.isEmpty() ),

            new WebMarkupContainer( "agentsDiv" ).add(
                new ListView<Actor>( "agents", actors ) {
                    @Override
                    protected void populateItem( final ListItem<Actor> item ) {
                        final Actor actor = item.getModelObject();
                        PageParameters parameters =
                            ResponderPage.createParameters( actor, uri, version );

                        item.add(
                            new ListView<User>( "addUser", unassigned ) {
                                @Override
                                protected void populateItem( ListItem<User> tListItem ) {
                                    User u = tListItem.getModelObject();
                                    tListItem.add( new Link<User>( "addLink", tListItem.getModel() ) {
                                        @Override
                                        public void onClick() {
                                            PlanService s = createService( plan );
                                            String username = getModelObject().getUsername();
                                            Participation p = s.findParticipation( username );
                                            if ( p == null ) {
                                                p = new Participation( username );
                                                s.add( p );
                                            }
                                            p.setActor( actor );
                                            p.setActual();
                                            planManager.save( plan );

                                            getPage().detachModels();
                                            setRedirect( true );
                                            setResponsePage( ResponderPage.class, getPage().getPageParameters() );
                                        }
                                    }.add( new Label( "userLink", u.getFullName() ) ) );
                                }
                            },

                            new BookmarkablePageLink<ResponderPage>(
                                "responder", ResponderPage.class, parameters )
                                    .add( new Label( "responderName", actor.toString() )
                                              .setRenderBodyOnly( true ) ),

                            new WebMarkupContainer( "many" )
                                            .setVisible( actor.isArchetype() )
                        ).setOutputMarkupId( true );
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

                        item.add( new BookmarkablePageLink<ResponderPage>(
                            "responderList", ResponderPage.class, parameters )
                                      .add( new Label( "otherPlanName", p.toString() )
                                                .setRenderBodyOnly( true ) ) );
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
                    }
                }
            ).setVisible( !otherPlanners.isEmpty() )

        );
    }

    private static List<User> findUnassignedUsers( PlanService service ) {
        Collection<User> inputCollection = service.getUserService().getUsers();
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
            if ( !actor.isArchetype() )
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
