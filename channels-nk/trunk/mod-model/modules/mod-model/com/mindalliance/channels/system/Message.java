// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.system;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.support.GUID;

/**
 * A statement made in the context of a conversation, possibly in
 * reply to another message.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision$
 */
public class Message extends Statement {

    private List<Message> replies;
    private boolean retracted;

    /**
     * Default constructor.
     */
    public Message() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Message( GUID guid ) {
        super( guid );
    }

    /**
     * Return the replies to this message.
     */
    public synchronized List<Message> getReplies() {
        if ( replies == null )
            replies = new ArrayList<Message>();
        return replies;
    }

    /**
     * Set the replies.
     * @param replies the replies to set
     */
    public void setReplies( List<Message> replies ) {
        this.replies = replies;
    }

    /**
     * Add a reply.
     * @param message the message
     */
    public void addReply( Message message ) {
        getReplies().add( message );
    }

    /**
     * Remove a reply.
     * @param message the reply
     */
    public void removeReply( Message message ) {
        getReplies().remove( message );
    }

    /**
     * Tells if this statement was retracted.
     */
    public boolean isRetracted() {
        return retracted;
    }

    /**
     * Retract/unrectract this statement.
     * @param retracted the retracted state
     */
    public void setRetracted( boolean retracted ) {
        this.retracted = retracted;
    }

}
