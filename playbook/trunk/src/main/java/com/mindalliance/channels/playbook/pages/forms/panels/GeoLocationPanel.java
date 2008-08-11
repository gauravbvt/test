package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 22, 2008
 * Time: 12:51:07 PM
 */
public class GeoLocationPanel extends AbstractComponentPanel {

    private static final long serialVersionUID = 3434516113648748749L;

    public GeoLocationPanel(
            String id, AbstractPlaybookPanel parentPanel, String propPath ) {
        super( id, parentPanel, propPath );
    }

    @Override
    protected void load() {
        super.load();
        addReplaceable(
                new AreaInfoPanel(
                        "areaInfo", this, propPath + ".areaInfo" ) );
        addReplaceable(
                new LatLongPanel(
                        "latLong", this, propPath + ".latLong" ) );
    }
}
