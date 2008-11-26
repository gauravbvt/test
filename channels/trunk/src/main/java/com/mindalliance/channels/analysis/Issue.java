package com.mindalliance.channels.analysis;

import com.mindalliance.channels.model.ModelObject;

/**
 * A problem uncovered about a model object.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 26, 2008
 * Time: 9:30:12 AM
 */
public class Issue extends AnalysisObject {

    /**
     * How to resolve the issue
     */
    private String remediation;

    /**
     * Constructor
     *
     * @param about -- the ModelObject the issue is about
     */
    public Issue( ModelObject about ) {
        super( about );
    }

    /**
     * Constructor
     *
     * @param about    -- the ModelObject the issue is about
     * @param property -- the problematic property
     */
    public Issue( ModelObject about, String property ) {
        super( about, property );
    }

    /**
     * To String
     *
     * @return a string
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( getAbout().getClass().getSimpleName() );
        sb.append( " " );
        sb.append( getAbout().getName() );
        sb.append( "(" );
        sb.append( getAbout().getId() );
        sb.append( ")" );
        sb.append( ": " );
        sb.append( getDescription() );
        return sb.toString();
    }

    public String getRemediation() {
        return remediation;
    }

    public void setRemediation( String remediation ) {
        this.remediation = remediation;
    }

}
