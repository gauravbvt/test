/*
 * Created on May 2, 2007
 *
 */
package com.mindalliance.channels.data;

import java.util.List;

import com.mindalliance.channels.data.beans.NeedsToKnow;

/**
 * Has information needs.
 * @author jf
 *
 */
public interface Needy extends Assertable {
	
	List<NeedsToKnow> getNeedsToKnowAssertions();

}
