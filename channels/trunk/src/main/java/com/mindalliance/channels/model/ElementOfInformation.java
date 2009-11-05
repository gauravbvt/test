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
    private String sourceCodes = "";
    /**
     * Special handling codes.
     */
    private String specialHandlingCodes = "";

    public ElementOfInformation() {
    }

    public String getContent() {
        return content;
    }

    public void setContent( String content ) {
        this.content = content;
    }

    public String getSourceCodes() {
        return sourceCodes;
    }

    public void setSourceCodes( String sourceCodes ) {
        this.sourceCodes = sourceCodes;
    }

    public String getSpecialHandlingCodes() {
        return specialHandlingCodes;
    }

    public void setSpecialHandlingCodes( String specialHandlingCodes ) {
        this.specialHandlingCodes = specialHandlingCodes;
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
                sb.append( classification.toString() );
                sb.append( ' ' );
            }
            sb.deleteCharAt( sb.length() - 1 );
            sb.append( ']' );
        }
        return sb.toString();
    }

}
