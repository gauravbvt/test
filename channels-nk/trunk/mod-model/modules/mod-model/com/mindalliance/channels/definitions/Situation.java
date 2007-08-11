// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.definitions;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.definitions.Category.Taxonomy;
import com.mindalliance.channels.support.Duration;

/**
 * A specification for situations;
 * during what event categories they occur,
 * what resources categrories become (un)available and
 * what policies do or (do not) apply.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision$
 *
 * @composed - triggers * CategorySet
 * @composed - resources * CategorySet
 * @navassoc - - * Policy
 */
public class Situation extends TypedObject {

    private CategorySet triggers = new CategorySet( Taxonomy.Event );
    private CategorySet endTriggers = new CategorySet( Taxonomy.Event );
    private Duration duration;
    private CategorySet locations = new CategorySet( Taxonomy.Location );
    private CategorySet availableResources = new CategorySet();
    private CategorySet unavailableResources = new CategorySet();;
    private List<Policy> enforcedPolicies = new ArrayList<Policy>();
    private List<Policy> unenforcedPolicies = new ArrayList<Policy>();

    /**
     * Default constructor.
     */
    public Situation() {
        super( null, Taxonomy.Situation );
    }

    /**
     * Whether an event matches one of the trigger conditions for this
     * situation.
     * @param event the event
     */
    public boolean isStartedBy( final TypedObject event ) {
        return event.getCategorySet().implies( getTriggers() );
    }

    /**
     * Whether an event matches one of the ends conditions for this
     * situation.
     * @param event the event
     */
    public boolean isStoppedBy( final TypedObject event ) {
        return event.getCategorySet().implies( getEndTriggers() );
    }

    /**
     * Test if an object is disabled in this situation.
     * @param object the object
     */
    public boolean disables( TypedObject object ) {
        return object.getCategorySet().implies( getUnavailableResources() );
    }

    /**
     * Test if an object is enabled in this situation.
     * @param object the object
     */
    public boolean enables( TypedObject object ) {
        return object.getCategorySet().implies( getAvailableResources() );
    }

    /**
     * Test if a policy is enabled in this situation.
     * @param policy the policy
     */
    public boolean enables( Policy policy ) {
        return getEnforcedPolicies().contains( policy );
    }

    /**
     * Test if a policy is disabled in this situation.
     * @param policy the policy
     */
    public boolean disables( Policy policy ) {
        return getUnenforcedPolicies().contains( policy );
    }

    /**
     * Return the normally inoperational resources that become operational.
     */
    public CategorySet getAvailableResources() {
        return availableResources;
    }

    /**
     * Set the available resources.
     * @param availableResources the availableResources to set
     */
    public void setAvailableResources( CategorySet availableResources ) {
        this.availableResources = availableResources;
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
     * Return the normally operational resources categories
     * that become inoperational.
     */
    public CategorySet getUnavailableResources() {
        return unavailableResources;
    }

    /**
     * Set the unavailable resources.
     * @param unavailableResources the unavailableResources to set
     */
    public void setUnavailableResources( CategorySet unavailableResources ) {
        this.unavailableResources = unavailableResources;
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
     * Return categories of event triggering this environment.
     */
    public CategorySet getTriggers() {
        return triggers;
    }

    /**
     * Set the value of triggers.
     * @param triggers The new value of triggers
     */
    public void setTriggers( CategorySet triggers ) {
        this.triggers = triggers;
    }

    /**
     * Return the type of location that minimally encompasses the location
     * of the trigger event.
     */
    public CategorySet getLocations() {
        return locations;
    }

    /**
     * Set the value of compasss.
     * @param locations The new value of locations
     */
    public void setLocations( CategorySet locations ) {
        this.locations = locations;
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

    /**
     * Return the endTriggers.
     */
    public CategorySet getEndTriggers() {
        return this.endTriggers;
    }

    /**
     * Set the endTriggers.
     * @param endTriggers the endTriggers
     */
    public void setEndTriggers( CategorySet endTriggers ) {
        this.endTriggers = endTriggers;
    }
}
