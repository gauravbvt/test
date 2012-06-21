package com.mindalliance.channels.pages.reports.infoNeeds;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.PlanParticipation;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.pages.reports.AbstractAllParticipantsPage;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
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
        boolean isPlanner = getUser().isPlanner( plan.getUri() );
        getContainer().add(
            new Label( "planName", plan.toString() ),
            new WebMarkupContainer( "note" ).setVisible( isPlanner ),
            new WebMarkupContainer( "activeDiv" ).add(
                    new Label(
                            "participationNote",
                            isPlanner
                                    ? "(Users who participate as agents in this plan and thus have info needs)"
                                    : "(Your participation in this plan)"
                    ),
               new ListView<PlanParticipation>( "participatingUsers", getParticipations() ) {
                    @Override
                    protected void populateItem( ListItem<PlanParticipation> item ) {
                        PageParameters parameters = new PageParameters();
                        PlanParticipation p = item.getModelObject();
                        parameters.set( PLAN, getUri() );
                        parameters.set( VERSION, getVersion() );
                        Actor actor = p.getActor( getQueryService() );
                        parameters.set( "agent", actor.getId() );
                        String participantUsername = p.getParticipant().getUsername();
                        parameters.set( "user", participantUsername );
                        ChannelsUser participatingUser = getUserDao().getUserNamed( participantUsername );
                        item.add(
                                new Label( "participantName", participatingUser.getFullName() )
                                        .setRenderBodyOnly( true ),
                                new BookmarkablePageLink<InfoNeedsPage>(
                                        "participation", InfoNeedsPage.class, parameters )
                                        .add( new Label( "participationName", actor.toString() )
                                                .setRenderBodyOnly( true ) )
                        );

                        if ( item.getIndex() == getViewSize() - 1 )
                            item.add( new AttributeAppender( "class",
                                                             new Model<String>( "last" ), " " ) );
                    }
                }
            ).setVisible( !getParticipations().isEmpty() ),

            new WebMarkupContainer( "agentsDiv" ).add(
                    isPlanner() ?
               new ListView<Actor>( "agents", getActors() ) {
                    @Override
                    protected void populateItem( ListItem<Actor> item ) {
                        final Actor actor = item.getModelObject();
                        PageParameters parameters =
                            InfoNeedsPage.createParameters( actor, getUri(), getVersion() );

                        item.add(

                            new BookmarkablePageLink<InfoNeedsPage>(
                                "agent", InfoNeedsPage.class, parameters )
                                    .add( new Label( "agentName", actor.getNormalizedName() )
                                              .setRenderBodyOnly( true ) ),

                                new Label( "participationPlurality", actor.getParticipationPlurality() )
                        ).setOutputMarkupId( true );

                        if ( item.getIndex() == getViewSize() - 1 )
                            item.add( new AttributeAppender( "class",
                                                             new Model<String>( "last" ), " " ) );

                    }
                }
                            : new Label( "agents", "" )
          ).setVisible( !getActors().isEmpty() && isPlanner )

        );
    }


}
