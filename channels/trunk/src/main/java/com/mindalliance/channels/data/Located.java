/*
 * Created on Apr 25, 2007
 *
 */
package com.mindalliance.channels.data;

import com.mindalliance.channels.data.elements.reference.Location;

/**
 * Something that has a location.
 * @author jf
 *
 */
public interface Located {
	
	/**
	 * 
	 * @return a location
	 */
	Location getLocation();

}
