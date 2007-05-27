// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.user;

import java.util.List;

import com.mindalliance.channels.User;
import com.mindalliance.channels.data.elements.Element;
import com.mindalliance.channels.util.AbstractJavaBean;

/**
 * Conversation, possibly private, about some element.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Conversation extends AbstractJavaBean {

    private Element topic;

    /** Private conversations. */
    private List<User> guests;

    /** Top messages (not replies). */
    private List<Message> messages;

    /**
     * Default constructor.
     */
    public Conversation() {
        super();
    }

    /**
     * Return the guests.
     */
    public List<User> getGuests() {
        return guests;
    }

    /**
     * Set the guests.
     * @param guests the guests to set
     */
    public void setGuests( List<User> guests ) {
        this.guests = guests;
    }

    /**
     * Add a guest.
     * @param user the guest
     */
    public void addGuest( User user ) {
        guests.add( user );
    }

    /**
     * Remove a guest.
     * @param user the guest
     */
    public void removeGuest( User user ) {
        guests.remove( user );
    }

    /**
     * Return the messages.
     */
    public List<Message> getMessages() {
        return messages;
    }

    /**
     * Set the messages.
     * @param messages the messages to set
     */
    public void setMessages( List<Message> messages ) {
        this.messages = messages;
    }

    /**
     * Add a message.
     * @param message the message
     */
    public void addMessage( Message message ) {
        messages.add( message );
    }

    /**
     * Remove a message.
     * @param message the message
     */
    public void removeMessage( Message message ) {
        messages.remove( message );
    }

    /**
     * Return the topic.
     */
    public Element getTopic() {
        return topic;
    }

    /**
     * Set the topic.
     * @param topic the topic to set
     */
    public void setTopic( Element topic ) {
        this.topic = topic;
    }
}
