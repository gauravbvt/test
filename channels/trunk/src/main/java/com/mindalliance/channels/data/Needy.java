/*
 * Created on May 2, 2007
 *
 */
package com.mindalliance.channels.data;

import java.util.List;

import com.mindalliance.channels.data.elements.assertions.NeedsToKnow;


/**
 * Has information needs.
 * @author jf
 *
 */
public interface Needy extends Assertable {
	/**
	 * Get NeedsToKnow assertions.
	 * @return
	 */
	List<NeedsToKnow> getNeedsToKnowAssertions();

}
