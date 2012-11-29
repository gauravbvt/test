// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.participation.PlanParticipation;
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


    protected void initComponents( QueryService service, final Plan plan ) {
        boolean isPlanner = getUser().isPlanner( plan.getUri() );
        getContainer().add(
                new Label( "planName", plan.toString() ),
                new WebMarkupContainer( "note" ).setVisible( isPlanner ),
                new WebMarkupContainer( "activeDiv" ).add(
                        new Label( 
                                "participationNote",
                                isPlanner
                                        ? "(Users who participate as agents in this plan and thus have their protocols)"
                                        : "(Your participation in this plan)"
                                ),
                        new ListView<PlanParticipation>( "participatingUsers", getParticipations() ) {
                            @Override
                            protected void populateItem( ListItem<PlanParticipation> item ) {
                                PlanParticipation p = item.getModelObject();
                                Actor actor = p.getActor( getQueryService() );
                                String participatingUsername = p.getParticipant().getUsername();
                                ChannelsUser participatingUser = getUserDao().getUserNamed( participatingUsername );
                                item.add(
                                        new BookmarkablePageLink<ProtocolsPage>(
                                                "participant", ProtocolsPage.class, makeUserParameters( participatingUsername ) )
                                                .add( new Label( "participantName", participatingUser.getFullName() )
                                                        .setRenderBodyOnly( true ) ),
                                        new BookmarkablePageLink<ProtocolsPage>(
                                                "participation", ProtocolsPage.class, makeActorParameters( participatingUsername, actor ) )
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
                                                ProtocolsPage.createParameters( actor, getUri(), getVersion() );

                                        item.add(
                                                new BookmarkablePageLink<ProtocolsPage>(
                                                        "agent", ProtocolsPage.class, parameters )
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

    private PageParameters makeUserParameters( String username ) {
        PageParameters parameters = new PageParameters();
        parameters.set( PLAN, getUri() );
        parameters.set( VERSION, getVersion() );
        parameters.set( "user", username );
        return parameters;
    }

    private PageParameters makeActorParameters( String username, Actor actor ) {
        PageParameters parameters = new PageParameters();
        parameters.set( PLAN, getUri() );
        parameters.set( VERSION, getVersion() );
        parameters.set( "user", username );
        parameters.set( "agent", actor.getId() );
        return parameters;
    }

}
