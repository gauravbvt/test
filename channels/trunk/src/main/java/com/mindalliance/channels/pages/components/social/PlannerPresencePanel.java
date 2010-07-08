package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.social.PresenceEvent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;

/**
 * Planner presence panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 5, 2010
 * Time: 2:28:30 PM
 */
public class PlannerPresencePanel extends AbstractSocialEventPanel {

    private PresenceEvent latestPresenceEvent = null;

    public PlannerPresencePanel( String id, String username, Updatable updatable ) {
        super( id, username, updatable );
        init();
    }

    protected void moreInit( WebMarkupContainer socialItemContainer ) {
        addStatus( socialItemContainer );
    }

    protected String getCssClass() {
        PresenceEvent presenceEvent = getLatestPresenceEvent();
        return presenceEvent != null && presenceEvent.isLogin()
                ? "joining"
                : "leaving";
    }

    public String getTime() {
        PresenceEvent presenceEvent = getLatestPresenceEvent();
        return presenceEvent == null ? "" : presenceEvent.getShortTimeElapsedString();
    }

    public String getLongTime() {
         PresenceEvent presenceEvent = getLatestPresenceEvent();
         return presenceEvent == null ? "" : presenceEvent.getLongTimeElapsedString();
     }

    private void addStatus( WebMarkupContainer socialItemContainer ) {
        Label statusLabel = new Label( "status", new PropertyModel<String>( this, "status" ) );
        socialItemContainer.add( statusLabel );
    }

    public String getStatus() {
        PresenceEvent presenceEvent = getLatestPresenceEvent();
        if ( presenceEvent == null ) {
            return " Is not here";
        } else if ( presenceEvent.isLogin() ) {
            return " Is here ";
        } else {
            return " Has left ";
        }
    }

    private PresenceEvent getLatestPresenceEvent() {
        if ( latestPresenceEvent == null ) {
            latestPresenceEvent = getPlanningEventService().findLatestPresence( getUsername() );
        }
        return latestPresenceEvent;
    }

}
