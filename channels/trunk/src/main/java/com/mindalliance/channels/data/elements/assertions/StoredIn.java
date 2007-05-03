/*
 * Created on May 2, 2007
 *
 */
package com.mindalliance.channels.data.elements.assertions;

import com.mindalliance.channels.data.elements.resources.Repository;
import com.mindalliance.channels.util.Duration;

/**
 * Assertion about created knowledge or produced artefact being stored in a repository.
 * @author jf
 *
 */
public class StoredIn extends Assertion {
	
	private Repository repository; // Where it is stored
	private Duration delay; // average delay between creation and availability in repository

	public Storable getStorable() {
		return (Storable)getAbout();
	}
}
