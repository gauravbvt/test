package com.mindalliance.channels.analysis;

import com.mindalliance.channels.model.ModelObject;

/**
 * Analysis about a model object, possibly specific to one of its property.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 26, 2008
 * Time: 9:30:43 AM
 */
abstract public class AnalysisObject {
    /**
     * What model object the analysis is about
     */
    private ModelObject about;
    /**
     * The analysis
     */
    private String description;

    /**
     * What property of the model object the analysis is specifically about
     */
    private String property;

    public ModelObject getAbout() {
        return about;
    }

    public void setAbout( ModelObject about ) {
        this.about = about;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty( String property ) {
        this.property = property;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }


}
