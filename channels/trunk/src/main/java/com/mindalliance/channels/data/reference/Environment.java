/*
 * Created on Apr 26, 2007
 */
package com.mindalliance.channels.data.reference;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.mindalliance.channels.data.Resource;
import com.mindalliance.channels.data.elements.scenario.Event;
import com.mindalliance.channels.data.support.Pattern;
import com.mindalliance.channels.util.GUID;

/**
 * A specification for situations; during what event they occur, what
 * resources become (un)available and what policies (do not) apply.
 * 
 * @author jf
 */
public class Environment extends ReferenceData {

    public class TriggerCondition {

        private Pattern<Event> eventPattern;

        public boolean isTriggeredBy( Event event ) {
            return eventPattern.matches( event );
        }

        /**
         * @return the eventPattern
         */
        public Pattern<Event> getEventPattern() {
            return eventPattern;
        }

        /**
         * @param eventPattern the eventPattern to set
         */
        public void setEventPattern( Pattern<Event> eventPattern ) {
            this.eventPattern = eventPattern;
        }
    }

    private List<TriggerCondition> triggerConditions; // Event
                                                        // patterns
    private List<Resource> availableResources;
    private List<Resource> unavailableResources;
    private List<Policy> enforcedPolicies;
    private List<Policy> unenforcedPolicies;

    /**
     * Whether an event matches one of the trigger conditions for this
     * environment.
     * 
     * @param event
     * @return
     */
    public boolean isTriggeredBy( final Event event ) {
        return CollectionUtils.exists( triggerConditions, new Predicate() {

            public boolean evaluate( Object object ) {
                TriggerCondition triggerCondition = (TriggerCondition) object;
                return triggerCondition.isTriggeredBy( event );
            }
        } );
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
     * @return the triggerConditions
     */
    public List<TriggerCondition> getTriggerConditions() {
        return triggerConditions;
    }

    /**
     * @param triggerConditions the triggerConditions to set
     */
    public void setTriggerConditions( List<TriggerCondition> triggerConditions ) {
        this.triggerConditions = triggerConditions;
    }

    /**
     * @param triggerCondition
     */
    public void addTriggerCondition( TriggerCondition triggerCondition ) {
        triggerConditions.add( triggerCondition );
    }

    /**
     * @param triggerCondition
     */
    public void removeTriggerCondition( TriggerCondition triggerCondition ) {
        triggerConditions.remove( triggerCondition );
    }

}
