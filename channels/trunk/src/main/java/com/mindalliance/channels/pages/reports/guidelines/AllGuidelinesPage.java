// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.pages.reports.guidelines;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.PlanParticipation;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.pages.reports.AbstractAllParticipantsPage;
import com.mindalliance.channels.pages.reports.protocols.ProtocolsPage;
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

public class AllGuidelinesPage extends AbstractAllParticipantsPage {

    public AllGuidelinesPage() {
        super( new PageParameters() );
    }

    public AllGuidelinesPage( PageParameters parameters ) {
        super( parameters );
    }

    @Override
    protected String getPageName() {
        return "All Info Sharing Guidelines";
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
                                        ? "(Users who participate as agents in this plan and thus have guidelines)"
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
                                String participatingUsername = p.getParticipant().getUsername();
                                parameters.set( "user", participatingUsername );
                                ChannelsUser participatingUser = getUserDao().getUserNamed( participatingUsername );
                                item.add(
                                        new Label( "participantName", participatingUser.getFullName() )
                                                .setRenderBodyOnly( true ),
                                        new BookmarkablePageLink<GuidelinesPage>(
                                                "participation", ProtocolsPage.class, parameters )
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
                                                new BookmarkablePageLink<GuidelinesPage>(
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

}
