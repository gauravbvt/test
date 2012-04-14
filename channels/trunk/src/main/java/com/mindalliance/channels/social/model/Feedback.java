package com.mindalliance.channels.social.model;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.pages.Channels;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/14/12
 * Time: 9:38 AM
 */
@Entity
public class Feedback extends UserStatement implements Notifiable {

    public static final Feedback UNKNOWN = new Feedback( Channels.UNKNOWN_FEEDBACK_ID );
    public static final String GUIDELINES = "Guidelines";
    public static final String INFO_NEEDS = "Info needs";
    public static final String RFI = "RFI";
    public static final String PLANNING = "Planning";
    public static final String PARTICIPATING = "Participating";
    public static final String ISSUES = "Issues";

    public enum Type {
        QUESTION,
        PROBLEM,
        SUGGESTION
    }

    private String fromEmail;
    private Type type;
    @Column(length=1000)
    private String topic;
    private boolean urgent;
    private Date whenNotified;
    private boolean resolved;
    private boolean repliedTo;
    private Date lastReplied;

    @OneToMany( cascade=CascadeType.ALL, mappedBy="feedback" )
    private List<UserMessage> replies;

    public Feedback() {
    }

    public Feedback( long id ) {
        this.id = id; // only to be used for unknown feedback
    }

    public Feedback( String username, String planUri, int planVersion, Type type ) {
        super( planUri, planVersion, username );
        this.type = type;
    }

    public boolean isUnknown() {
        return this.equals( UNKNOWN );
    }

    public Type getType() {
        return type;
    }

    public void setType( Type type ) {
        this.type = type;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent( boolean urgent ) {
        this.urgent = urgent;
    }

    public String getTopic() {
        return topic == null ? "" : topic;
    }

    public void setTopic( String topic ) {
        this.topic = topic;
    }

    public Date getWhenNotified() {
        return whenNotified;
    }

    public void setWhenNotified( Date whenNotified ) {
        this.whenNotified = whenNotified;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail( String fromEmail ) {
        this.fromEmail = fromEmail;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved( boolean resolved ) {
        this.resolved = resolved;
    }


    public List<UserMessage> getReplies() {
        return replies;
    }

    public void setReplies( List<UserMessage> replies ) {
        this.replies = replies;
    }
    
    public void addReply( UserMessage reply ) {
        repliedTo = true;
        replies.add( reply );
    }

    public boolean isSuggestion() {
        return type == Type.SUGGESTION;
    }

    public boolean isProblem() {
        return type == Type.PROBLEM;
    }

    public boolean isQuestion() {
        return type == Type.QUESTION;
    }

    public boolean isRepliedTo() {
        return repliedTo;
    }

    public void setRepliedTo( boolean repliedTo ) {
        this.repliedTo = repliedTo;
    }
    
    public String getTypeLabel() {
        return type.name().toLowerCase();
    }
    
    public Date getLastReplied() {
        return lastReplied;
    }

    public void setLastReplied( Date lastReplied ) {
        this.lastReplied = lastReplied;
    }

    public String getUserFullName( ChannelsUserDao userDao ) {
        ChannelsUser user = userDao.getUserNamed( getUsername() );
        if ( user != null ) {
            String fullName = user.getFullName();
            return (fullName == null || fullName.isEmpty()) ? getUsername() : fullName;
        } else {
            return getUsername();
        }
    }


}
