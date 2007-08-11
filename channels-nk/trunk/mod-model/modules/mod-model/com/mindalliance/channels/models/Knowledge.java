// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.models;

import com.mindalliance.channels.definitions.Information;
import com.mindalliance.channels.support.GUID;

/**
 * Information produced by a task. It may incorporate elements of information
 * needed and presumably received by the task's agents.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Knowledge extends Product {

    private Information information;

    /**
     * Default constructor.
     */
    public Knowledge() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Knowledge( GUID guid ) {
        super( guid );
    }

    /**
     * Return the information (what new information was created).
     * Information's contents aggregated from this knowledge's
     * expanded types (e.g. diagnosis, treatment, prognostic).
     */
    public Information getInformation() {
        return information;
    }

    /**
     * Set the information.
     * @param information the information
     */
    public void setInformation( Information information ) {
        this.information = information;
    }

}
