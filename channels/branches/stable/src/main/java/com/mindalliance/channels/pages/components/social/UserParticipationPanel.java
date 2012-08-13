package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.PlanParticipation;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        addNoParticipation();
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
        ListView<ParticipationWrapper> participationList = new ListView<ParticipationWrapper>(
                "participations",
                participations() ) {
            @Override
            protected void populateItem( ListItem<ParticipationWrapper> item ) {
                ParticipationWrapper participationWrapper = item.getModelObject();
                String assignation = getAssignation( participationWrapper.getActor( getQueryService() ) );
                AjaxCheckBox confirmedCheckBox = new AjaxCheckBox(
                        "confirmed",
                        new PropertyModel<Boolean>( participationWrapper, "confirmed" )
                ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        resetAll();
                        getPlanManager().clearCache();
                        target.add( userParticipationContainer );
                        update( target, new Change( Change.Type.Updated, getPlan(), "participation" ) );
                    }
                };
                boolean open = participationWrapper.isOpen( getQueryService() );
                confirmedCheckBox.setEnabled( open );
                confirmedCheckBox.add( new AttributeModifier(
                        "title",
                        open ? "Open participation" : "Planned participation"
                ) );
                item.add( confirmedCheckBox );
                item.add( new Label( "participation", assignation ) );
            }
        };
        userParticipationContainer.add( participationList );
    }

    private void addNoParticipation() {
        Label noParticipation = new Label( "noParticipation", "I do not yet participate" );
        noParticipation.setVisible( currentParticipations().isEmpty() );
        userParticipationContainer.add(  noParticipation );
    }

    private List<ParticipationWrapper> participations() {
        final QueryService queryService = getQueryService();
        List<ParticipationWrapper> wrappers = new ArrayList<ParticipationWrapper>();
        final List<PlanParticipation> currentParticipations = currentParticipations();
        for ( PlanParticipation participation : currentParticipations ) {
            wrappers.add( new ParticipationWrapper( participation, true ) );
        }
        for ( Actor actor : planParticipationService.findOpenActors( getUser(), getQueryService() ) ) {
            PlanParticipation openParticipation = new PlanParticipation(
                    getUsername(),
                    getPlan(),
                    getUser(),
                    actor );
            wrappers.add( new ParticipationWrapper( openParticipation, false ) );
        }
        Collections.sort(
                wrappers,
                new Comparator<ParticipationWrapper>() {
                    @Override
                    public int compare( ParticipationWrapper p1, ParticipationWrapper p2 ) {
                        boolean p1Open = p1.isOpen( queryService );
                        boolean p2Open = p2.isOpen( queryService );
                        if ( !p1Open && p2Open ) return -1;
                        if ( p1Open && !p2Open ) return 1;
                        return p1.getActor( queryService ).getName()
                                .compareTo( p2.getActor( queryService ).getName() );
                    }
                } );
        return wrappers;
    }

    private List<PlanParticipation> currentParticipations() {
        return planParticipationService.getParticipations(
                getPlan(),
                getUser().getUserInfo(),
                getQueryService() );
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

    public class ParticipationWrapper implements Serializable {

        private PlanParticipation participation;
        private boolean confirmed;

        public ParticipationWrapper( PlanParticipation participation, boolean confirmed ) {
            this.participation = participation;
            this.confirmed = confirmed;
        }

        public PlanParticipation getParticipation() {
            return participation;
        }

        public void setParticipation( PlanParticipation participation ) {
            this.participation = participation;
        }

        public boolean isConfirmed() {
            return confirmed;
        }

        public void setConfirmed( boolean confirmed ) {
            this.confirmed = confirmed;
            if ( confirmed ) {
                if ( getQueryService().getPlanParticipationService().isParticipationAvailable(
                        participation.getActor( getQueryService() ),
                        getUser(),
                        getQueryService() ) ) {
                    planParticipationService.save( participation );
                }
            } else {
                planParticipationService.delete( participation );
            }
            getPlanManager().clearCache(); // Must manually clear the cache
        }

        public Actor getActor( QueryService queryService ) {
            return participation.getActor( queryService );
        }

        public boolean isOpen( QueryService queryService ) {
            return getActor( queryService ).isOpenParticipation();
        }
    }


}
