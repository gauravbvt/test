package com.mindalliance.channels.core.community.rfi;

import com.mindalliance.channels.core.orm.model.AbstractPersistentPlanObject;
import org.apache.commons.validator.UrlValidator;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
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
public class Answer extends AbstractPersistentPlanObject {

    @ManyToOne( cascade = CascadeType.ALL )
    private AnswerSet answerSet;

    private String text = "";

    private int sequence = 0;

    private static final String YES = "yes";

    private static final String NO = "no";


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
        text = s;
    }
    
    public void setYesOrNo( String s ) {
        text = s.trim().toLowerCase().equals( YES ) ? YES : NO;
    }
    
    public boolean isYes() {
        return text.equals( YES );
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
            return text != null && !text .isEmpty() ? new URL( text ) : null;
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
}
