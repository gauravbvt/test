package com.mindalliance.channels.db.data.surveys;

import com.mindalliance.channels.db.data.AbstractChannelsDocument;
import org.apache.commons.validator.UrlValidator;
import org.springframework.data.mongodb.core.mapping.Document;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * An (alternate) answer given by a user to a survey question.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/30/13
 * Time: 12:34 PM
 */
@Document( collection = "surveys" )
public class Answer extends AbstractChannelsDocument {

    private String text = "";

    private int sequence = 0;

    public static final String YES = "yes";

    public static final String NO = "no";

    private boolean removed;

    private String answerSetUid;

    public Answer() {
    }

    public Answer( AnswerSet answerSet ) {
        super( answerSet.getCommunityUri(),
                answerSet.getPlanUri(),
                answerSet.getPlanVersion(),
                answerSet.getUsername() );
        answerSetUid = answerSet.getUid();
    }

    public String getAnswerSetUid() {
        return answerSetUid;
    }

    public void setAnswerSetUid( String answerSetUid ) {
        this.answerSetUid = answerSetUid;
    }

    public String getText() {
        return text;
    }

    public void setText( String s ) {
        text = s;
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
