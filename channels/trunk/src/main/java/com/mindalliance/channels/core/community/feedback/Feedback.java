package com.mindalliance.channels.core.community.feedback;

import com.mindalliance.channels.core.community.notification.Notifiable;
import com.mindalliance.channels.core.orm.model.AbstractPersistentPlanObject;
import com.mindalliance.channels.pages.Channels;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
public class Feedback extends AbstractPersistentPlanObject implements Notifiable {

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
        SUGGESTION,
        REPLY
    }

    private String fromEmail;
    private Type type;
    private String topic;
    /**
     * A modelObjectRef string.
     */
    private String about;
    private String content;
    private boolean urgent;
    private Date whenNotified;
    private boolean resolved;
    private boolean repliedTo;
    private Date lastReplied;

    @ManyToOne( cascade = CascadeType.ALL )
    @JoinColumn( name = "replyTo_id" )
    private Feedback replyTo;

    @OneToMany( mappedBy = "replyTo" )
    private List<Feedback> replies;

    public Feedback() {
    }

    public Feedback( long id ) {
        this.id = id; // only to be used for unknown feedback
    }

    public Feedback( String username, String planUri, Type type ) {
        super( planUri, username );
        this.type = type;
    }
    
    public Feedback( String username, String planUri, Feedback feedback ) {
        this( username, planUri, Type.REPLY );
        replyTo = feedback;
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

    public String getContent() {
        return content == null ? "" : content;
    }

    public void setContent( String content ) {
        this.content = content;
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

    public String getAbout() {
        return about;
    }

    public void setAbout( String about ) {
        this.about = about;
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

    public Feedback getReplyTo() {
        return replyTo;
    }

    public void setReplyTo( Feedback replyTo ) {
        this.replyTo = replyTo;
    }

    public List<Feedback> getReplies() {
        return replies;
    }

    public void setReplies( List<Feedback> replies ) {
        this.replies = replies;
    }
    
    public void addReply( Feedback reply ) {
        repliedTo = true;
        replies.add( reply );
    }

    public boolean isReply() {
        return type == Type.REPLY;
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
    
    public String getUrgentLabel() {
        return urgent ? "yes" : "no";
    }
    
    public String getTypeLabel() {
        return type.name().toLowerCase();
    }
    
    public  String getResolvedLabel() {
        return resolved ? "yes" : "no";
    }

    public Date getLastReplied() {
        return lastReplied;
    }

    public void setLastReplied( Date lastReplied ) {
        this.lastReplied = lastReplied;
    }
}
