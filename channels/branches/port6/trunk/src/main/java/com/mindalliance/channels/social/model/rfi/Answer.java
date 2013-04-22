package com.mindalliance.channels.social.model.rfi;

import com.mindalliance.channels.core.orm.model.AbstractPersistentChannelsObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.UrlValidator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/16/12
 * Time: 3:19 PM
 */
@Entity
public class Answer extends AbstractPersistentChannelsObject {

    @ManyToOne
    private AnswerSet answerSet;

    @Column( length = 10000 )
    private String text = "";

    private int sequence = 0;

    public static final String YES = "yes";

    public static final String NO = "no";

    @Transient
    private boolean removed;

    public Answer() {
    }

    public Answer( AnswerSet answerSet ) {
        super( answerSet.getCommunityUri(), answerSet.getPlanUri(),answerSet.getPlanVersion(), answerSet.getUsername() );
        this.answerSet = answerSet;
    }

    public AnswerSet getAnswerSet() {
        return answerSet;
    }

    public void setAnswerSet( AnswerSet answerSet ) {
        this.answerSet = answerSet;
    }

    public String getText() {
        return text;
    }

    public void setText( String s ) {
        text = StringUtils.abbreviate( s, 10000 );
    }

    public void setYes() {
        text = YES;
    }

    public void setNo() {
        text = NO;
    }


    public boolean isYes() {
        return text.equals( YES );
    }

    public boolean isGiven() {
        return !getText().isEmpty();
    }

    public void setUrl( String s ) {
        if ( new UrlValidator().isValid( s ) ) {
            text = s;
        } else {
            text = "";
        }
    }

    public URL getUrl() {
        try {
            return text != null && !text.isEmpty() ? new URL( text ) : null;
        } catch ( MalformedURLException e ) {
            return null;
        }
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence( int sequence ) {
        this.sequence = sequence;
    }

    public void reset() {
        text = "";
    }

    public void remove() {
        removed = true;
    }

    public boolean wasRemoved() {
        return removed;
    }
}
