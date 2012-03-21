package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.PlanParticipation;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/20/12
 * Time: 4:34 PM
 */
public class UserParticipationPanel extends AbstractSocialListPanel {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( UserParticipationPanel.class );

    @SpringBean
    private ChannelsUser user;

    @SpringBean
    private PlanParticipationService planParticipationService;
    private WebMarkupContainer userParticipationContainer;


    public UserParticipationPanel( String id, SocialPanel socialPanel, boolean collapsible ) {
        super( id, collapsible );
        init();
    }

    protected void init() {
        super.init();
        resetAll();
    }

    private void resetAll() {
        addUserParticipationContainer();
        addUserRole();
        addParticipation();
    }

    private void addUserRole() {
        userParticipationContainer.add( new Label( "userRole", getUserRole() ) );
    }

    private void addUserParticipationContainer() {
        userParticipationContainer = new WebMarkupContainer( "userParticipation" );
        userParticipationContainer.setOutputMarkupId( true );
        addOrReplace( userParticipationContainer );
    }


    private void addParticipation() {
        ListView<PlanParticipation> participationList = new ListView<PlanParticipation>(
                "participations",
                planParticipationService.getParticipations( getPlan(), getUser().getUserInfo(), getQueryService() )
        ) {
            @Override
            protected void populateItem( ListItem<PlanParticipation> item ) {
                PlanParticipation participation = item.getModelObject();
                String assignation = getAssignation( participation.getActor( getQueryService() ) );
                item.add( new Label( "participation", assignation ) );
            }
        };
        userParticipationContainer.add( participationList );
    }

    private String getUserRole() {
        String userRole = user.isAdmin()
                ? "administrator"
                : user.isPlanner()
                ? "planner"
                : "participant";
        return ( ChannelsUtils.startsWithVowel( userRole ) ? " an " : " a " ) + userRole + ".";
    }


    private String getAssignation( Actor actor ) {
        StringBuilder sb = new StringBuilder();
        if ( actor != null ) {
            sb.append( "I participate as " );
            sb.append( actor.getName() );
            String channelsString = actor.getChannelsString();
            if ( !channelsString.isEmpty() ) {
                sb.append( ", reachable via " );
                sb.append( channelsString );
            }
            sb.append( '.' );
        }
        return sb.toString();
    }


}
