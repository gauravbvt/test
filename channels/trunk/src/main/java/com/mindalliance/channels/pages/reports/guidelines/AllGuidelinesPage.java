// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.pages.reports.guidelines;

import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.command.LockManager;
import com.mindalliance.channels.core.command.LockingException;
import com.mindalliance.channels.core.command.commands.UpdateObject.Action;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.dao.UserDao;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Participation;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.pages.reports.AbstractAllParticipantsPage;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** A planner's table of contents to responders pages. */

public class AllGuidelinesPage extends AbstractAllParticipantsPage {

    public AllGuidelinesPage() {
        super( new PageParameters(  ) );
    }

    public AllGuidelinesPage( PageParameters parameters ) {
        super( parameters );
    }

    protected void initComponents( QueryService service, final Plan plan ) {
        List<Plan> otherPlans = findOtherPlans( plan );
        List<User> otherPlanners = findOtherPlanners( getUserDao(), plan );
        add(
            new Label( "userName", user.getUsername() ),
            new Label( "planName", plan.toString() ),

            new WebMarkupContainer( "activeDiv" ).add(
                new ListView<Participation>( "activeResponders", getUsers() ) {
                    @Override
                    protected void populateItem( ListItem<Participation> item ) {
                        PageParameters parameters = new PageParameters();
                        Participation p = item.getModelObject();
                        parameters.set( PLAN, getUri() );
                        parameters.set( VERSION, getVersion() );
                        Actor actor = p.getActor();
                        parameters.set( "agent", actor.getId() );
                        String userName = p.getUsername();
                        parameters.set( "user", userName );
                        User otherUser = getUserDao().getUserNamed( userName );
                        item.add(
                            new BookmarkablePageLink<GuidelinesPage>(
                                "responder", GuidelinesPage.class, parameters )
                                  .add( new Label( "responderName", otherUser.getFullName() )
                                            .setRenderBodyOnly( true ) ),
                            new ExternalLink( "userName", "mailTo:" + otherUser.getEmail(), userName ),
                            new WebMarkupContainer( "detachItem" )
                                .add( new Link<Participation>( "removeLink", item.getModel() ) {
                                    @Override
                                    public void onClick() {
                                        Participation participation = getModelObject();
                                        try {
                                            Commander commander = getCommander( plan );
                                            LockManager lockManager = commander.getLockManager();
                                            lockManager.lock( user.getUsername(), plan.getId() );
                                            assign( commander, participation, null );
                                            lockManager.release( user.getUsername(), plan.getId() );
                                        } catch ( LockingException e ) {
                                            LoggerFactory.getLogger( getClass() ).warn(
                                                "Unable to get plan lock",
                                                e );
                                        }
                                        getPage().detachModels();
                                        setResponsePage( GuidelinesPage.class, getPage().getPageParameters() );
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
            ).setVisible( !getUsers().isEmpty() ),

            new WebMarkupContainer( "agentsDiv" ).add(
                new ListView<Actor>( "agents", getActors() ) {
                    @Override
                    protected void populateItem( ListItem<Actor> item ) {
                        final Actor actor = item.getModelObject();
                        PageParameters parameters =
                            GuidelinesPage.createParameters( actor, getUri(), getVersion() );

                        item.add(
                            new WebMarkupContainer( "assign" )
                                .add( new ListView<User>( "addUser", getUnassigned() ) {
                                    @Override
                                    protected void populateItem( ListItem<User> tListItem ) {
                                        User u = tListItem.getModelObject();
                                        tListItem.add( new Link<User>( "addLink", tListItem.getModel() ) {
                                            @Override
                                            public void onClick() {
                                                try {
                                                    Commander cmdr = getCommander( plan );
                                                    String username = getModelObject().getUsername();
                                                    LockManager lockManager = cmdr.getLockManager();
                                                    lockManager.lock( user.getUsername(), plan.getId() );
                                                    assign( cmdr, findParticipation( cmdr, username ), actor );
                                                    lockManager.release( user.getUsername(), plan.getId() );
                                                } catch ( LockingException e ) {
                                                    LoggerFactory.getLogger( getClass() ).warn( "Unable to get plan lock", e );
                                                }
                                                getPage().detachModels();
                                                setResponsePage( GuidelinesPage.class,
                                                                 getPage().getPageParameters() );
                                            }
                                        }.add( new Label( "userLink", u.getFullName() ) ) );
                                    }
                                } )
                                .setVisible( !getUnassigned().isEmpty() ),

                            new BookmarkablePageLink<GuidelinesPage>(
                                "responder", GuidelinesPage.class, parameters )
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
            ).setVisible( !getActors().isEmpty() ),

            new WebMarkupContainer( "plansDiv" ).add(
                new ListView<Plan>( "otherPlans", otherPlans ) {
                    @Override
                    protected void populateItem( ListItem<Plan> item ) {
                        PageParameters parameters = new PageParameters();
                        Plan p = item.getModelObject();
                        parameters.set( PLAN, p.getUri() );
                        parameters.set( VERSION, p.getVersion() );

                        item.add( new BookmarkablePageLink<GuidelinesPage>(
                            "responderList", GuidelinesPage.class, parameters )
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
        commander.doUnsafeCommand( new UpdatePlanObject( User.current().getUsername(), participation,
                                                   "actor",
                                                   actor,
                                                   Action.Set ) );

    }



    private List<Plan> findOtherPlans( Plan current ) {
        Collection<Plan> allPlans = getPlanManager().getPlannablePlans( getUser() );
        List<Plan> answer = new ArrayList<Plan>( allPlans.size());
        for ( Plan other : allPlans )
            if ( !current.equals( other ) )
                answer.add( other );

        return answer;
    }

    private List<User> findOtherPlanners(
        UserDao userDao, Plan plan ) {
        String me = user.getUsername();

        Collection<User> planners = userDao.getPlanners( plan.getUri() );
        List<User> answer = new ArrayList<User>( planners.size());
        for ( User u : planners )
            if ( !me.equals( u.getUsername() ) )
                answer.add( u );

        return answer;
    }


}
