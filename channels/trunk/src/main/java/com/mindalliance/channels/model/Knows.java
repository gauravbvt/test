// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import com.mindalliance.channels.system.InformationAsset;
import com.mindalliance.channels.util.Duration;

/**
 * The acquisition at some point in time of new information then held
 * in custody.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 *
 * @opt attributes
 */
public class Knows extends Assertion {

    private InformationAsset asset;
    private Duration delay;

    /**
     * Default constructor.
     */
    Knows() {
        super();
    }

    /**
     * Return the value of asset.
     */
    public InformationAsset getAsset() {
        return this.asset;
    }

    /**
     * Set the value of asset.
     * @param asset The new value of asset
     */
    public void setAsset( InformationAsset asset ) {
        this.asset = asset;
    }

    /**
     * Return the value of delay.
     */
    public Duration getDelay() {
        return this.delay;
    }

    /**
     * Set the value of delay.
     * @param delay The new value of delay
     */
    public void setDelay( Duration delay ) {
        this.delay = delay;
    }
}
