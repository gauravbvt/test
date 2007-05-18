// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.elements.resources;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.data.Actor;
import com.mindalliance.channels.data.Contactable;
import com.mindalliance.channels.data.components.Information;
import com.mindalliance.channels.data.elements.assertions.Assertion;
import com.mindalliance.channels.data.elements.assertions.CanAccess;
import com.mindalliance.channels.data.elements.assertions.NeedsToKnow;
import com.mindalliance.channels.util.GUID;

/**
 * An accessible resource who can also execute tasks (actors are specified by agents in scenarios).
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
abstract public class AbstractActor extends AccessibleResource implements Actor {
    
    private List<Information> expertise;

    public AbstractActor() {
        super();
    }

    public AbstractActor( GUID guid ) {
        super( guid );
    }

    /**
     * What the agent knows (general knowledge plus possibly situational knowledge)
     */
    public boolean knows( Information information ) {
        return false; // TODO
    }

    /**
     * What situational information (i.e. in scenario) the actor needs.
     */
    public boolean needsToKnow( Information information ) {
        return false; // TODO
    }

    public List<NeedsToKnow> getNeedsToKnowAssertions() {
        List<NeedsToKnow> list = new ArrayList<NeedsToKnow>();
        for (Assertion assertion : getAssertions())
                if (assertion instanceof  NeedsToKnow) list.add( (NeedsToKnow) assertion);
        return list;
    }

    public List<CanAccess> getCanAccessAssertions() {
        List<CanAccess> list = new ArrayList<CanAccess>();
        for (Assertion assertion : getAssertions())
                if (assertion instanceof  CanAccess) list.add( (CanAccess) assertion);
        return list;
    }

    public boolean hasAccess( Contactable contactable ) {
        return false;
    }

    /**
     * Return the value of expertise.
     */
    public List<Information> getExpertise() {
        return expertise;
    }

    
    /**
     * Set the value of expertise.
     * @param expertise The new value of expertise
     */
    public void setExpertise( List<Information> expertise ) {
        this.expertise = expertise;
    }
    
    public void addExpertise(Information information) {
        expertise.add( information );
    }
    
    public void removeExpertise(Information information) {
        expertise.remove( information );
    }

}
