// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.reference.Policy;
import com.mindalliance.channels.system.Channel;

/**
 * A change in applicable policies and/or channel availability for
 * the duration of an event.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 *
 * @opt attributes
 */
public class Environment extends Occurence {

    private List<Channel> disabledChannels = new ArrayList<Channel>();
    private List<Channel> enabledChannels = new ArrayList<Channel>();
    private List<Policy> suspendedPolicies = new ArrayList<Policy>();
    private List<Policy> enforcedPolicies = new ArrayList<Policy>();

    /**
     * Default constructor.
     */
    Environment() {
        super();
    }

    /**
     * Convenience constructor.
     * @param scenario the scenario
     */
    public Environment( Scenario scenario ) {
        super( scenario );
    }

    /**
     * Return the value of disabledChannels.
     */
    public List<Channel> getDisabledChannels() {
        return this.disabledChannels;
    }

    /**
     * Set the value of disabledChannels.
     * @param disabledChannels The new value of disabledChannels
     */
    public void setDisabledChannels( List<Channel> disabledChannels ) {
        this.disabledChannels = disabledChannels;
    }

    /**
     * Return the value of enabledChannels.
     */
    public List<Channel> getEnabledChannels() {
        return this.enabledChannels;
    }

    /**
     * Set the value of enabledChannels.
     * @param enabledChannels The new value of enabledChannels
     */
    public void setEnabledChannels( List<Channel> enabledChannels ) {
        this.enabledChannels = enabledChannels;
    }

    /**
     * Return the value of enforcedPolicies.
     */
    public List<Policy> getEnforcedPolicies() {
        return this.enforcedPolicies;
    }

    /**
     * Set the value of enforcedPolicies.
     * @param enforcedPolicies The new value of enforcedPolicies
     */
    public void setEnforcedPolicies( List<Policy> enforcedPolicies ) {
        this.enforcedPolicies = enforcedPolicies;
    }

    /**
     * Return the value of suspendedPolicies.
     */
    public List<Policy> getSuspendedPolicies() {
        return this.suspendedPolicies;
    }

    /**
     * Set the value of suspendedPolicies.
     * @param suspendedPolicies The new value of suspendedPolicies
     */
    public void setSuspendedPolicies( List<Policy> suspendedPolicies ) {
        this.suspendedPolicies = suspendedPolicies;
    }
}
