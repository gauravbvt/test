/*
 * Created on Apr 25, 2007
 *
 */
package com.mindalliance.channels.data;

import com.mindalliance.channels.data.elements.Situation;

/**
 * Something that may or may not be operational by default or in a given situation.
 * @author jf
 *
 */
public interface Resource extends Element, Describable {
	
	/**
	 * 
	 * @param situation
	 * @return whether the resource is operational in a given situation.
	 */
	boolean isOperational(Situation situation);
	
	/**
	 * 
	 * @return whether the resource is operation by default.
	 */
	boolean isOperational();

}
