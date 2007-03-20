// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import java.util.SortedSet;
import java.util.TreeSet;

import com.mindalliance.channels.project.Domain;
import com.mindalliance.channels.util.GUID;

/**
 * A coherent set of possibly cross-domain objectives shared by
 * various organizations.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class Mission extends AbstractNamedObject {

    private SortedSet<Domain> domains = new TreeSet<Domain>();
    private String objective;

    /**
     * Default constructor.
     * @param guid the unique ID for this object
     */
    Mission( GUID guid ) {
        super( guid );
    }

    /**
     * Return the value of domains.
     */
    public SortedSet<Domain> getDomains() {
        return this.domains;
    }

    /**
     * Set the value of domains.
     * @param domains The new value of domains
     */
    public void setDomains( SortedSet<Domain> domains ) {
        this.domains = domains;
    }

    /**
     * Return the value of objective.
     */
    public String getObjective() {
        return this.objective;
    }

    /**
     * Set the value of objective.
     * @param objective The new value of objective
     */
    public void setObjective( String objective ) {
        this.objective = objective;
    }
}
