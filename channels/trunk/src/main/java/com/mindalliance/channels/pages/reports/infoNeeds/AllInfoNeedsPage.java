package com.mindalliance.channels.pages.reports.infoNeeds;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.participation.Agent;
import com.mindalliance.channels.core.community.participation.UserParticipation;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.db.data.messages.Feedback;
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
        this( new PageParameters(  ) );
    }

    public AllInfoNeedsPage( PageParameters parameters ) {
        super( parameters );
    }

    @Override
    public String getPageName() {
        return "All Info Needs";
    }

    @Override
    protected String getFeedbackType() {
        return Feedback.PARTICIPATING;
    }


    protected void initComponents( QueryService service, final CommunityService communityService ) {
        boolean isPlanner = getUser().isPlanner( communityService.getPlan().getUri() );
        getContainer().add(
            new Label( "planName", communityService.getPlan().toString() ),
            new WebMarkupContainer( "note" ).setVisible( isPlanner ),
            new WebMarkupContainer( "activeDiv" ).add(
                    new Label(
                            "participationNote",
                            isPlanner
                                    ? "(Users who participate as agents in this plan and thus have info needs)"
                                    : "(Your participation in this plan)"
                    ),
               new ListView<UserParticipation>( "participatingUsers", getParticipations() ) {
                    @Override
                    protected void populateItem( ListItem<UserParticipation> item ) {
                        PageParameters parameters = new PageParameters();
                        UserParticipation p = item.getModelObject();
                        parameters.set( COMMUNITY_PARM, getPlanCommunityUri() );
                        parameters.set( VERSION_PARM, getPlanVersion() );
                        Actor actor = p.getAgent(getCommunityService()).getActor();
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
               new ListView<Agent>( "agents", getAgents() ) {
                    @Override
                    protected void populateItem( ListItem<Agent> item ) {
                        final Agent agent = item.getModelObject();
                        PageParameters parameters =
                            InfoNeedsPage.createParameters( agent.getActor(), getPlanCommunity() );

                        item.add(

                            new BookmarkablePageLink<InfoNeedsPage>(
                                "agent", InfoNeedsPage.class, parameters )
                                    .add( new Label( "agentName", agent.getName() )
                                              .setRenderBodyOnly( true ) ),

                                new Label( "participationPlurality", agent.getActor().getParticipationPlurality() )
                        ).setOutputMarkupId( true );

                        if ( item.getIndex() == getViewSize() - 1 )
                            item.add( new AttributeAppender( "class",
                                                             new Model<String>( "last" ), " " ) );

                    }
                }
                            : new Label( "agents", "" )
          ).setVisible( !getAgents().isEmpty() && isPlanner )

        );
    }


}
