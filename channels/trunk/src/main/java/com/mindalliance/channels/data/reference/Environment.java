/*
 * Created on Apr 26, 2007
 */
package com.mindalliance.channels.data.reference;

import java.util.List;

import com.mindalliance.channels.data.Resource;
import com.mindalliance.channels.data.elements.scenario.Event;
import com.mindalliance.channels.data.support.Duration;
import com.mindalliance.channels.data.support.Pattern;
import com.mindalliance.channels.data.support.TypeSet;

/**
 * A specification for situations; during what event they occur, what
 * resources become (un)available and what policies (do not) apply.
 * 
 * @author jf
 */
public class Environment extends TypedReferenceData {

    // What kind of event triggers this environment
    private Pattern<Event> triggerCondition;  // An event pattern
    // How long this environment stays in effect
    private Duration duration; // if null, then use the trigger event's duration
    // The type of location that minimally encompasses the location of the trigger event.
    private TypeSet compasss = new TypeSet(Type.LOCATION, TypeSet.SINGLETON);
    // The normally inoperational resources that become operational
    private List<Resource> availableResources;
    // The normally operational resources that become inoperational
    private List<Resource> unavailableResources;
    // The normally inoperative policies that become operative
    private List<Policy> enforcedPolicies;
    // The normally operative policies that become inoperative
    private List<Policy> unenforcedPolicies;

    /**
     * Whether an event matches one of the trigger conditions for this
     * environment.
     * 
     * @param event
     * @return
     */
    public boolean isTriggeredBy( final Event event ) {
        return triggerCondition.matches( event );
    }

    /**
     * @return the availableResources
     */
    public List<Resource> getAvailableResources() {
        return availableResources;
    }

    /**
     * @param availableResources the availableResources to set
     */
    public void setAvailableResources( List<Resource> availableResources ) {
        this.availableResources = availableResources;
    }

    /**
     * @param resource
     */
    public void addAvailableResource( Resource resource ) {
        availableResources.add( resource );
    }

    /**
     * @param resource
     */
    public void removeAvailableResource( Resource resource ) {
        availableResources.remove( resource );
    }

    /**
     * @return the enforcedPolicies
     */
    public List<Policy> getEnforcedPolicies() {
        return enforcedPolicies;
    }

    /**
     * @param enforcedPolicies the enforcedPolicies to set
     */
    public void setEnforcedPolicies( List<Policy> enforcedPolicies ) {
        this.enforcedPolicies = enforcedPolicies;
    }

    /**
     * @param policy
     */
    public void addEnforcedPolicy( Policy policy ) {
        enforcedPolicies.add( policy );
    }

    /**
     * @param policy
     */
    public void removeEnforcedPolicy( Policy policy ) {
        enforcedPolicies.remove( policy );
    }

    /**
     * @return the unavailableResources
     */
    public List<Resource> getUnavailableResources() {
        return unavailableResources;
    }

    /**
     * @param unavailableResources the unavailableResources to set
     */
    public void setUnavailableResources( List<Resource> unavailableResources ) {
        this.unavailableResources = unavailableResources;
    }

    /**
     * @param resource
     */
    public void addUnavailableResource( Resource resource ) {
        unavailableResources.add( resource );
    }

    /**
     * @param resource
     */
    public void removeUnavailableResource( Resource resource ) {
        unavailableResources.remove( resource );
    }

    /**
     * @return the unenforcedPolicies
     */
    public List<Policy> getUnenforcedPolicies() {
        return unenforcedPolicies;
    }

    /**
     * @param unenforcedPolicies the unenforcedPolicies to set
     */
    public void setUnenforcedPolicies( List<Policy> unenforcedPolicies ) {
        this.unenforcedPolicies = unenforcedPolicies;
    }

    /**
     * @param policy
     */
    public void addUnenforcedPolicy( Policy policy ) {
        unenforcedPolicies.add( policy );
    }

    /**
     * @param policy
     */
    public void removeUnenforcedPolicy( Policy policy ) {
        unenforcedPolicies.remove( policy );
    }

    
    /**
     * Return the value of triggerCondition.
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
     * Return the value of compasss.
     */
    public TypeSet getCompasss() {
        return compasss;
    }

    
    /**
     * Set the value of compasss.
     * @param compasss The new value of compasss
     */
    public void setCompasss( TypeSet compasss ) {
        this.compasss = compasss;
    }

    
    /**
     * Return the value of duration.
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
