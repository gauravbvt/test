package com.mindalliance.channels.util;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.text.Collator;

/**
 * A name range.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 27, 2009
 * Time: 7:32:35 PM
 */
public class NameRange implements Serializable {
    /**
     * Upper bound of prior range.
     */
    private String prior = "";
    /**
     * Lower bound of next range.
     */
    private String posterior = "";
    /**
     * Lower bound.
     */
    private String lower;
    /**
     * Upper bound.
     */
    private String upper;
    /**
     * Boundaries as smallest possible strings.
     */
    private String[] boundLabels;
    /**
     * COllator.
     */
    private static Collator collator = Collator.getInstance();

    public NameRange() {
    }

    public NameRange( String lower, String upper ) {
        this.lower = lower;
        assert !this.lower.isEmpty();
        this.upper = upper;
        assert !this.upper.isEmpty();
    }

    public String getLower() {
        return lower;
    }

    public String getUpper() {
        return upper;
    }

    public void setPrior( String prior ) {
        this.prior = prior;
    }

    public void setPosterior( String posterior ) {
        this.posterior = posterior;
    }

    public boolean isEmpty() {
        return lower == null || upper == null || lower.isEmpty() || upper.isEmpty();
    }

    public boolean contains( String name ) {
        String lcName = name.toLowerCase();
        return isEmpty() ||
                ( collator.compare( lower, lcName ) <= 0
                        && collator.compare( lcName, upper ) <= 0 );
    }

    public String getLowerBound() {
        return getBoundLabels()[0];
    }


    public String getUpperBound() {
        return getBoundLabels()[1];
    }

    private String[] getBoundLabels() {
        if ( boundLabels == null ) {
            StringBuilder lowerBound = new StringBuilder();
            StringBuilder upperBound = new StringBuilder();
            int index = 0;
            boolean done;
            do {
                lowerBound.append( lower.charAt( index ) );
                done = prior.length() <= index
                        || lower.length() == index + 1
                        || lower.charAt( index ) != prior.charAt( index );
                index++;

            } while ( !done );
            if ( upper.equals( lower ) ) {
                upperBound.append( lowerBound.toString() );
            } else {
                index = 0;
                do {
                    upperBound.append( upper.charAt( index ) );
                    done = lower.length() <= index
                            || upper.length() == index + 1
                            || (
                            upper.charAt( index ) != lower.charAt( index )
                                    && ( posterior.length() <= index || upper.charAt( index ) != posterior.charAt( index ) )
                    );
                    index++;
                } while ( !done );
            }
            boundLabels = new String[2];
            boundLabels[0] = StringUtils.capitalize( lowerBound.toString() );
            boundLabels[1] = StringUtils.capitalize( upperBound.toString() );
        }
        return boundLabels;
    }

    /**
     * Get label.
     *
     * @return a string
     */
    public String getLabel() {
        if ( isEmpty() ) {
            return null;
        } else {
            return getBoundLabels()[0]
                    + "-"
                    + getBoundLabels()[1];
        }
    }

}
