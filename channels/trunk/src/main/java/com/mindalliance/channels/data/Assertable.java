/*
 * Created on May 1, 2007
 *
 */
package com.mindalliance.channels.data;

import java.util.List;

import com.mindalliance.channels.data.elements.assertions.Assertion;

/**
 * Something that can be the object of assertions.
 * @author jf
 *
 */
public interface Assertable {

	List<Assertion> getAssertions();

}
