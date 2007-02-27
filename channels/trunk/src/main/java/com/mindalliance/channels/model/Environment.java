// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.util.GUID;

/**
 * A change in applicable policies and/or channel availability for
 * the duration of an event.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class Environment extends AbstractNamedObject {

    private List<Event> triggers = new ArrayList<Event>();
    private List<Channel> disabledChannels = new ArrayList<Channel>();
    private List<Channel> enabledChannels = new ArrayList<Channel>();
    private List<Policy> suspendedPolicies = new ArrayList<Policy>();
    private List<Policy> enforcedPolicies = new ArrayList<Policy>();

    /**
     * Default constructor.
     * @param guid the unique ID for this object
     */
    Environment( GUID guid ) {
        super( guid );
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

    /**
     * Return the value of triggers.
     */
    public List<Event> getTriggers() {
        return this.triggers;
    }

    /**
     * Set the value of triggers.
     * @param triggers The new value of triggers
     */
    public void setTriggers( List<Event> triggers ) {
        this.triggers = triggers;
    }
}
