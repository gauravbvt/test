/*
 * Created on Apr 25, 2007
 *
 */
package com.mindalliance.channels.data;

/**
 * Something with a name. Anything with a name can be compared with another also with a name.
 * @author jf
 *
 */
public interface Named extends Comparable<Named> {

	/**
	 * Return a name
	 * @return
	 */
	String getName();
	
}
