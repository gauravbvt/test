package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.panels.AbstractComponentPanel;
import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.ifm.info.LocationInfo;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 22, 2008
 * Time: 12:51:07 PM
 */
public class LocationInfoPanel extends AbstractComponentPanel {

    LocationInfo locationInfo;
    AreaInfoPanel areaInfoPanel;
    LatLongPanel latLongPanel;

    public LocationInfoPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }


    protected void load() {
        super.load();
        areaInfoPanel = new AreaInfoPanel("areaInfo", this, propPath + ".areaInfo", readOnly, feedback);
        addReplaceable(areaInfoPanel);
        latLongPanel = new LatLongPanel("latLong", this, propPath + ".latLong", readOnly, feedback);
        addReplaceable(latLongPanel);
    }

}
