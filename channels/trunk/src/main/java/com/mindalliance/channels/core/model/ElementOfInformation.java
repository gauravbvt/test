package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.Matcher;

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
    /**
     * Whether this EOI is subject to the timeliness-from-availability requirements on the flow if notification.
     * Meaningful only for EOIs of info needs.
     */
    private boolean timeSensitive = false;

    public ElementOfInformation() {
    }

    public static List<ElementOfInformation> copy( List<ElementOfInformation> eois ) {
        List<ElementOfInformation> copiedEois = new ArrayList<ElementOfInformation>(  );
        for ( ElementOfInformation eoi : eois ) {
            copiedEois.add( new ElementOfInformation( eoi ) );
        }
        return copiedEois;
    }

    public ElementOfInformation( ElementOfInformation eoi ) {
        content = eoi.getContent();
        classifications = new ArrayList<Classification>( eoi.getClassifications() );
        description = eoi.getDescription();
        specialHandling = eoi.getSpecialHandling();
        transformation = eoi.getTransformation();
        timeSensitive = eoi.isTimeSensitive();
    }

    public ElementOfInformation( String content ) {
        this.content = content;
    }

    public String getContent() {
        return content == null ? "?" : content;
    }

    public void setContent( String content ) {
        this.content = content;
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public String getSpecialHandling() {
        return specialHandling == null ? "" : specialHandling;
    }

    public void setSpecialHandling( String specialHandling ) {
        this.specialHandling = specialHandling;
    }

    public boolean isTimeSensitive() {
        return timeSensitive;
    }

    public void setTimeSensitive( boolean timeSensitive ) {
        this.timeSensitive = timeSensitive;
    }

    @Override
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

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( getLabel() );
        if ( !getDescription().isEmpty() ) {
            sb.append( ": " );
            sb.append( getDescription() );
        }
        if ( !getSpecialHandling().isEmpty() ) {
            sb.append( " -" );
            sb.append( getSpecialHandling() );
        }
        return sb.toString();
    }

    public String getLabel() {
        StringBuilder sb = new StringBuilder();
        sb.append( getContent() );
        if ( !classifications.isEmpty() ) {
            sb.append( '[' );
            for ( Classification classification : classifications ) {
                sb.append( classification.getLabel() );
                sb.append( ' ' );
            }
            sb.deleteCharAt( sb.length() - 1 );
            sb.append( ']' );
        }
        if ( !getDescription().isEmpty() || !getSpecialHandling().isEmpty() ) {
            sb.append( "..." );
        }
        return sb.toString();
    }


    public boolean equals( Object object ) {
        if ( object instanceof ElementOfInformation ) {
            ElementOfInformation other = (ElementOfInformation) object;
            return getContent().equals( other.getContent() ) && transformation.equals( other.getTransformation() );
        } else {
            return false;
        }
    }

    /**
     * {inheritDoc}
     */
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + getContent().hashCode();
        hash = hash * 31 + transformation.hashCode();
        return hash;
    }

    /**
     * Reset all properties except content.
     */
    public void retainContentAndTimeSensitivityOnly() {
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
        merged.setTimeSensitive( eoi.isTimeSensitive() && other.isTimeSensitive() );
        return merged;
    }

    /**
     * Add classifications not already encompassed.
     *
     * @param classificationList a list of classifications
     * @param plan               the plan
     */
    public void addClassifications( List<Classification> classificationList, Plan plan ) {
        for ( Classification classification : classificationList )
            if ( !Classification.encompass( classifications, classification, plan ) )
                classifications.add( classification );
    }


    private boolean sameDefinitionAs( ElementOfInformation eoi ) {
        return Matcher.same( getContent(), eoi.getContent() )
                && Matcher.same( getDescription(), eoi.getDescription() )
                && Matcher.same( getSpecialHandling(), eoi.getSpecialHandling() );
    }

}
