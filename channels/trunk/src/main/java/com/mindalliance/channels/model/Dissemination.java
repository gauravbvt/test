package com.mindalliance.channels.model;

import com.mindalliance.channels.model.Transformation.Type;

import java.io.Serializable;

/**
 * The dissemination of an element of information.
 */
public class Dissemination implements Serializable {

    /**
     * Flow disseminating eoi.
     */
    private Flow flow;

    /**
     * Transformation type.
     */
    private Type transformationType;

    /**
     * Subject transformed to produce eoi.
     */
    private Subject transformedSubject;

    /**
     * Transmission delay.
     */
    private Delay delay;
    /**
     * Subject transmitted.
     */
    private Subject subject;

    /**
     * Root dissemination.
     */
    private boolean root;
    /**
     * The part from which dissemination is calculated (forward or backward).
     */
    private Part startPart;
    /**
     * The initial subject disseminated.
     */
    private Subject startSubject;
    /**
     * Whether dissemination is to targets or from sources.
     */
    private boolean toTargets;

    public Dissemination() {
    }

    public Dissemination(
            Flow flow,
            Type transformationType,
            Delay delay,
            Subject transformedSubject,
            Subject subject ) {
        this(
                flow,
                transformationType,
                delay,
                transformedSubject,
                subject,
                (Part)flow.getSource(),
                subject,
                true );
    }

    public Dissemination(
            Flow flow,
            Type transformationType,
            Delay delay,
            Subject transformedSubject,
            Subject subject,
            Part startPart,
            Subject startSubject,
            boolean toTargets) {

        this.flow = flow;
        this.delay = delay;
        this.transformationType = transformationType;
        this.transformedSubject = transformedSubject;
        this.subject = subject;
        this.startPart = startPart;
        this.startSubject = startSubject;
        this.toTargets = toTargets;
    }

    public Flow getFlow() {
        return flow;
    }

    public void setFlow( Flow flow ) {
        this.flow = flow;
    }

    public Type getTransformationType() {
        return transformationType;
    }

    public void setTransformationType( Type transformationType ) {
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

    public boolean isRoot() {
        return root;
    }

    public void setRoot( boolean root ) {
        this.root = root;
    }

    public Delay getDelay() {
        return delay;
    }

    public void setDelay( Delay delay ) {
        this.delay = delay;
    }

    public String getUniqueId() {
        return flow.getId() + ":" + hashCode();
    }

    public Part getPart( boolean showTargets ) {
        return (Part) ( showTargets ? flow.getTarget() : flow.getSource() );
    }

    public Delay getNeedMaxDelay() {
        Part needyPart = toTargets ? getPart( true ) : startPart;
        Subject neededSubject = toTargets ? getSubject() : startSubject;
        Flow need = needyPart.findNeedFor( neededSubject );
        if ( need == null ) {
            return null;
        } else {
            return need.getMaxDelay();
        }
    }

    public String toString() {
        return transformedSubject + transformationType.getSymbol() + subject;
    }

    public void addToDelay( Delay val ) {
        delay = delay.add( val );
    }

    public boolean isTimely() {
        Delay required = getNeedMaxDelay();
        return required == null || getDelay().compareTo( required ) <= 0;
    }

    public boolean equals( Object other ) {
        if ( other instanceof Dissemination ) {
            Dissemination dissemination = (Dissemination) other;
            return flow.equals( dissemination.getFlow() )
                    && subject.equals( dissemination.getSubject() );
        } else {
            return false;
        }
    }

    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + flow.hashCode();
        hash = hash * 31 + subject.hashCode();
        return hash;
    }
}
