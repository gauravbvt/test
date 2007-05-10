/*
 * Created on May 2, 2007
 *
 */
package com.mindalliance.channels.data.elements.assertions;

import com.mindalliance.channels.data.Storable;
import com.mindalliance.channels.data.elements.resources.Repository;
import com.mindalliance.channels.data.support.Duration;
import com.mindalliance.channels.util.GUID;

/**
 * Assertion about created knowledge or produced artefact being stored in a repository.
 * @author jf
 *
 */
public class StoredIn extends Assertion {
	
	private Repository repository; // Where it is stored
	private Duration delay; // average delay between creation and availability in repository
	
	
	public StoredIn() {
		super();
	}

	public StoredIn(GUID guid) {
		super(guid);
	}

	/**
	 * Return the Sotrable target of the assertion
	 * @return
	 */
	public Storable getStorable() {
		return (Storable)getAbout();
	}

	/**
	 * @return the delay
	 */
	public Duration getDelay() {
		return delay;
	}

	/**
	 * @param delay the delay to set
	 */
	public void setDelay(Duration delay) {
		this.delay = delay;
	}

	/**
	 * @return the repository
	 */
	public Repository getRepository() {
		return repository;
	}

	/**
	 * @param repository the repository to set
	 */
	public void setRepository(Repository repository) {
		this.repository = repository;
	}
}
