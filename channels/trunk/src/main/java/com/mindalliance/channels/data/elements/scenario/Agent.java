// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.elements.scenario;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.data.OptOutable;
import com.mindalliance.channels.data.components.Knowledgeable;
import com.mindalliance.channels.data.elements.assertions.Assertion;
import com.mindalliance.channels.data.elements.assertions.CanAccess;
import com.mindalliance.channels.data.elements.assertions.NeedsToKnow;
import com.mindalliance.channels.data.elements.assertions.OptedOut;
import com.mindalliance.channels.data.elements.resources.Actor;
import com.mindalliance.channels.data.reference.Information;
import com.mindalliance.channels.util.GUID;

/**
 * A specification of which actors execute a task, together or separately.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public abstract class Agent extends AbstractScenarioElement
    implements Knowledgeable, OptOutable {

    private Task task;

    /**
     * Default constructor.
     */
    public Agent() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Agent( GUID guid ) {
        super( guid );
    }

    /**
     * Return the actors specified by this agent.
     */
    public abstract List<Actor> getActors();

    /**
     * Get the list of CanAccess assertions.
     */
    public List<CanAccess> getCanAccessAssertions() {
        List<CanAccess> canAccessAssertions = new ArrayList<CanAccess>();
        for ( Assertion assertion : getAssertions() ) {
            if ( assertion instanceof CanAccess )
                canAccessAssertions.add( (CanAccess) assertion );
        }
        return canAccessAssertions;
    }

    /**
     * Get the list of NeedToKnow assertions.
     */
    public List<NeedsToKnow> getNeedsToKnowAssertions() {
        List<NeedsToKnow> needsToKnowAssertions = new ArrayList<NeedsToKnow>();
        for ( Assertion assertion : getAssertions() ) {
            if ( assertion instanceof NeedsToKnow )
                needsToKnowAssertions.add( (NeedsToKnow) assertion );
        }
        return needsToKnowAssertions;
    }

    /**
     * Return the optedOut assertions.
     */
    public List<OptedOut> getOptedOutAssertions() {
        List<OptedOut> optedOutAssertions = new ArrayList<OptedOut>();
        for ( Assertion assertion : getAssertions() ) {
            if ( assertion instanceof OptedOut )
                optedOutAssertions.add( (OptedOut) assertion );
        }
        return optedOutAssertions;
    }

    /**
     * Test if this agent knows of an information.
     * @param information the information
     */
    public boolean knows( Information information ) {
        // TODO
        return false;
    }

    /**
     * Test if this agent needs to know an information.
     * @param information the information
     */
    public boolean needsToKnow( Information information ) {
        // TODO
        return false;
    }

    /**
     * Return the task.
     */
    public Task getTask() {
        return task;
    }

    /**
     * Set the task.
     * @param task the task to set
     */
    public void setTask( Task task ) {
        this.task = task;
    }

}
