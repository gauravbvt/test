/*
 * Created on May 2, 2007
 *
 */
package com.mindalliance.channels.data;

import java.util.List;

import com.mindalliance.channels.data.elements.Role;

/**
 * An actor is a role or it implies roles.
 * @author jf
 *
 */
public interface Actor extends Element {

	List<Role> getRoles();
	
}
