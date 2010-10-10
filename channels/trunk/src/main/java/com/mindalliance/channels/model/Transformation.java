package com.mindalliance.channels.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
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
    private List<String> subjects = new ArrayList<String>();

    public Transformation() {
    }

    public Type getType() {
        return type;
    }

    public void setType( Type type ) {
        this.type = type;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects( List<String> subjects ) {
        this.subjects = subjects;
    }

    public boolean isNone() {
        return type == Type.Identity;
    }

    public void addSubject( String subject ) {
        if ( !subjects.contains( subject) ) {
            subjects.add( subject );
        }
    }

    public Type getEffectiveType() {
        return getSubjects().isEmpty() ? Type.Identity : type;
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
                case Identity: return "none";
                case Renaming: return "renames";
                case Aggregation: return "aggregates";
                default: return name();
            }
        }

        public static List<String> getAllLabels() {
            List<String> labels = new ArrayList<String>();
            for ( Type type : Type.values() ) {
                labels.add( type.getLabel() );
            }
            Collections.sort( labels );
            return labels;
        }

        public static Type valueOfLabel( String label ) {
            for ( Type type : Type.values() ) {
                if ( type.getLabel().equals( label ) ) return type;
            }
            return null;
        }


    }
}
