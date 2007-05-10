/*
 * Created on May 4, 2007
 *
 */
package com.mindalliance.channels.data.elements.scenario;

import com.mindalliance.channels.data.components.Cause;
import com.mindalliance.channels.util.GUID;

/**
 * A response is a sharing of information prompted by a request. 
 * Note that upon being responded, the recipient implicitly asserts a Known
 * on the Knowledge caused by the response.
 * @author jf
 *
 */
public class Response extends Communication {

	private Cause<Request> cause; // can not be null

	public Response() {
		super();
	}

	public Response(GUID guid) {
		super(guid);
	}

	public Cause<Request> getCause() {
		return cause;
	}

	/**
	 * @param cause the cause to set
	 */
	public void setCause(Cause cause) {
		this.cause = (Cause<Request>)cause;
	}

}
