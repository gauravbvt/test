/*
 * Created on May 2, 2007
 *
 */
package com.mindalliance.channels.data.elements;

import java.util.List;

import com.mindalliance.channels.data.Caused;
import com.mindalliance.channels.data.Knowable;
import com.mindalliance.channels.data.Storable;
import com.mindalliance.channels.data.beans.Known;
import com.mindalliance.channels.data.beans.StoredIn;
import com.mindalliance.channels.data.beans.TaskCause;

abstract public class Product extends AbstractScenarioElement implements Caused, Knowable, Storable {
	
	/* (non-Javadoc)
	 * @see com.mindalliance.channels.data.Storable#getStoredInAssertions()
	 */
	private TaskCause taskCause;
	private List<Known> knownAssertions;
	private List<StoredIn> storedInAssertions;

	public List<StoredIn> getStoredInAssertions() {
		return storedInAssertions;
	}

	/* (non-Javadoc)
	 * @see com.mindalliance.channels.data.Knowable#getKnownAssertions()
	 */
	public List<Known> getKnownAssertions() {
		return knownAssertions;
	}

	public TaskCause getCause() {
		return taskCause;
	}

}
