package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.panels.AbstractComponentPanel;
import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.ifm.info.GeoLocation;
import com.mindalliance.channels.playbook.ifm.info.AreaInfo;
import com.mindalliance.channels.playbook.ifm.info.LatLong;
import com.mindalliance.channels.playbook.support.RefUtils;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 22, 2008
 * Time: 12:51:07 PM
 */
public class GeoLocationPanel extends AbstractComponentPanel {

    AreaInfoPanel areaInfoPanel;
    LatLongPanel latLongPanel;

    public GeoLocationPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
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
