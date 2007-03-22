// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import com.mindalliance.channels.Model;

/**
 * A partial or complete match between a Know
 * and a Need to know determining an exchange of information.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 *
 * @navassoc - needToKnow 1 NeedsToKnow
 * @navassoc - know       1 Knows
 */
public class Exchange extends ModelElement {

    private NeedsToKnow needToKnow;
    private Knows know;

    /**
     * Default constructor.
     */
    Exchange() {
        super();
    }

    /**
     * Convenience constructor.
     * @param model the model
     */
    public Exchange( Model model ) {
        super( model );
    }

    /**
     * Return the value of need.
     */
    public NeedsToKnow getNeedToKnow() {
        return this.needToKnow;
    }

    /**
     * Set the value of need.
     * @param need The new value of need
     */
    public void setNeedToKnow( NeedsToKnow need ) {
        this.needToKnow = need;
    }

    /**
     * Return the value of know.
     */
    public Knows getKnow() {
        return this.know;
    }

    /**
     * Set the value of know.
     * @param know The new value of know
     */
    public void setKnow( Knows know ) {
        this.know = know;
    }
}
