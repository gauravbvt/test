/*
 * Created on Apr 25, 2007
 *
 */
package com.mindalliance.channels.data;

import java.util.List;

import com.mindalliance.channels.data.beans.Known;


/**
 * Something in the real world that's knowable and thus about which information can be communicated.
 * @author jf
 *
 */
public interface Knowable extends Assertable {
		
	List<Known> getKnownAssertions();
	
}
