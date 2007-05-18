/*
 * Created on May 2, 2007
 */
package com.mindalliance.channels.data;

import java.util.List;

import com.mindalliance.channels.data.elements.resources.Role;

/**
 * An actor is a role or it implies one or more roles.
 * 
 * @author jf
 */
public interface Actor extends Element, Knowledgeable, Needy, Connected, Accessible {

    /**
     * Get the roles implied by the actor.
     * 
     * @return
     */
    List<Role> getRoles();

}
