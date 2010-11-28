package com.mindalliance.channels.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Information classification.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 2, 2009
 * Time: 1:31:11 PM
 */
public class Classification implements Identifiable, Comparable {
    /**
     * Classification system.
     * Set once.
     */
    private String system = "";
    /**
     * Classification name.
     * Set once.
     */
    private String name = "";
    /**
     * Classification level.
     * The lower the number the more classified.
     */
    private int level = 0;

    public Classification() {
    }

    public String getSystem() {
        return system;
    }

    public void setSystem( String system ) {
        assert this.system.isEmpty();
        this.system = system;
    }

    public long getId() {
        return hashCode();
    }

    public String getDescription() {
        return toString();
    }

    public String getName() {
        return name;
    }

    public String getTypeName() {
        return "classification";
    }

    public void setName( String name ) {
        assert this.name.isEmpty();
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel( int level ) {
        this.level = level;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return system + "/" + name + "(" + level + ")";
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals( Object object ) {
        if ( object instanceof Classification ) {
            Classification other = (Classification) object;
            return
                    system.equals( other.getSystem() )
                            && name.equals( other.getName() );
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + system.hashCode();
        hash = hash * 31 + name.hashCode();
        return hash;

    }

    /**
     * Whether a classifcation is higher than or equal to another.
     *
     * @param other a classification
     * @return a boolean
     */
    public boolean encompasses( Classification other ) {
        return system.equals( other.getSystem() )
                && level <= other.getLevel();
    }

    /**
     * Whether a classifcation is higher than another.
     *
     * @param other a classification
     * @return a boolean
     */
    public boolean isHigherThan( Classification other ) {
        return system.equals( other.getSystem() )
                && level < other.getLevel();
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo( Object object ) {
        Classification other = (Classification) object;
        assert system.equals( other.getSystem() );
        return level < other.getLevel()
                ? -1
                : ( level > other.getLevel() )
                ? 1
                : 0;
    }

    /**
     * Label.
     *
     * @return a string
     */
    public String getLabel() {
        return system + "/" + name;
    }

    /**
     * Is this classification encompassed by any in a given list?
     *
     * @param classifications a list of classifications
     * @return a boolean
     */
    public boolean impliedBy( List<Classification> classifications ) {
        return CollectionUtils.exists(
                classifications,
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return ( (Classification) obj ).encompasses( Classification.this );
                    }
                }
        );
    }

    /**
     * Whether at least one in a list of classifications is stronger than a classification.
     *
     * @param classifications a list of classifications
     * @param classification  a classification
     * @return a boolean
     */
    public static boolean encompass(
            final List<Classification> classifications,
            final Classification classification ) {
        List<Classification> others = new ArrayList<Classification>();
        others.add( classification );
        return encompass( classifications, others );
    }


    /**
     * Whether at least one in a list of classifications is stronger than another in another list.
     *
     * @param classifications a list of classifications
     * @param others          a list of classifications
     * @return a boolean
     */
    public static boolean hasHigherClassification(
            final List<Classification> classifications,
            final List<Classification> others ) {
        if ( classifications.isEmpty() && others.isEmpty() )
            // equal
            return false;
        if ( others.isEmpty() )
            // higher
            return true;
        // At least one of the classifications is higher than one of the others.
        for ( Classification other : others ) {
            for ( Classification classification : classifications ) {
                if ( classification.isHigherThan( other ) ) return true;
            }
        }
        return false;
    }

    /**
     * Whether classifications encompass others.
     *
     * @param classifications a list of classifications
     * @param others          a list of classifications
     * @return a boolean
     */
    public static boolean encompass(
            final List<Classification> classifications,
            final List<Classification> others ) {
        if ( classifications.isEmpty() && others.isEmpty() )
            // equal
            return true;
        // For each other, there is a higher or equal classification 
        // and none in the others is higher than any of the classifications.
        for ( final Classification other : others ) {
            if ( !CollectionUtils.exists(
                    classifications,
                    new Predicate() {
                        public boolean evaluate( Object object ) {
                            Classification c = (Classification) object;
                            return c.encompasses( other );
                        }
                    }
            ) ||
                    CollectionUtils.exists(
                            classifications,
                            new Predicate() {
                                public boolean evaluate( Object object ) {
                                    Classification c = (Classification) object;
                                    return other.isHigherThan( c );
                                }
                            }
                    ) ) return false;
        }
        return true;
    }


}
