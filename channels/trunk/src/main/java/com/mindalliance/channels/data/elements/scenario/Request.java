/*
 * Created on May 4, 2007
 *
 */
package com.mindalliance.channels.data.elements.scenario;

import com.mindalliance.channels.data.components.Cause;

/**
 * A request is a prompt to share information. A request can be
 * passed along through intermediates.
 * @author jf
 *
 */
public class Request extends Communication {

	private Cause<Request> cause; // if not null then a request is being passed along

	public Cause<Request> getCause() {
		return cause;
	}

}
