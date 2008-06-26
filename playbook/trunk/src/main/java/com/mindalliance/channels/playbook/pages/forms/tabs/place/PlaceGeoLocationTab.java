package com.mindalliance.channels.playbook.pages.forms.tabs.place;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.panels.GeoLocationPanel;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 10, 2008
 * Time: 7:42:11 PM
 */
public class PlaceGeoLocationTab extends AbstractFormTab {

    protected GeoLocationPanel geoLocationPanel;

    public PlaceGeoLocationTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        geoLocationPanel = new GeoLocationPanel("geoLocation", this, "geoLocation", false, feedback);
        addReplaceable(geoLocationPanel);
    }
}
