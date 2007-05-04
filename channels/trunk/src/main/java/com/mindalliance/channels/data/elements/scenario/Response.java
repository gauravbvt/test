/*
 * Created on May 4, 2007
 *
 */
package com.mindalliance.channels.data.elements.scenario;

import com.mindalliance.channels.data.components.Cause;

/**
 * A response is a sharing of information prompted by a request. 
 * Note that upon being responded, the recipient implicitly asserts a Known
 * on the Knowledge caused by the response.
 * @author jf
 *
 */
public class Response extends Communication {

	private Cause<Request> cause; // can not be null

	public Cause<Request> getCause() {
		return cause;
	}

}
