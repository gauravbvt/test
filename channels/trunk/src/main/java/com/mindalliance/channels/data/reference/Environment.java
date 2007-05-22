// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.reference;

import java.util.List;

import com.mindalliance.channels.data.Resource;
import com.mindalliance.channels.data.elements.scenario.Event;
import com.mindalliance.channels.data.support.Duration;
import com.mindalliance.channels.data.support.Pattern;

/**
 * A specification for situations; during what event they occur, what
 * resources become (un)available and what policies (do not) apply.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision$
 */
public class Environment extends TypedReferenceData {

    private Pattern<Event> triggerCondition;
    private Duration duration;
    private TypeSet compass;
    private List<Resource> availableResources;
    private List<Resource> unavailableResources;
    private List<Policy> enforcedPolicies;
    private List<Policy> unenforcedPolicies;

    /**
     * Default constructor.
     */
    public Environment() {
        super();
    }

    /**
     * Whether an event matches one of the trigger conditions for this
     * environment.
     *
     * @param event the event
     */
    public boolean isTriggeredBy( final Event event ) {
        return triggerCondition.matches( event );
    }

    /**
     * Return the normally inoperational resources that become operational.
     */
    public List<Resource> getAvailableResources() {
        return availableResources;
    }

    /**
     * Set the available resources.
     * @param availableResources the availableResources to set
     */
    public void setAvailableResources( List<Resource> availableResources ) {
        this.availableResources = availableResources;
    }

    /**
     * Add an available resource.
     * @param resource the resource
     */
    public void addAvailableResource( Resource resource ) {
        availableResources.add( resource );
    }

    /**
     * Remove an available resource.
     * @param resource the resource
     */
    public void removeAvailableResource( Resource resource ) {
        availableResources.remove( resource );
    }

    /**
     * Return the normally inoperative policies that become operative.
     */
    public List<Policy> getEnforcedPolicies() {
        return enforcedPolicies;
    }

    /**
     * Set the enforced policies.
     * @param enforcedPolicies the enforcedPolicies to set
     */
    public void setEnforcedPolicies( List<Policy> enforcedPolicies ) {
        this.enforcedPolicies = enforcedPolicies;
    }

    /**
     * Add an enforced policy.
     * @param policy the policy
     */
    public void addEnforcedPolicy( Policy policy ) {
        enforcedPolicies.add( policy );
    }

    /**
     * Remove an enforced policy.
     * @param policy the policy
     */
    public void removeEnforcedPolicy( Policy policy ) {
        enforcedPolicies.remove( policy );
    }

    /**
     * Return the normally operational resources that become inoperational.
     */
    public List<Resource> getUnavailableResources() {
        return unavailableResources;
    }

    /**
     * Set the unavailable resources.
     * @param unavailableResources the unavailableResources to set
     */
    public void setUnavailableResources( List<Resource> unavailableResources ) {
        this.unavailableResources = unavailableResources;
    }

    /**
     * Add an unavailable resource.
     * @param resource the resource
     */
    public void addUnavailableResource( Resource resource ) {
        unavailableResources.add( resource );
    }

    /**
     * Remove an unavailable resource.
     * @param resource the resource
     */
    public void removeUnavailableResource( Resource resource ) {
        unavailableResources.remove( resource );
    }

    /**
     * Return the normally operative policies that become inoperative.
     */
    public List<Policy> getUnenforcedPolicies() {
        return unenforcedPolicies;
    }

    /**
     * Set the unenforced policies.
     * @param unenforcedPolicies the unenforcedPolicies to set
     */
    public void setUnenforcedPolicies( List<Policy> unenforcedPolicies ) {
        this.unenforcedPolicies = unenforcedPolicies;
    }

    /**
     * Add an unenforced policy.
     * @param policy the policy
     */
    public void addUnenforcedPolicy( Policy policy ) {
        unenforcedPolicies.add( policy );
    }

    /**
     * Remove an unenforced policy.
     * @param policy the policy
     */
    public void removeUnenforcedPolicy( Policy policy ) {
        unenforcedPolicies.remove( policy );
    }

    /**
     * Return what kind of event triggers this environment.
     */
    public Pattern<Event> getTriggerCondition() {
        return triggerCondition;
    }

    /**
     * Set the value of triggerCondition.
     * @param triggerCondition The new value of triggerCondition
     */
    public void setTriggerCondition( Pattern<Event> triggerCondition ) {
        this.triggerCondition = triggerCondition;
    }

    /**
     * Return the type of location that minimally encompasses the location
     * of the trigger event.
     */
    public TypeSet getCompass() {
        return compass;
    }

    /**
     * Set the value of compasss.
     * @param compass The new value of compass
     */
    public void setCompass( TypeSet compass ) {
        this.compass = compass;
    }

    /**
     * Return how long this environment stays in effect.
     * If null, then use the trigger event's duration.
     */
    public Duration getDuration() {
        return duration;
    }

    /**
     * Set the value of duration.
     * @param duration The new value of duration
     */
    public void setDuration( Duration duration ) {
        this.duration = duration;
    }
}
