package com.mindalliance.channels.db.data.messages;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.db.data.users.UserRecord;
import org.springframework.data.mongodb.core.mapping.Document;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/25/13
 * Time: 3:16 PM
 */
@Document( collection = "messages" )
public class UserMessage extends UserStatement {

    private String toUsername;
    private boolean sendNotification;
    private Date whenNotificationSent;
    private boolean read;
    private String feedbackId;


    public UserMessage() {}

    public UserMessage( String username, String text, PlanCommunity planCommunity ) {
        super( username, text, planCommunity );
    }

    public UserMessage( String username, String text, ModelObject modelObject, PlanCommunity planCommunity ) {
        super( username, text, modelObject, planCommunity );
    }

    @Override
    public String getToUsername( String topic ) {
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

    public boolean isBroadcast( ChannelsUser currentUser, PlanCommunity planCommunity ) {
        return toUsername == null // legacy - all planners
                || toUsername.equals( UserRecord.PLANNERS ) && currentUser.isPlannerOrAdmin( planCommunity.getPlanUri() )
                || toUsername.equals( UserRecord.USERS );
    }

    public boolean isNotificationSent() {
        return sendNotification && whenNotificationSent != null;
    }

    public String getFromUsername() {
        return getUsername();
    }

    public boolean isToAllPlanners() {
        return toUsername.equals( UserRecord.PLANNERS );
    }

    public boolean isToAllUsers() {
        return toUsername.equals( UserRecord.USERS );
    }

    public boolean isRead() {
        return read;
    }

    public void setRead( boolean read ) {
        this.read = read;
    }

    public String getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId( String feedbackId ) {
        this.feedbackId = feedbackId;
    }

    public boolean isAwaitingNotification() {
        return sendNotification && whenNotificationSent == null;
    }

    // Messageable


    public String getTextContent( Format format, CommunityService communityService ) {
        // Ignore TEXT vs HTML for now
        Date now = new Date();
        StringBuilder sb = new StringBuilder();
        String aboutString = getMoLabel();
        if ( !aboutString.isEmpty() )
            sb.append( "About " ).append( aboutString ).append( "\n\n" );

        sb.append( getText() );
        Feedback feedback = getFeedback( communityService );
        if ( feedback != null ) {
            sb.append( "\n\n -- In response to the " )
                    .append( feedback.getTypeLabel() )
                    .append( " you sent on " );
            sb.append( new SimpleDateFormat( DATE_FORMAT_STRING ).format( feedback.getCreated() ) );
            sb.append( ":\n\n" );
            sb.append( feedback.getText() );
            sb.append( "\n\n ---------------- " );
        }
        sb.append( "\n\n -- Message first sent in Channels " )
                .append( ChannelsUtils.getLongTimeIntervalString( now.getTime() - getCreated().getTime() ) )
                .append( " ago --" );
        return sb.toString();
    }

    private Feedback getFeedback( CommunityService communityService ) {
        return null;  //Todo - get feedback from db given feedbackId
    }

    public String getTextSubject( Format format, CommunityService communityService ) {
        return "[" + getPlanUri() + "] " + getText();
    }

}
