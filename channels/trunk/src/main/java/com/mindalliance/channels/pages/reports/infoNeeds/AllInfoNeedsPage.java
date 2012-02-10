package com.mindalliance.channels.pages.reports.infoNeeds;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
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
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

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
        super( new PageParameters(  ) );
    }

    public AllInfoNeedsPage( PageParameters parameters ) {
        super( parameters );
    }


    protected void initComponents( QueryService service, final Plan plan ) {
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
                        parameters.set( PLAN, getUri() );
                        parameters.set( VERSION, getVersion() );
                        Actor actor = p.getActor();
                        parameters.set( "agent", actor.getId() );
                        String userName = p.getUsername();
                        parameters.set( "user", userName );
                        ChannelsUser otherUser = getUserDao().getUserNamed( userName );
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
                            item.add( new AttributeAppender( "class",
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
                            item.add( new AttributeAppender( "class",
                                                             new Model<String>( "last" ), " " ) );

                    }
                }
            ).setVisible( !getActors().isEmpty() )

        );
    }


}
