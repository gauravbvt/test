package com.mindalliance.channels.pages.reports.infoNeeds;

import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Participation;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.pages.reports.AbstractAllParticipantsPage;
import com.mindalliance.channels.query.PlanService;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;

/**
 * All participants and agents info needs.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/27/11
 * Time: 12:11 PM
 */
public class AllInfoNeedsPage extends AbstractAllParticipantsPage {

    public AllInfoNeedsPage() {
        super( InfoNeedsPage.class );
    }

    public AllInfoNeedsPage( PageParameters parameters ) {
        super( parameters );
    }


    protected void initComponents( PlanService service, final Plan plan ) {
        addChannelsLogo();
        add(
            new Label( "userName", getUser().getUsername() ),
            new Label( "planName", plan.toString() ),

            new WebMarkupContainer( "activeDiv" ).add(
                new ListView<Participation>( "activeResponders", getUsers() ) {
                    @Override
                    protected void populateItem( ListItem<Participation> item ) {
                        PageParameters parameters = new PageParameters();
                        Participation p = item.getModelObject();
                        parameters.put( PLAN, getUri() );
                        parameters.put( VERSION, getVersion() );
                        Actor actor = p.getActor();
                        parameters.put( "agent", actor.getId() );
                        String userName = p.getUsername();
                        parameters.put( "user", userName );
                        User otherUser = getPlanManager().getUserService().getUserNamed( userName );
                        item.add(
                            new BookmarkablePageLink<InfoNeedsPage>(
                                "responder", InfoNeedsPage.class, parameters )
                                  .add( new Label( "responderName", otherUser.getFullName() )
                                            .setRenderBodyOnly( true ) ),
                            new ExternalLink( "userName", "mailTo:" + otherUser.getEmail(), userName ),
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
                            InfoNeedsPage.createParameters( actor, getUri(), getVersion() );

                        item.add(

                            new BookmarkablePageLink<InfoNeedsPage>(
                                "responder", InfoNeedsPage.class, parameters )
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
            ).setVisible( !getActors().isEmpty() )

        );
    }


}
