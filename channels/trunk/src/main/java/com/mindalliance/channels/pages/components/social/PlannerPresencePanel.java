package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.social.PresenceEvent;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
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
        addIcon( socialItemContainer );
        addTime( socialItemContainer );
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

    private void addIcon( WebMarkupContainer socialItemContainer ) {
        WebMarkupContainer icon = new WebMarkupContainer( "icon" );
        icon.setVisible( isPresent() );
        socialItemContainer.add( icon );
    }

    private void addTime( WebMarkupContainer socialItemContainer ) {
        boolean present = isPresent();
        String time = getTime();
        String timeLabelString = "";
        if ( !time.isEmpty() && present ) {
            timeLabelString = time;
        }
        Label timeLabel = new Label( "time", new Model<String>( timeLabelString ) );
        if ( !timeLabelString.isEmpty() ) {
            timeLabel.add( new AttributeModifier(
                    "title",
                    true,
                    new PropertyModel<String>( this, "longTime" ) ) );
        }
        timeLabel.setVisible( isPresent() );
        socialItemContainer.add( timeLabel );
        if ( !present & !time.isEmpty() ) {
            getNameLabel().add( new AttributeModifier(
                    "title",
                    true,
                    new Model<String>( "left " + time ) ) );
        }
    }

    public boolean isPresent() {
        PresenceEvent presenceEvent = getLatestPresenceEvent();
        return presenceEvent != null && presenceEvent.isLogin();
    }

    private PresenceEvent getLatestPresenceEvent() {
        if ( latestPresenceEvent == null ) {
            latestPresenceEvent = getPlanningEventService().findLatestPresence( getUsername() );
        }
        return latestPresenceEvent;
    }

}
