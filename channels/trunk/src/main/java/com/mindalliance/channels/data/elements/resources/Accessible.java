/*
 * Created on May 3, 2007
 *
 */
package com.mindalliance.channels.data.elements.resources;


/**
 * A resource that controls access to itself.
 * @author jf
 *
 */
public interface Accessible extends Resource {

	boolean hasAccess(Contactable contactable);
	
}
