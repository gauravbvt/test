package com.mindalliance.channels.analysis;

import com.mindalliance.channels.ModelObject;

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
     * Type of issue having to do with the definition of a model object
     */
    public static final String DEFINITION = "Definition";
    /**
     * Type of having having to do with a flow of information
     */
    public static final String FLOW = "Flow";
    /**
     * Type of issue having to do with a scenario as a whole
     */
    public static final String SYSTEMIC = "Systemic";
    /**
     * All possible types of issues
     */
    public static final String[] TYPES = new String[]{DEFINITION, FLOW, SYSTEMIC};
    /**
     * Type of issue
     */
    private String type;

    /**
     * How to resolve the issue
     */
    private String remediation;

    /**
     * Constructor
     *
     * @param type  -- the type of issue
     * @param about -- the ModelObject the issue is about
     */
    public Issue( String type, ModelObject about ) {
        super( about );
        this.type = type;
    }

    /**
     * Constructor
     *
     * @param type     -- the type of issue
     * @param about    -- the ModelObject the issue is about
     * @param property -- the problematic property
     */
    public Issue( String type, ModelObject about, String property ) {
        super( about, property );
        this.type = type;
    }

    /**
     * To String
     *
     * @return a string
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( type );
        sb.append( " issue on " );
        sb.append( getAbout().getClass().getSimpleName() );
        sb.append( " " );
        sb.append( getAbout().getName() );
        sb.append( "(" );
        sb.append( getAbout().getId() );
        sb.append( ")" );
        sb.append( ": " );
        sb.append( getDescription() );
        if ( remediation != null ) {
            sb.append( " [" );
            sb.append( remediation );
            sb.append( ']' );
        }
        return sb.toString();
    }

    public String getRemediation() {
        return remediation;
    }

    public void setRemediation( String remediation ) {
        this.remediation = remediation;
    }

}
