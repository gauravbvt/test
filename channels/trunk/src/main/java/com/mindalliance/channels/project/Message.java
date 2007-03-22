// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.project;

import java.sql.Timestamp;

import com.mindalliance.channels.Project;
import com.mindalliance.channels.User;

/**
 * A comment made by a user in a conversation.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 *
 * @opt attributes
 * @navassoc - - 1 User
 * @navassoc * replyTo 0..1 Message
 */
public class Message extends AbstractProjectObject {

    private User from;
    private Timestamp when;
    private String content;
    private Message replyTo;

    /**
     * Default constructor.
     */
    public Message() {
        super();
    }

    /**
     * Default constructor.
     * @param project the project
     */
    public Message( Project project ) {
        super( project );
    }

    /**
     * Return the value of content.
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Set the value of content.
     * @param content The new value of content
     */
    public void setContent( String content ) {
        this.content = content;
    }

    /**
     * Return the value of from.
     */
    public User getFrom() {
        return this.from;
    }

    /**
     * Set the value of from.
     * @param from The new value of from
     */
    public void setFrom( User from ) {
        this.from = from;
    }

    /**
     * Return the value of replyTo.
     */
    public Message getReplyTo() {
        return this.replyTo;
    }

    /**
     * Set the value of replyTo.
     * @param replyTo The new value of replyTo
     */
    public void setReplyTo( Message replyTo ) {
        this.replyTo = replyTo;
    }

    /**
     * Return the value of when.
     */
    public Timestamp getWhen() {
        return this.when;
    }

    /**
     * Set the value of when.
     * @param when The new value of when
     */
    public void setWhen( Timestamp when ) {
        this.when = when;
    }
}
