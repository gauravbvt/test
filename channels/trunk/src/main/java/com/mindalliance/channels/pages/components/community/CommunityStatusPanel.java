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
public class CommunityStatusPanel extends AbstractCommandablePanel {

    private WebMarkupContainer statusContainer;

    public CommunityStatusPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        statusContainer = new WebMarkupContainer( "statusContainer" );
        statusContainer.setOutputMarkupId( true );
        Label statusLabel = new Label(
                "status",
                getPlanCommunity().isClosed()
                        ? "This community is closed to participation"
                        : "This community is open for participation"
        );
        statusContainer.add( statusLabel );
        ConfirmedAjaxFallbackLink<String> toggleLink = new ConfirmedAjaxFallbackLink<String>(
                "toggle",
                getPlanCommunity().isClosed()
                        ? "Open this community for participation?"
                        : "Close this community to participation?"
        ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                toggleCommunityStatus();
                init();
                target.add( statusContainer );
            }
        };
        Label toggleToLabel = new Label(
                "toggleTo",
                getPlanCommunity().isClosed()
                        ? "Open to participation"
                        : "Close to participation"
        );
        toggleLink.add( toggleToLabel );
        statusContainer.add( toggleLink );
        addOrReplace( statusContainer );

    }

    private void toggleCommunityStatus() {
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
