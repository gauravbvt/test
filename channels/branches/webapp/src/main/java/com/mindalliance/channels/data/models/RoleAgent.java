// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.models;

import java.util.ArrayList;
import java.util.List;

import com.beanview.annotation.PropertyOptions;
import com.mindalliance.channels.data.profiles.Actor;
import com.mindalliance.channels.data.profiles.Role;
import com.mindalliance.channels.data.support.GUID;

/**
 * One or more identical roles executing the task separately.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class RoleAgent extends Agent {

    private Role role;
    private Integer number;

    /**
     * Default constructor.
     */
    public RoleAgent() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public RoleAgent( GUID guid ) {
        super( guid );
    }

    /**
     * Overriden from ....
     * @see com.mindalliance.channels.data.models.Agent#getActors()
     */
    @Override
    @PropertyOptions( ignore = true )
    public List<Actor> getActors() {
        List<Actor> actors = new ArrayList<Actor>();
        actors.add( role );
        return actors;
    }

    /**
     * Return the number.
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * Set the number.
     * @param number the number
     */
    public void setNumber( Integer number ) {
        this.number = number;
    }

    /**
     * Return the role.
     */
    public Role getRole() {
        return role;
    }

    /**
     * Set the role.
     * @param role the role
     */
    public void setRole( Role role ) {
        this.role = role;
    }
}
