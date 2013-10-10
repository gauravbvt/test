package com.mindalliance.channels.pages.components.manager;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.ParticipationManager;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.db.data.communities.UserParticipation;
import com.mindalliance.channels.db.data.communities.UserParticipationConfirmation;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.db.services.communities.UserParticipationConfirmationService;
import com.mindalliance.channels.db.services.communities.UserParticipationService;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Participation todos panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/4/12
 * Time: 11:07 AM
 */
public class ParticipationConfirmationsPanel extends AbstractUpdatablePanel {

    @SpringBean
    private ParticipationManager participationManager;


    public ParticipationConfirmationsPanel( String id, IModel<? extends Identifiable> model ) {
        super( id, model );
        init();
    }

    private void init() {
        addParticipationToConfirm();
    }

    private void addParticipationToConfirm() {
        WebMarkupContainer confirmationsContainer = new WebMarkupContainer( "supervisorParticipationConfirmations" );
        confirmationsContainer.setOutputMarkupId( true );
        addOrReplace( confirmationsContainer );
        List<ParticipationConfirmationWrapper> confirmationWrappers = participationConfirmationWrappers();
        ListView<ParticipationConfirmationWrapper> confirmationList = new ListView<ParticipationConfirmationWrapper>(
                "participationConfirmations",
                confirmationWrappers
        ) {
            @Override
            protected void populateItem( ListItem<ParticipationConfirmationWrapper> item ) {
                ParticipationConfirmationWrapper confirmationWrapper = item.getModelObject();
                AjaxCheckBox confirmedCheckBox = new AjaxCheckBox(
                        "confirmed",
                        new PropertyModel<Boolean>( confirmationWrapper, "confirmed" )
                ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        resetAllAndUpdate( target );
                    }
                };
                item.add( confirmedCheckBox );
                UserRecord participatingUser = confirmationWrapper.getParticipatingUser();
                item.add( new Label( "user", participatingUser.getFullName() ) );
                item.add( new Label( "email", participatingUser.getEmail() ) );
                Agent participationAgent = confirmationWrapper.getAgent( getCommunityService() );
                if ( participationAgent != null )
                    addTipTitle( item, participationAgent.getRequirementsDescription( getCommunityService()) );
                item.add( new Label( "agent", participationAgent == null ? "?" : participationAgent.getName() ) );
            }
        };
        confirmationsContainer.add( confirmationList );
        confirmationsContainer.setVisible( !confirmationWrappers.isEmpty() );
    }

    @SuppressWarnings( "unchecked" )
    private List<ParticipationConfirmationWrapper> participationConfirmationWrappers() {
        CommunityService communityService = getCommunityService();
        UserParticipationService userParticipationService = communityService.getUserParticipationService();
        List<ParticipationConfirmationWrapper> wrappers = new ArrayList<ParticipationConfirmationWrapper>();
        // Find all plan participation confirmations made by a supervisor user participates as (= confirmed)
        List<UserParticipationConfirmation> userConfirmations = communityService
                .getUserParticipationConfirmationService()
                .listUserParticipationsConfirmedBy( getUser(), communityService );
        for ( UserParticipationConfirmation userConfirmation : userConfirmations ) {
            wrappers.add( new ParticipationConfirmationWrapper( userConfirmation, true ) );
        }
        // Find all plan participation confirmations user needs to confirm as supervisor
        List<UserParticipation> userParticipationAwaitingUserConfirmation = participationManager.
                listUserParticipationsAwaitingConfirmationBy( getUser(), communityService );
        for ( UserParticipation participationToBeValidated : userParticipationAwaitingUserConfirmation ) {
            UserParticipationConfirmation confirmationToBe = new UserParticipationConfirmation(
                    participationToBeValidated,
                    null,
                    getUsername() );
            wrappers.add( new ParticipationConfirmationWrapper( confirmationToBe, false ) );
        }
        return wrappers;
    }

    private void resetAllAndUpdate( AjaxRequestTarget target ) {
        resetAll();
        target.add( this );
        update( target, new Change( Change.Type.Updated, getPlanCommunity(), "participation" ) );
    }

    private void resetAll() {
        addParticipationToConfirm();
    }

    public class ParticipationConfirmationWrapper implements Serializable {
        private UserParticipationConfirmation participationConfirmation;
        private boolean confirmed;

        public ParticipationConfirmationWrapper(
                UserParticipationConfirmation participationConfirmation,
                boolean confirmed ) {
            this.confirmed = confirmed;
            this.participationConfirmation = participationConfirmation;
        }

        public boolean isConfirmed() {
            return confirmed;
        }

        public void setConfirmed( boolean confirmed ) {
            this.confirmed = confirmed;
            CommunityService communityService = getCommunityService();
            UserParticipationConfirmationService userParticipationConfirmationService =
                    communityService.getUserParticipationConfirmationService();
            for ( Agent supervisor : communityService.getParticipationManager().listSupervisorsUserParticipatesAs(
                    participationConfirmation.getUserParticipation( communityService ),
                    getUser(),
                    communityService
            ) ) {
                if ( confirmed ) {
                    userParticipationConfirmationService.addParticipationConfirmation(
                            participationConfirmation.getUserParticipation( communityService ),
                            supervisor,
                            getUser(),
                            communityService );
                } else {
                    userParticipationConfirmationService.removeParticipationConfirmation(
                            participationConfirmation.getUserParticipation( communityService ),
                            supervisor,
                            communityService );
                }
            }
        }

        public UserRecord getParticipatingUser() {
            CommunityService communityService = getCommunityService();
            UserParticipation userParticipation = participationConfirmation.getUserParticipation(communityService);
            return userParticipation != null ? userParticipation.getParticipant( communityService ) : null;
        }

        public Agent getAgent( CommunityService communityService ) {
            UserParticipation userParticipation = participationConfirmation.getUserParticipation(communityService);
            return userParticipation != null ? userParticipation.getAgent( communityService ) : null;
        }
    }



}
