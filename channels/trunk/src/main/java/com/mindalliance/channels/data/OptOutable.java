/*
 * Created on May 1, 2007
 *
 */
package com.mindalliance.channels.data;

import java.util.List;

import com.mindalliance.channels.data.beans.OptedOut;

/**
 * Can be opted out of by roles, teams and persons so that it does not apply to them.
 * @author jf
 *
 */
public interface OptOutable extends Assertable {

	List<OptedOut> getOptedOutAssertions();
	
}
