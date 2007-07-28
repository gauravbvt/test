// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.models;

import com.mindalliance.channels.data.profiles.Repository;
import com.mindalliance.channels.data.support.Duration;
import com.mindalliance.channels.data.support.GUID;

/**
 * Assertion about created knowledge or produced artefact being stored
 * in a repository.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class StoredIn extends Assertion {

    private Repository repository;
    private Duration delay;

    /**
     * Default constructor.
     */
    public StoredIn() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public StoredIn( GUID guid ) {
        super( guid );
    }

    /**
     * Return the average delay between creation and
     * availability in repository.
     */
    public Duration getDelay() {
        return delay;
    }

    /**
     * Set the delay.
     * @param delay the delay to set
     */
    public void setDelay( Duration delay ) {
        this.delay = delay;
    }

    /**
     * Return the repository.
     */
    public Repository getRepository() {
        return repository;
    }

    /**
     * Set the repository.
     * @param repository the repository to set
     */
    public void setRepository( Repository repository ) {
        this.repository = repository;
    }
}
