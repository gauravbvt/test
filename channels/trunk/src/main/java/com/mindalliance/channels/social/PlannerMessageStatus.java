package com.mindalliance.channels.social;

/**
 * Message status.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 3, 2010
 * Time: 8:09:22 PM
 */
public class PlannerMessageStatus extends PersistentObject {

    private boolean read;
    private String username;
    private String messageId;

    public PlannerMessageStatus() {
    }

    public PlannerMessageStatus( String messageId, String username ) {
        super();
        this.messageId = messageId;
        this.username = username;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead( boolean read ) {
        this.read = read;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername( String username ) {
        this.username = username;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId( String messageId ) {
        this.messageId = messageId;
    }
}
