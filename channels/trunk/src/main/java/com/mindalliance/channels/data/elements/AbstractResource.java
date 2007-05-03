/*
 * Created on Apr 30, 2007
 *
 */
package com.mindalliance.channels.data.elements;

import com.mindalliance.channels.data.Resource;
import com.mindalliance.channels.data.beans.Information;

public abstract class AbstractResource extends AbstractElement implements
		Resource {

	/* (non-Javadoc)
	 * @see com.mindalliance.channels.data.Describable#getDescriptor()
	 */
	public Information getDescriptor() {
		return null;
	}

	public boolean isOperational(Situation situation) {
		return false;
	}

	public boolean isOperational() {
		return false;
	}

}
