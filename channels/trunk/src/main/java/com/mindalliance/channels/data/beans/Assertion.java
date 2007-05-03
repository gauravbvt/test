/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.beans;

import com.mindalliance.channels.data.Assertable;
import com.mindalliance.channels.util.AbstractJavaBean;

/**
 * An assertion about an element.
 * @author jf
 *
 */
abstract public class Assertion extends AbstractJavaBean {
	
	private Assertable about;
	
	public Assertable getAbout() {
		return about;
	}

}
