/*
 * Created on May 7, 2007
 *
 */
package com.mindalliance.channels.data.elements.assertions;

import java.util.List;

/**
 * Something which can not co-exist with something else.
 * @author jf
 *
 */
public interface Excludable extends Assertable {
	
	List<Excluded> getExcludedAssertions();

}
