/*
 * Created on May 1, 2007
 *
 */
package com.mindalliance.channels.data.elements.assertions;

import java.util.List;


/**
 * Can be opted out of by roles, teams and persons so that it does not apply to them.
 * @author jf
 *
 */
public interface OptOutable extends Assertable {

	List<OptedOut> getOptedOutAssertions();
	
}
