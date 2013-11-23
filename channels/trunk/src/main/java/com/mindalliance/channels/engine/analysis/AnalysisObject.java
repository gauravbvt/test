package com.mindalliance.channels.engine.analysis;

import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelObject;

import java.io.Serializable;

/**
 * Analysis about an identifiable, possibly specific to one of its property.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 26, 2008
 * Time: 9:30:43 AM
 */
public abstract class AnalysisObject implements Serializable {

    /**
     * What the analysis is about
     */
    private Identifiable about;

    /**
     * The analysis
     */
    private String description;

    /**
     * What property of the model object the analysis is specifically about
     */
    private String property;

    public AnalysisObject() {}

    /**
     * Constructor
     *
     * @param identifiable -- what the issue is about
     */
    public AnalysisObject( Identifiable identifiable ) {
        this.about = identifiable;
    }

    /**
     * Constructor
     *
     * @param about    -- the ModelObject the issue is about
     * @param property -- the problematic property
     */
    public AnalysisObject( Identifiable about, String property ) {
        this( about );
        this.property = property;
    }

    public Identifiable getAbout() {
        return about;
    }

    public void setAbout( Identifiable about ) {
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
