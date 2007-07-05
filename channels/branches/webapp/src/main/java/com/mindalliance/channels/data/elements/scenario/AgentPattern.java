// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.elements.scenario;

import java.util.List;

import com.mindalliance.channels.data.OptOutable;
import com.mindalliance.channels.data.elements.resources.Actor;
import com.mindalliance.channels.data.elements.resources.Role;
import com.mindalliance.channels.data.reference.Pattern;
import com.mindalliance.channels.util.GUID;

/**
 * All matching roles in project scope execute the task as separate
 * activities.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class AgentPattern extends Agent implements OptOutable {

    private Pattern<Role> pattern;

    /**
     * Default constructor.
     */
    public AgentPattern() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public AgentPattern( GUID guid ) {
        super( guid );
    }

    /**
     * Return the matched roles.
     */
    public List<Role> getMatchedRoles() {
        return null;
    }

    /**
     * Get the actors.
     */
    @Override
    public List<Actor> getActors( ) {
        // TODO
        return null;
    }

    /**
     * Return the pattern.
     */
    public Pattern<Role> getPattern() {
        return pattern;
    }

    /**
     * Set the pattern.
     * @param pattern the pattern to set
     */
    public void setPattern( Pattern<Role> pattern ) {
        this.pattern = pattern;
    }

}
