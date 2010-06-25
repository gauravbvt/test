package com.mindalliance.channels.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

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
     * Source codes.
     */
    private String sources = "";
    /**
     * Special handling codes.
     */
    private String specialHandling = "";

    public ElementOfInformation() {
    }

    public ElementOfInformation( ElementOfInformation eoi ) {
        content = eoi.getContent();
        classifications = new ArrayList<Classification>( eoi.getClassifications() );
        sources = eoi.getSources();
        specialHandling = eoi.getSpecialHandling();
    }

    public String getContent() {
        return content;
    }

    public void setContent( String content ) {
        this.content = content;
    }

    public String getSources() {
        return sources;
    }

    public void setSources( String sources ) {
        this.sources = sources;
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
        if ( !sources.isEmpty() ) {
            sb.append( ": " );
            sb.append( sources );
        }
        if ( !specialHandling.isEmpty() ) {
            sb.append( " -" );
            sb.append( specialHandling );
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
        setSources( "" );
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
}
