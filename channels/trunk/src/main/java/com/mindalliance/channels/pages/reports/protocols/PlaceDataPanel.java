package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.entities.PlaceData;

/**
 * Place data panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/26/12
 * Time: 3:56 PM
 */
public class PlaceDataPanel extends AbstractDataPanel {

    private PlaceData placeData;

    public PlaceDataPanel( String id, PlaceData placeData ) {
        super( id );
        this.placeData = placeData;
        init();
    }

    private void init() {
        // todo
    }
}
