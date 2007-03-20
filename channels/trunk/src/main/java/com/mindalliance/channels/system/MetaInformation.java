// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.system;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.JavaBean;

/**
 * A specification of the nature, composition and source of
 * information that is either needed, known or (to be) transmitted.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 *
 * @opt attributes
 * @composed - segments * MetaInformation
 */
public class MetaInformation {

    private JavaBean about;
    private List<String> elements = new ArrayList<String>();
    private List<MetaInformation> segments = new ArrayList<MetaInformation>();

    /**
     * Default constructor.
     */
    MetaInformation() {
    }

    /**
     * Return the value of elements.
     */
    public List<String> getElements() {
        return this.elements;
    }

    /**
     * Set the value of elements.
     * @param elements The new value of elements
     */
    public void setElements( List<String> elements ) {
        this.elements = elements;
    }

    /**
     * Return the value of segments.
     */
    public List<MetaInformation> getSegments() {
        return this.segments;
    }

    /**
     * Set the value of segments.
     * @param segments The new value of segments
     */
    public void setSegments( List<MetaInformation> segments ) {
        this.segments = segments;
    }

    /**
     * Return the value of about.
     */
    public JavaBean getAbout() {
        return this.about;
    }

    /**
     * Set the value of about.
     * @param about The new value of about
     */
    public void setAbout( JavaBean about ) {
        this.about = about;
    }
}
