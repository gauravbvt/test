// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.Model;
import com.mindalliance.channels.reference.Type;

/**
 * A problem detected with or within a scenario.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 *
 * @opt attributes
 * @composed - consequences * Issue
 * @navassoc - what 1 ModelElement
 */
public class Issue extends ModelElement {

    /**
     * The severity attached to an evaluation.
     */
    public enum Severity { Low, Medium, High, Critical }

    private String cause;
    private String solution;
    private List<Issue> consequences = new ArrayList<Issue>();
    private double likelihood;
    private Type type;
    private Severity severity;
    private ModelElement what;

    /**
     * Default constructor.
     */
    public Issue() {
        super();
    }

    /**
     * Convenience constructor.
     * @param model the model
     */
    public Issue( Model model ) {
        super( model );
    }

    /**
     * Return the value of cause.
     */
    public String getCause() {
        return this.cause;
    }

    /**
     * Set the value of cause.
     * @param cause The new value of cause
     */
    public void setCause( String cause ) {
        this.cause = cause;
    }

    /**
     * Return the value of impacts.
     */
    public List<Issue> getConsequences() {
        return this.consequences;
    }

    /**
     * Set the value of impacts.
     * @param impacts The new value of impacts
     */
    public void setConsequences( List<Issue> impacts ) {
        this.consequences = impacts;
    }

    /**
     * Add an impact.
     * @param impact the impact
     */
    public void addConsequence( Issue impact ) {
        this.consequences.add( impact );
    }

    /**
     * Remove an impact.
     * @param impact the impact
     */
    public void removeConsequence( Issue impact ) {
        this.consequences.remove( impact );
    }

    /**
     * Return the value of solution.
     */
    public String getSolution() {
        return this.solution;
    }

    /**
     * Set the value of solution.
     * @param solution The new value of solution
     */
    public void setSolution( String solution ) {
        this.solution = solution;
    }

    /**
     * Return the value of likelihood.
     */
    public double getLikelihood() {
        return this.likelihood;
    }

    /**
     * Set the value of likelihood.
     * @param likelihood The new value of likelihood
     */
    public void setLikelihood( double likelihood ) {
        this.likelihood = likelihood;
    }

    /**
     * Return the value of severity.
     */
    public Severity getSeverity() {
        return this.severity;
    }

    /**
     * Set the value of severity.
     * @param severity The new value of severity
     */
    public void setSeverity( Severity severity ) {
        this.severity = severity;
    }

    /**
     * Return the value of with.
     */
    public ModelElement getWhat() {
        return this.what;
    }

    /**
     * Set the value of with.
     * @param with The new value of with
     */
    public void setWhat( ModelElement with ) {
        this.what = with;
    }

    /**
     * Return the value of type.
     */
    public Type getType() {
        return this.type;
    }

    /**
     * Set the value of type.
     * @param type The new value of type
     */
    public void setType( Type type ) {
        this.type = type;
    }
}
