package com.mindalliance.channels.core.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class Classification implements Identifiable, Comparable<Classification> {

    /** Logger. */
    private static final Logger LOG = LoggerFactory.getLogger( Classification.class );


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

    @Override
    public long getId() {
        return hashCode();
    }

    @Override
    public String getDescription() {
        return toString();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTypeName() {
        return "classification";
    }

    @Override
    public boolean isModifiableInProduction() {
        return false;
    }

    @Override
    public String getClassLabel() {
        return getClass().getSimpleName();
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

    public String toString() {
        return system + '/' + name;
    }

    private int getEffectiveLevel( CollaborationModel collaborationModel ) {
        Classification referenceClassification = collaborationModel.getClassification( system, name );
        if ( referenceClassification == null ) {
            LOG.warn( "Can't reference classification {}", this );
            return Integer.MIN_VALUE;
        } else
            return referenceClassification.getLevel();
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;

        if ( obj != null && getClass() == obj.getClass() ) {
            Classification that = (Classification) obj;
            return name.equals( that.getName() ) && system.equals( that.getSystem() );
        }

        return false;
    }

    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + system.hashCode();
        hash = hash * 31 + name.hashCode();
        return hash;

    }

    /**
     * Whether a classification is higher than or equal to another.
     *
     * @param other a classification
     * @param collaborationModel the containing plan
     * @return a boolean
     */
    public boolean encompasses( Classification other, CollaborationModel collaborationModel ) {
        return system.equals( other.getSystem() )
            && getEffectiveLevel( collaborationModel ) <= other.getEffectiveLevel( collaborationModel );
    }

    /**
     * Whether a classifcation is higher than another.
     *
     * @param other a classification
     * @param collaborationModel the context
     * @return a boolean
     */
    public boolean isHigherThan( Classification other, CollaborationModel collaborationModel ) {
        return system.equals( other.getSystem() )
            && getEffectiveLevel( collaborationModel ) < other.getEffectiveLevel( collaborationModel );
    }

    @Override
    public int compareTo( Classification object ) {
        assert system.equals( object.getSystem() );
        return level < object.getLevel() ? -1
                                         : level > object.getLevel() ? 1 : 0;
    }

    /**
     * Label.
     *
     * @return a string
     */
    public String getLabel() {
        return system + '/' + name;
    }

    @Override
    public String getKindLabel() {
        return getTypeName();
    }

    @Override
    public String getUid() {
        return Long.toString( getId() );
    }


    /**
     * Whether at least one in a list of classifications is stronger than a classification.
     *
     * @param classifications a list of classifications
     * @param classification  a classification
     * @param collaborationModel the containing plan
     * @return a boolean
     */
    public static boolean encompass( List<Classification> classifications, Classification classification, CollaborationModel collaborationModel ) {
        List<Classification> others = new ArrayList<Classification>();
        others.add( classification );
        return encompass( classifications, others, collaborationModel );
    }

    /**
     * Whether at least one in a list of classifications is stronger than another in another list.
     *
     * @param classifications a list of classifications
     * @param others          a list of classifications
     * @param collaborationModel the context
     * @return a boolean                                                                                       1
     */
    public static boolean hasHigherClassification(
            List<Classification> classifications, List<Classification> others, CollaborationModel collaborationModel ) {
        if ( classifications.isEmpty() && others.isEmpty() )
            // equal
            return false;

        if ( others.isEmpty() )
            // higher
            return true;

        // At least one of the classifications is higher than one of the others.
        for ( Classification other : others ) {
            for ( Classification classification : classifications ) {
                if ( classification.isHigherThan( other, collaborationModel ) )
                    return true;
            }
        }

        return false;
    }

    /**
     * Whether classifications encompass others.
     *
     * @param classifications a list of classifications
     * @param others          a list of classifications
     * @param collaborationModel the containing plan
     * @return a boolean
     */
    public static boolean encompass(
            List<Classification> classifications, List<Classification> others, final CollaborationModel collaborationModel ) {

        if ( classifications.isEmpty() && others.isEmpty() )
            // equal
            return true;

        // For each other, there is a higher or equal classification
        // and none in the others is higher than any of the classifications.
        for ( final Classification other : others ) {
            if ( !CollectionUtils.exists( classifications, new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return ((Classification) object).encompasses( other, collaborationModel );
                            }
                        } )
                 || CollectionUtils.exists( classifications, new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return other.isHigherThan( (Classification) object, collaborationModel );
                            }
                        } ) )
                return false;
        }

        return true;
    }
}
