package com.mindalliance.channels.model;

import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A transformation applied to elements of information.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 10, 2010
 * Time: 8:56:21 AM
 */
public class Transformation implements Serializable {
    /**
     * The type of transformation.
     */
    private Type type = Type.Identity;
    /**
     * The name of what is being transformed.
     */
    private List<Subject> subjects = new ArrayList<Subject>();

    public Transformation() {
    }

    public Transformation( Type type ) {
        this.type = type;
    }

    public Transformation( Type type, Subject subject ) {
        this.type = type;
        subjects.add( subject );
    }


    public Type getType() {
        return type;
    }

    public void setType( Type type ) {
        this.type = type;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects( List<Subject> subjects ) {
        this.subjects = subjects;
    }

    public boolean isNone() {
        return type == Type.Identity;
    }

    public void addSubject( Subject subject ) {
        if ( !subjects.contains( subject ) ) {
            subjects.add( subject );
        }
    }

    public Type getEffectiveType() {
        return getSubjects().isEmpty() ? Type.Identity : type;
    }

    public Subject newSubject() {
        return new Subject();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<Subject> iter = subjects.iterator();
        while ( iter.hasNext() ) {
            sb.append( iter.next() );
            if ( iter.hasNext() ) sb.append( "," );
        }
        sb.append( type.getSymbol() );
        return sb.toString();
    }

    /**
     * Whether transformation renames a given subject.
     *
     * @param subject a subject
     * @return a boolean
     */
    public boolean renames( Subject subject ) {
        return type == Type.Renaming && subjects.contains( subject );
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals( Object object ) {
        if ( object instanceof Transformation ) {
            Transformation other = (Transformation) object;
            return type.equals( other.getType() )
                    && CollectionUtils.isEqualCollection( subjects, other.getSubjects() );
        } else {
            return false;
        }
    }

    /**
     * {inheritDoc}
     */
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + type.hashCode();
        hash = hash * 31 + subjectsHashCode();
        return hash;
    }

    /**
     * Order-independent hashCode.
     * @return an int
     */
    private int subjectsHashCode() {
        int hash = 0;
        for ( Subject subject : subjects ) {
            hash += subject.hashCode();
        }
        return hash;
    }


    /**
     * A type of transformation.
     */
    public enum Type {
        /**
         * No transformation.
         */
        Identity,
        /**
         * The subjects are renamed (considered synonymous).
         */
        Renaming,
        /**
         * The subjects are aggregated.
         */
        Aggregation;

        public String getLabel() {
            switch ( this ) {
                case Identity:
                    return "none";
                case Renaming:
                    return "same as";
                case Aggregation:
                    return "aggregates";
                default:
                    return name();
            }
        }

        public static List<String> getAllLabels() {
            List<String> labels = new ArrayList<String>();
            for ( Type type : Type.values() ) {
                if ( type != Identity ) labels.add( type.getLabel() );
            }
            Collections.sort( labels );
            List<String> results = new ArrayList<String>();
            results.add( Identity.getLabel() );
            results.addAll( labels );
            return results;
        }

        public static Type valueOfLabel( String label ) {
            for ( Type type : Type.values() ) {
                if ( type.getLabel().equals( label ) ) return type;
            }
            return null;
        }

        /**
         * Combine transformation types.
         * Aggregation absorbs Renaming absorbs Identity.
         *
         * @param other a type
         * @return a type
         */
        public Type combineWith( Type other ) {
            return other.compareTo( this ) >= 0 ? other : this;
        }

        /**
         * Symbol representing the transformation type.
         *
         * @return a string
         */
        public String getSymbol() {
            String symbol;
            switch ( this ) {
                case Renaming:
                    symbol = "=";
                    break;
                case Aggregation:
                    symbol = "=>";
                    break;
                default:
                    symbol = "";
            }
            return symbol;
        }
    }

}
