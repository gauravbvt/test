/*
 * Created on May 1, 2007
 *
 */
package com.mindalliance.channels.data.elements.assertions;

import java.util.List;


/**
 * Subject to policy regulations.
 * @author jf
 *
 */
public interface Regulatable extends Assertable {

	List<Regulated> getRegulatedAssertions();
	
}
