/*
 * Created on May 3, 2007
 *
 */
package com.mindalliance.channels.data.user;

import com.mindalliance.channels.util.GUID;

/**
 * Request to the proper authority to resolve a conflict about some element. 
 * @author jf
 *
 */
public class ResolutionRequest extends UserRequest {

	private GUID elementGUID;
	private boolean escalate; // indicates that prior resolution requests about the same element were not addressed to satisfaction.
}
