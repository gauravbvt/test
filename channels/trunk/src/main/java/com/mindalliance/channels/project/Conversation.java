// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.project;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.Project;

/**
 * A sequence of messages from users about some data element.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 *
 * @opt attributes
 * @composed - - 1..* Message
 */
public class Conversation extends AbstractProjectObject {

    private Object about;
    private List<Message> messages = new ArrayList<Message>();

    /**
     * Default constructor.
     */
    public Conversation() {
        super();
    }

    /**
     * Default constructor.
     * @param project the project
     */
    public Conversation( Project project ) {
        super( project );
    }

    /**
     * Return the value of about.
     */
    public Object getAbout() {
        return this.about;
    }

    /**
     * Set the value of about.
     * @param about The new value of about
     */
    public void setAbout( Object about ) {
        this.about = about;
    }

    /**
     * Return the value of messages.
     */
    public List<Message> getMessages() {
        return this.messages;
    }

    /**
     * Set the value of messages.
     * @param messages The new value of messages
     */
    public void setMessages( List<Message> messages ) {
        this.messages = messages;
    }

    /**
     * Add a message to this conversation.
     * @param message the message
     */
    public void addMessage( Message message ) {
        this.messages.add( message );
    }

    /**
     * Remove a message from this conversation.
     * @param message the message
     */
    public void removeMessage( Message message ) {
        this.messages.remove( message );
    }
}
