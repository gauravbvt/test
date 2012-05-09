package com.mindalliance.channels.social.model;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.social.services.notification.Messageable;
import org.apache.commons.lang.StringUtils;

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
public class UserMessage extends UserStatement implements Messageable {

    private String toUsername;
    private boolean sendNotification;
    private Date whenNotificationSent;

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

    public boolean isSendNotification() {
        return sendNotification;
    }

    public void setSendNotification( boolean sendNotification ) {
        this.sendNotification = sendNotification;
    }

    public Date getWhenNotificationSent() {
        return whenNotificationSent;
    }

    public void setWhenNotificationSent( Date whenNotificationSent ) {
        this.whenNotificationSent = whenNotificationSent;
    }

    public boolean isBroadcast( ChannelsUser currentUser ) {
        return toUsername == null // legacy - all planners
                || toUsername.equals( ChannelsUserInfo.PLANNERS ) && currentUser.isPlanner()
                || toUsername.equals( ChannelsUserInfo.USERS );
    }

    public boolean isNotificationSent() {
        return sendNotification && whenNotificationSent != null;
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

    // Messageable

    @Override
    public String getContent( Format format, int maxLength ) {
        // Ignore TEXT vs HTML for now
        Date now = new Date();
        StringBuilder sb = new StringBuilder();
        String aboutString = getMoLabel();
        if ( !aboutString.isEmpty() )
            sb.append( "About " ).append( aboutString ).append( "\n\n" );

        sb.append( getText() );
        Feedback feedback = getFeedback();
        if ( feedback != null ) {
            sb.append( "\n\n -- In response to the " )
                    .append( feedback.getTypeLabel() )
                    .append( " you sent on " );
            sb.append( DATE_FORMAT.format( feedback.getCreated() ) );
            sb.append( ":\n\n" );
            sb.append( feedback.getText() );
            sb.append( "\n\n ---------------- " );
        }
        sb.append( "\n\n -- Message first sent in Channels " )
                .append( ChannelsUtils.getLongTimeIntervalString( now.getTime() - getCreated().getTime() ) )
                .append( " ago --" );
        return StringUtils.abbreviate( sb.toString(), maxLength );
    }

    @Override
    public String getSubject( Format format, int maxLength ) {
        return StringUtils.abbreviate( "[" + getPlanUri() + "] " + getText(), maxLength );
    }

    @Override
    public Date getWhenNotified() {
        return getWhenNotificationSent();
    }

    public boolean isToAllPlanners() {
        return toUsername.equals( ChannelsUserInfo.PLANNERS );
    }

    public boolean isToAllUsers() {
        return toUsername.equals( ChannelsUserInfo.USERS );
    }

}
