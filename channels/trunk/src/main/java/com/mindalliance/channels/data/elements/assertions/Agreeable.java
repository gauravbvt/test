/*
 * Created on May 2, 2007
 *
 */
package com.mindalliance.channels.data.elements.assertions;

import java.util.List;


/**
 * Can be agreed to
 * @author jf
 *
 */
public interface Agreeable extends Assertable {
	
	List<AgreedTo> getAgreedToAssertions();

}
