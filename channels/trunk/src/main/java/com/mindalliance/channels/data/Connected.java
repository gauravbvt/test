/*
 * Created on May 2, 2007
 *
 */
package com.mindalliance.channels.data;

import java.util.List;

import com.mindalliance.channels.data.elements.assertions.CanAccess;


/**
 * Has access to information resources.
 * @author jf
 *
 */
public interface Connected extends Assertable {
	/**
	 * Return CanAccess assertions
	 * @return
	 */
	List<CanAccess> getCanAccessAssertions();

}
