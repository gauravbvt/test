package com.mindalliance.channels.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Element of information.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 2, 2009
 * Time: 3:12:55 PM
 */
public class ElementOfInformation implements Classifiable {
    /**
     * Description of the EOI's content.
     */
    private String content = "";
    /**
     * EOI's classifications.
     */
    private List<Classification> classifications = new ArrayList<Classification>();
    /**
     * Description.
     */
    private String description = "";
    /**
     * Special handling codes.
     */
    private String specialHandling = "";

    private Transformation transformation = new Transformation();

    public ElementOfInformation() {
    }

    public ElementOfInformation( ElementOfInformation eoi ) {
        content = eoi.getContent();
        classifications = new ArrayList<Classification>( eoi.getClassifications() );
        description = eoi.getDescription();
        specialHandling = eoi.getSpecialHandling();
    }

    public String getContent() {
        return content;
    }

    public void setContent( String content ) {
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public String getSpecialHandling() {
        return specialHandling;
    }

    public void setSpecialHandling( String specialHandling ) {
        this.specialHandling = specialHandling;
    }

    public List<Classification> getClassifications() {
        return classifications;
    }

    public void setClassifications( List<Classification> classifications ) {
        this.classifications = classifications;
    }

    public Transformation getTransformation() {
        return transformation;
    }

    public void setTransformation( Transformation transformation ) {
        this.transformation = transformation;
    }

    /**
     * Add a classification to the EOI if unique.
     *
     * @param classification a classification
     * @return a boolean - whether added
     */
    public boolean addClassification( Classification classification ) {
        if ( !classifications.contains( classification ) ) {
            classifications.add( classification );
            return true;
        } else {
            return false;
        }
    }

    /**
     * Whether there is no classification not encompassed by an actor's clearance.
     *
     * @param actor an actor
     * @return a boolean
     */
    public boolean isClearedFor( final Actor actor ) {
        return !CollectionUtils.exists(
                classifications,
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        final Classification eoiClassification = (Classification) object;
                        return !CollectionUtils.exists(
                                actor.getClearances(),
                                new Predicate() {
                                    public boolean evaluate( Object object ) {
                                        Classification clearance = (Classification) object;
                                        return clearance.encompasses( eoiClassification );
                                    }
                                }
                        );
                    }
                }
        );
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( getLabel() );
        if ( !description.isEmpty() ) {
            sb.append( ": " );
            sb.append( description );
        }
        if ( !specialHandling.isEmpty() ) {
            sb.append( " -" );
            sb.append( specialHandling );
        }
        return sb.toString();
    }

    public String getLabel() {
        StringBuilder sb = new StringBuilder();
        sb.append( content );
        if ( !classifications.isEmpty() ) {
            sb.append( '[' );
            for ( Classification classification : classifications ) {
                sb.append( classification.getLabel() );
                sb.append( ' ' );
            }
            sb.deleteCharAt( sb.length() - 1 );
            sb.append( ']' );
        }
        if ( !description.isEmpty() || !specialHandling.isEmpty() ) {
            sb.append( "...");
        }
        return sb.toString();
    }


    /**
     * {@inheritDoc}
     */
    public boolean equals( Object object ) {
        if ( object instanceof ElementOfInformation ) {
            ElementOfInformation other = (ElementOfInformation) object;
            return content.equals( other.getContent() );
        } else {
            return false;
        }
    }

    /**
     * {inheritDoc}
     */
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + content.hashCode();
        return hash;
    }

    /**
     * Reset all properties except content.
     */
    public void retainContentOnly() {
        setClassifications( new ArrayList<Classification>() );
        setDescription( "" );
        setSpecialHandling( "" );

    }

    /**
     * Whether the eoi is classified.
     *
     * @return a boolean
     */
    public boolean isClassified() {
        return !getClassifications().isEmpty();
    }

    /**
     * Merge two synonymous eois.
     *
     * @param eoi   an EOI
     * @param other an EOI
     * @return an EOI
     */
    public static ElementOfInformation merge(
            ElementOfInformation eoi,
            ElementOfInformation other ) {
        ElementOfInformation merged = new ElementOfInformation();
        merged.setContent( eoi.getContent() );
        Set<Classification> mergedClassifications = new HashSet<Classification>( eoi.getClassifications() );
        mergedClassifications.addAll( other.getClassifications() );
        merged.setClassifications( new ArrayList<Classification>( mergedClassifications ) );
        String mergedHandling =
                other.getSpecialHandling().length() > eoi.getSpecialHandling().length()
                        ? other.getSpecialHandling()
                        : eoi.getSpecialHandling();
        merged.setSpecialHandling( mergedHandling );
        String mergedDescription =
                other.getDescription().length() > eoi.getDescription().length()
                        ? other.getDescription()
                        : eoi.getDescription();
        merged.setDescription( mergedDescription );
        return merged;
    }

}
