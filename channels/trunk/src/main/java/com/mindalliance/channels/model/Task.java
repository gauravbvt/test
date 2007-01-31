// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.GUID;

/**
 * A task of some kind that would be carried out by one or more
 * agents in response to some event in order to fulfill role-based
 * responsibilities; a task may have outcomes if it fails or succeeds
 * and may require and generate information.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class Task extends AbstractNamedObject {

    private Duration duration;
    private List<InformationNeed> input = new ArrayList<InformationNeed>();
    private List<InformationGain> output = new ArrayList<InformationGain>();
    private List<Outcome> successOutcomes = new ArrayList<Outcome>();
    private List<Outcome> failureOutcomes = new ArrayList<Outcome>();
    private Information details;

    /**
     * Default constructor.
     * @param guid the unique ID for this object
     */
    Task( GUID guid ) {
        super( guid );
    }

    /**
     * Return the value of details.
     */
    public Information getDetails() {
        return this.details;
    }

    /**
     * Set the value of details.
     * @param details The new value of details
     */
    public void setDetails( Information details ) {
        this.details = details;
    }

    /**
     * Return the value of duration.
     */
    public Duration getDuration() {
        return this.duration;
    }

    /**
     * Set the value of duration.
     * @param duration The new value of duration
     */
    public void setDuration( Duration duration ) {
        this.duration = duration;
    }

    /**
     * Return the value of failureOutcomes.
     */
    public List<Outcome> getFailureOutcomes() {
        return this.failureOutcomes;
    }

    /**
     * Set the value of failureOutcomes.
     * @param failureOutcomes The new value of failureOutcomes
     */
    public void setFailureOutcomes( List<Outcome> failureOutcomes ) {
        this.failureOutcomes = failureOutcomes;
    }

    /**
     * Return the value of input.
     */
    public List<InformationNeed> getInput() {
        return this.input;
    }

    /**
     * Set the value of input.
     * @param input The new value of input
     */
    public void setInput( List<InformationNeed> input ) {
        this.input = input;
    }

    /**
     * Return the value of output.
     */
    public List<InformationGain> getOutput() {
        return this.output;
    }

    /**
     * Set the value of output.
     * @param output The new value of output
     */
    public void setOutput( List<InformationGain> output ) {
        this.output = output;
    }

    /**
     * Return the value of successOutcomes.
     */
    public List<Outcome> getSuccessOutcomes() {
        return this.successOutcomes;
    }

    /**
     * Set the value of successOutcomes.
     * @param successOutcomes The new value of successOutcomes
     */
    public void setSuccessOutcomes( List<Outcome> successOutcomes ) {
        this.successOutcomes = successOutcomes;
    }
}
