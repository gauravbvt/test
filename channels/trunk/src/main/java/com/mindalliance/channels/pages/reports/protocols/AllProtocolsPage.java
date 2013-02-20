// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.participation.Agent;
import com.mindalliance.channels.core.community.participation.UserParticipation;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.pages.reports.AbstractAllParticipantsPage;
import com.mindalliance.channels.social.model.Feedback;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * A planner's table of contents to responders pages.
 */

public class AllProtocolsPage extends AbstractAllParticipantsPage {

    public AllProtocolsPage() {
        super( new PageParameters() );
    }

    public AllProtocolsPage( PageParameters parameters ) {
        super( parameters );
    }

    @Override
    protected String getPageName() {
        return "All Collaboration Protocols";
    }

    @Override
    protected String getFeedbackType() {
        return Feedback.PARTICIPATING;
    }


    protected void initComponents( QueryService service, final CommunityService communityService ) {
        boolean isPlanner = getUser().isPlanner( communityService.getPlan().getUri() );
        getContainer().add(
                new WebMarkupContainer( "note" ).setVisible( isPlanner ),
                new WebMarkupContainer( "activeDiv" ).add(
                        new Label( 
                                "participationNote",
                                isPlanner
                                        ? "(Users who participate as agents in this plan and thus have their protocols)"
                                        : "(Your participation in this plan)"
                                ),
                        new ListView<UserParticipation>( "participatingUsers", getParticipations() ) {
                            @Override
                            protected void populateItem( ListItem<UserParticipation> item ) {
                                UserParticipation p = item.getModelObject();
                                Agent agent = p.getAgent( getCommunityService() );
                                String participatingUsername = p.getParticipant().getUsername();
                                ChannelsUser participatingUser = getUserDao().getUserNamed( participatingUsername );
                                item.add(
                                        new BookmarkablePageLink<ProtocolsPage>(
                                                "participant", ProtocolsPage.class, makeUserParameters( participatingUsername ) )
                                                .add( new Label( "participantName", participatingUser.getFullName() )
                                                        .setRenderBodyOnly( true ) ),
                                        new BookmarkablePageLink<ProtocolsPage>(
                                                "participation", ProtocolsPage.class, makeAgentParameters( participatingUsername, agent ) )
                                                .add( new Label( "participationName", agent.getName() )
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
                                                ProtocolsPage.createParameters(
                                                        agent,
                                                        getPlanCommunityUri(),
                                                        getPlan().getUri(),
                                                        getPlanVersion() );

                                        item.add(
                                                new BookmarkablePageLink<ProtocolsPage>(
                                                        "agent", ProtocolsPage.class, parameters )
                                                        .add( new Label( "agentName", agent.getName() )
                                                                .setRenderBodyOnly( true ) ),

                                                new Label(
                                                        "participationPlurality",
                                                        agent.getActor().getParticipationPlurality() )
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

    private PageParameters makeUserParameters( String username ) {
        PageParameters parameters = new PageParameters();
        parameters.set( COMMUNITY_PARM, getPlanCommunityUri() );
        parameters.set( "user", username );
        return parameters;
    }

    private PageParameters makeAgentParameters( String username, Agent agent ) {
        PageParameters parameters = new PageParameters();
        parameters.set( COMMUNITY_PARM, getPlanCommunityUri() );
        parameters.set( USER, username );
        parameters.set( AGENT, agent.getId() );
        if ( agent.getOrganizationParticipation() != null )
            parameters.set( ORG, agent.getOrganizationParticipation().getId() );
        return parameters;
    }

}
