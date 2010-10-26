package com.mindalliance.channels.model;

import com.mindalliance.channels.nlp.Matcher;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.text.Collator;

/**
 * The subject of a transformation.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 10, 2010
 * Time: 3:32:57 PM
 */
public class Subject implements Serializable, Comparable {
    /**
     * Information name.
     */
    private String info = "";
    /**
     * EOI name,
     */
    private String content = "";
    /**
     * Maximum length of info in label.
     */
    private static final int MAX_INFO_LENGTH_IN_LABEL = 20;

    public Subject() {
    }

    public Subject( String info, String content ) {
        this.info = info;
        this.content = content;
    }

    public String getInfo() {
        return info;
    }

    public String getContent() {
        return content;
    }

    public void setInfo( String info ) {
        this.info = info;
    }

    public void setContent( String content ) {
        this.content = content;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals( Object object ) {
        Matcher matcher = Matcher.getInstance();
        if ( object instanceof Subject ) {
            Subject other = (Subject) object;
            return matcher.same( info, other.getInfo() )
                    && matcher.same( content, other.getContent() );
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        Matcher matcher = Matcher.getInstance();
        int hash = 1;
        hash = hash * 31 + matcher.makeCanonical( info ).hashCode();
        hash = hash * 31 + matcher.makeCanonical( content ).hashCode();
        return hash;

    }

    public String getLabel( int maxInfoLength ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "\"");
        sb.append( content );
        sb.append( "\" in \"" );
        sb.append( StringUtils.abbreviate( info, maxInfoLength ) );
        sb.append( "\"" );
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return getLabel( Integer.MAX_VALUE );
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo( Object other ) {
        return Collator.getInstance().compare(
                getLabel( Integer.MAX_VALUE ),
                ( (Subject) other ).getLabel( Integer.MAX_VALUE ) );
    }
}

