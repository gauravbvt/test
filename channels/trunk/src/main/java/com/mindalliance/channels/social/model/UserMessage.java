package com.mindalliance.channels.social.model;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.social.services.UserMessageService;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * A message from a user to one or multiple users.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/2/12
 * Time: 11:28 AM
 */
@Entity
public class UserMessage extends UserStatement {

    private String toUsername;
    private boolean emailIt;
    private Date whenEmailed;

    @ManyToOne
    private Feedback feedback;
    
    public UserMessage() {} 

    public UserMessage( String planUri, int planVersion, String username, String text ) {
        super( planUri, planVersion, username, text);
    }

    public UserMessage( String planUri, int planVersion, String username, String text, ModelObject modelObject ) {
        super( planUri, planVersion, username, text, modelObject );
    }


    public String getToUsername() {
        return toUsername;
    }

    public void setToUsername( String toUsername ) {
        this.toUsername = toUsername;
    }

    public boolean isEmailIt() {
        return emailIt;
    }

    public void setEmailIt( boolean emailIt ) {
        this.emailIt = emailIt;
    }

    public Date getWhenEmailed() {
        return whenEmailed;
    }

    public void setWhenEmailed( Date whenEmailed ) {
        this.whenEmailed = whenEmailed;
    }

    public boolean isBroadcast( ChannelsUser currentUser ) {
        return toUsername == null // legacy - all planners
                || toUsername.equals( UserMessageService.PLANNERS ) && currentUser.isPlanner()
                || toUsername.equals( UserMessageService.USERS );
    }

    public boolean isEmailed() {
        return emailIt && whenEmailed != null;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback( Feedback feedback ) {
        this.feedback = feedback;
    }

    public String getFromUsername() {
        return getUsername();
    }
}
