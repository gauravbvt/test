package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.social.model.UserMessage;
import org.apache.wicket.model.IModel;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/29/12
 * Time: 1:25 PM
 */
public class UserMessagePanel extends UserStatementPanel {

    private boolean showReceived;

    public UserMessagePanel(
            String id,
            IModel<UserMessage> userMessageModel,
            boolean showReceived,
            int index,
            boolean showProfile,
            Updatable updatable ) {
        super( id, userMessageModel, index, showProfile, updatable );
        this.showReceived = showReceived;
    }

    protected String getPersistentPlanObjectUsername() {
        return showReceived
                ? getUserMessage().getFromUsername()
                : getUserMessage().getToUsername();
    }

    protected String getCssClasses() {
        String cssClasses = getUserMessage().isBroadcast( getUser() )
                ? "broadcast "
                : "private ";
        return cssClasses + super.getCssClasses();
    }

    protected String getTimeLabel() {
        return ( getUserMessage().isNotificationSent() ? "emailed " : "" )
                + getTime();
    }

    private UserMessage getUserMessage() {
        return (UserMessage)getUserStatement();
    }


    public String getUserFullNameAndRole() {
        return getInvolvement( showReceived ) + super.getUserFullNameAndRole();
    }

    private static String getInvolvement( boolean showReceived ) {
        return showReceived ? "From " : "To ";
    }


}
