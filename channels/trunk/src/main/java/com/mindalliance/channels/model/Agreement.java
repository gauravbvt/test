// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.mindalliance.channels.util.GUID;

/**
 * An agreement between two organizations to fulfill specified
 * information exchange requirements.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class Agreement extends AbstractModelObject {

    private SortedSet<Organization> parties = new TreeSet<Organization>();
    private List<Agreement> agreements = new ArrayList<Agreement>();

    /**
     * Default constructor.
     * @param guid the unique ID for this object
     */
    Agreement( GUID guid ) {
        super( guid );
    }

    /**
     * Return the value of agreements.
     */
    public List<Agreement> getAgreements() {
        return this.agreements;
    }

    /**
     * Set the value of agreements.
     * @param agreements The new value of agreements
     */
    public void setAgreements( List<Agreement> agreements ) {
        this.agreements = agreements;
    }

    /**
     * Return the value of parties.
     */
    public SortedSet<Organization> getParties() {
        return this.parties;
    }

    /**
     * Set the value of parties.
     * @param parties The new value of parties
     */
    public void setParties( SortedSet<Organization> parties ) {
        this.parties = parties;
    }
}
