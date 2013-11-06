package com.mindalliance.channels.pages.components.community;

import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.ConfirmedAjaxFallbackLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;

/**
 * Plan community status panel.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/14/13
 * Time: 1:09 PM
 */
public class CollaborationPlanStatusPanel extends AbstractCommandablePanel {

    private WebMarkupContainer statusContainer;

    public CollaborationPlanStatusPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        statusContainer = new WebMarkupContainer( "statusContainer" );
        statusContainer.setOutputMarkupId( true );
        Label statusLabel = new Label(
                "status",
                getPlanCommunity().isClosed()
                        ? "This plan is closed to participation"
                        : "This plan is open for participation"
        );
        statusContainer.add( statusLabel );
        ConfirmedAjaxFallbackLink<String> toggleLink = new ConfirmedAjaxFallbackLink<String>(
                "toggle",
                getPlanCommunity().isClosed()
                        ? "Open this plan for participation?"
                        : "Close this plan to participation?"
        ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                toggleCollaborationPlanStatus();
                init();
                target.add( statusContainer );
            }
        };
        boolean canBeOpened = getPlanCommunity().canBeOpenedForParticipation( getCommunityService() );
        if ( getPlanCommunity().isClosed() ) {
            toggleLink.setEnabled( canBeOpened );
            if ( !canBeOpened ) {
                String whyNot = "The plan is not ready to be opened for participation";
                if ( getPlanCommunity().getLocale( getCommunityService() ) == null) {
                    whyNot += " because the plan's locale is not resolved to a specific location";
                }
                addTipTitle( toggleLink, whyNot );
            }
        }
        Label toggleToLabel = new Label(
                "toggleTo",
                !getPlanCommunity().isClosed()
                        ? "Close to participation"
                        : canBeOpened
                            ? "Open to participation"
                            : "(Can't be opened yet for participation)"
        );
        toggleLink.add( toggleToLabel );
        statusContainer.add( toggleLink );
        addOrReplace( statusContainer );

    }

    private void toggleCollaborationPlanStatus() {
        Command command = new UpdatePlanObject(
                getUser().getUsername(),
                getPlanCommunity(),
                "closed",
                !getPlanCommunity().isClosed()
        );
        command.makeUndoable( false );
        doCommand( command );
    }

}
