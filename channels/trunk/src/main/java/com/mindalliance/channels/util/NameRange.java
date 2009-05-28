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
    private String[] bounds;
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
        return isEmpty() ||
                ( collator.compare( lower, name ) <= 0
                        && collator.compare( name, upper ) <= 0 );
    }

    public String getLowerBound() {
        return getBounds()[0];
    }


    public String getUpperBound() {
        return getBounds()[1];
    }

    private String[] getBounds() {
        if ( bounds == null ) {
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
            index = 0;
            do {
                upperBound.append( upper.charAt( index ) );
                done = lower.length() <= index
                        || posterior.length() <= index
                        || upper.length() == index + 1
                        || ( upper.charAt( index ) != lower.charAt( index )
                        && ( upper.charAt( index ) != posterior.charAt( index ) ) );
                index++;
            } while ( !done );
            bounds = new String[2];
            bounds[0] = StringUtils.capitalize( lowerBound.toString() );
            bounds[1] = StringUtils.capitalize( upperBound.toString() );
        }
        return bounds;
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
            return getBounds()[0]
                    + "-"
                    + getBounds()[1];
        }
    }

}
