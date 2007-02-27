// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import com.mindalliance.channels.util.GUID;

/**
 * The acquisition at some point in time of new information then held
 * in custody.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class InformationGain extends AbstractNamedObject {

    private InformationAsset asset;
    private Duration delay;

    /**
     * Default constructor.
     * @param guid the unique ID for this object
     */
    InformationGain( GUID guid ) {
        super( guid );
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
