/*
 * Created on May 3, 2007
 */
package com.mindalliance.channels.data.user;

import java.util.List;

/**
 * A statement made in the context of a conversation, possibly in
 * reply to another message
 * 
 * @author jf
 */
public class Message extends Statement {

    private List<Message> replies; // can be null
    private boolean retracted = false; // true if issuer retracted it

    public Message() {
        super();
    }

    /**
     * @return the replies
     */
    public List<Message> getReplies() {
        return replies;
    }

    /**
     * @param replies the replies to set
     */
    public void setReplies( List<Message> replies ) {
        this.replies = replies;
    }

    /**
     * @param message
     */
    public void addReply( Message message ) {
        replies.add( message );
    }

    /**
     * @param message
     */
    public void removeReply( Message message ) {
        replies.remove( message );
    }

    /**
     * @return the retracted
     */
    public boolean isRetracted() {
        return retracted;
    }

    /**
     * @param retracted the retracted to set
     */
    public void setRetracted( boolean retracted ) {
        this.retracted = retracted;
    }

}
