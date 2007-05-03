/*
 * Created on May 2, 2007
 *
 */
package com.mindalliance.channels.data.elements.assertions;

import java.util.List;


/**
 * Has information needs.
 * @author jf
 *
 */
public interface Needy extends Assertable {
	
	List<NeedsToKnow> getNeedsToKnowAssertions();

}
