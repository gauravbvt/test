/*
 * Created on May 1, 2007
 *
 */
package com.mindalliance.channels.data.elements.resources;

import com.mindalliance.channels.data.components.Information;
import com.mindalliance.channels.data.elements.Element;

/**
 * Someone who may know and/or need to know.
 * @author jf
 *
 */
public interface Knowledgeable extends Element {
		
	boolean knows(Information information);
	
	boolean needsToKnow(Information information);

}
