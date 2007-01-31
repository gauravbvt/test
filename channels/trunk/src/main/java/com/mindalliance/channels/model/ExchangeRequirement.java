// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import com.mindalliance.channels.GUID;

/**
 * A partial or complete match between an information need and
 * information asset.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class ExchangeRequirement extends AbstractModelObject {

    private InformationAsset asset;
    private InformationNeed need;

    /**
     * Default constructor.
     * @param guid the unique ID for this object
     */
    ExchangeRequirement( GUID guid ) {
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
     * Return the value of need.
     */
    public InformationNeed getNeed() {
        return this.need;
    }

    /**
     * Set the value of need.
     * @param need The new value of need
     */
    public void setNeed( InformationNeed need ) {
        this.need = need;
    }
}
