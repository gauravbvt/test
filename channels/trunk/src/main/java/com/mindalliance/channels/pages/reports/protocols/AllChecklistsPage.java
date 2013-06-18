// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.participation.Agent;
import com.mindalliance.channels.core.community.participation.UserParticipation;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
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
 * Page giving access to all participant protocols.
 */

public class AllChecklistsPage extends AbstractAllParticipantsPage {

    public AllChecklistsPage() {
        super( new PageParameters() );
    }

    public AllChecklistsPage( PageParameters parameters ) {
        super( parameters );
    }

    @Override
    protected String getHelpSectionId() {
        return "all-protocols-page";
    }

    @Override
    protected String getHelpTopicId() {
        return "about-all-protocols-page";
    }



    @Override
    public String getPageName() {
        return "All Participants' Checklists";
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
                                        new BookmarkablePageLink<ChecklistsPage>(
                                                "participant", ChecklistsPage.class, makeUserParameters( participatingUsername ) )
                                                .add( new Label( "participantName", participatingUser.getFullName() )
                                                        .setRenderBodyOnly( true ) ),
                                        new BookmarkablePageLink<ChecklistsPage>(
                                                "participation", ChecklistsPage.class, makeAgentParameters( participatingUsername, agent ) )
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
                                                ChecklistsPage.createParameters(
                                                        agent,
                                                        getPlanCommunityUri() );

                                        item.add(
                                                new BookmarkablePageLink<ChecklistsPage>(
                                                        "agent", ChecklistsPage.class, parameters )
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
