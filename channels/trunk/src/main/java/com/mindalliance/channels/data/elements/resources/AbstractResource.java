/*
 * Created on Apr 30, 2007
 *
 */
package com.mindalliance.channels.data.elements.resources;

import com.mindalliance.channels.data.components.Information;
import com.mindalliance.channels.data.elements.AbstractElement;
import com.mindalliance.channels.data.elements.analysis.Situation;

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
