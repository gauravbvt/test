package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.orm.model.AbstractPersistentPlanObject;

import javax.persistence.Entity;
import java.util.Date;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/14/12
 * Time: 9:38 AM
 */
@Entity
public class Feedback extends AbstractPersistentPlanObject implements Notifiable {

    public enum Type {
        QUESTION,
        PROBLEM,
        SUGGESTION
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

    public Feedback() {
    }

    public Feedback( String username, String planUri, Type type ) {
        super( planUri, username );
        this.type = type;
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
}
