package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.AbstractComponentPanel;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.info.LocationInfo;
import org.apache.wicket.ajax.AjaxRequestTarget;
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

    public LocationInfoPanel(String id, Ref element, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, element, propPath, readOnly, feedback);
    }


    protected void load() {
        super.load();
        areaInfoPanel = new AreaInfoPanel("areaInfo", element, propPath + ".areaInfo", readOnly, feedback);
        addReplaceable(areaInfoPanel);
        latLongPanel = new LatLongPanel("latLong", element, propPath + ".latLong", readOnly, feedback);
        addReplaceable(latLongPanel);
    }

}
