package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.Matcher;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.text.Collator;

/**
 * The subject of a transformation.
 */
public class Subject implements Serializable, Comparable<Subject> {

    /**
     * Information name.
     */
    private String info = "";

    /**
     * EOI name,
     */
    private String content = "";

    /**
     * "Root" subject from which dissemination is traced.
     */
    private boolean root;

    public Subject() {
    }

    public Subject( Subject subject ) {
        this( subject.getInfo(), subject.getContent() );
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

    public boolean isRoot() {
        return root;
    }

    public void setRoot( boolean root ) {
        this.root = root;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( obj instanceof Subject ) {
            Subject other = (Subject) obj;
            return Matcher.same( info, other.getInfo() ) && Matcher.same( content, other.getContent() );
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + Matcher.makeCanonical( info ).hashCode();
        hash = hash * 31 + Matcher.makeCanonical( content ).hashCode();
        return hash;
    }

    public String getLabel( int maxInfoLength ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "\"" );
        sb.append( content.isEmpty() ? "?" : content );
        sb.append( "\" in \"" );
        sb.append( StringUtils.abbreviate( info, maxInfoLength ) );
        sb.append( "\"" );
        return sb.toString();
    }

    @Override
    public String toString() {
        return getLabel( Integer.MAX_VALUE );
    }

    @Override
    public int compareTo( Subject o ) {
        return Collator.getInstance().compare( getLabel( Integer.MAX_VALUE ), o.getLabel( Integer.MAX_VALUE ) );
    }
}

