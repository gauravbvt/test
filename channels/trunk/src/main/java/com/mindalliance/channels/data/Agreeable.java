/*
 * Created on May 2, 2007
 *
 */
package com.mindalliance.channels.data;

import java.util.List;

import com.mindalliance.channels.data.beans.AgreedTo;

/**
 * Can be agreed to
 * @author jf
 *
 */
public interface Agreeable extends Assertable {
	
	List<AgreedTo> getAgreedToAssertions();

}
