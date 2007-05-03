/*
 * Created on May 2, 2007
 *
 */
package com.mindalliance.channels.data.elements.assertions;

import java.util.List;


/**
 * Has access to information resources.
 * @author jf
 *
 */
public interface Connected extends Assertable {
	
	List<CanAccess> getCanAccessAssertions();

}
