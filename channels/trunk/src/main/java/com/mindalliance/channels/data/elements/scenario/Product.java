/*
 * Created on May 2, 2007
 *
 */
package com.mindalliance.channels.data.elements.scenario;

import java.util.List;

import com.mindalliance.channels.data.Caused;
import com.mindalliance.channels.data.components.Cause;
import com.mindalliance.channels.data.elements.assertions.Storable;
import com.mindalliance.channels.data.elements.assertions.StoredIn;

abstract public class Product extends AbstractScenarioElement implements Caused, Storable {
	
	/* (non-Javadoc)
	 * @see com.mindalliance.channels.data.Storable#getStoredInAssertions()
	 */
	private Cause<Task> cause;

	/* (non-Javadoc)
	 * @see com.mindalliance.channels.data.Storable#getStoredInAssertions()
	 */
	public List<StoredIn> getStoredInAssertions() {
		return null;
	}

	public Cause<Task> getCause() {
		return (Cause<Task>)cause;
	}

}
