package com.mindalliance.channels.analysis.data;

import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Subject;
import com.mindalliance.channels.model.Transformation;

import java.io.Serializable;

/**
 * The dissemination of an element of information.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 22, 2010
 * Time: 11:04:57 AM
 */
public class Dissemination implements Serializable {
    /**
     * Flow disseminating eoi.
     */
    private Flow flow;
    /**
     * Transformation type.
     */
    private Transformation.Type transformationType;

    /**
     * Subject transformed to produce eoi.
     */
    private Subject transformedSubject;

    private Subject subject;

    public Dissemination() {
    }

    public Dissemination(
            Flow flow,
            Transformation.Type transformationType,
            Subject transformedSubject,
            Subject subject ) {
        this.flow = flow;
        this.transformationType = transformationType;
        this.transformedSubject = transformedSubject;
        this.subject = subject;
    }

    public Flow getFlow() {
        return flow;
    }

    public void setFlow( Flow flow ) {
        this.flow = flow;
    }

    public Transformation.Type getTransformationType() {
        return transformationType;
    }

    public void setTransformationType( Transformation.Type transformationType ) {
        this.transformationType = transformationType;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject( Subject subject ) {
        this.subject = subject;
    }

    public Subject getTransformedSubject() {
        return transformedSubject;
    }

    public void setTransformedSubject( Subject transformedSubject ) {
        this.transformedSubject = transformedSubject;
    }

    public String getUniqueId() {
        return flow.getId() + ":" + hashCode();
    }

    public static long extractFlowId( String id ) {
        return Long.valueOf( id.substring( id.indexOf( ":" ) + 1 ) );
    }

    public Part getPart( boolean showTargets ) {
        return (Part) ( showTargets ? flow.getTarget() : flow.getSource() );
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return transformedSubject
                + transformationType.getSymbol()
                + subject;
    }
}
