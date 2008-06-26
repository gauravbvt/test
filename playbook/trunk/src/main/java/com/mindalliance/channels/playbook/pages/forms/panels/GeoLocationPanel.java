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

    GeoLocation geoLocation;
    AjaxCheckBox undefinedCheckBox;
    WebMarkupContainer definedDiv;
    AreaInfoPanel areaInfoPanel;
    LatLongPanel latLongPanel;
    AreaInfo priorAreaInfo;
    LatLong priorLatLong;

    public GeoLocationPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }


    protected void load() {
        super.load();
        geoLocation = (GeoLocation)getComponent();
        priorAreaInfo = geoLocation.getAreaInfo();
        priorLatLong = geoLocation.getLatLong();
        undefinedCheckBox = new AjaxCheckBox("undefined", new Model((Boolean)geoLocation.isDefined())) {
            protected void onUpdate(AjaxRequestTarget target) {
                boolean undefined = (Boolean)undefinedCheckBox.getModelObject();
                if (undefined) {
                    priorAreaInfo = geoLocation.getAreaInfo();
                    priorLatLong = geoLocation.getLatLong();
                    RefUtils.set(getElement(), propPath+".latLong", new LatLong());
                    RefUtils.set(getElement(), propPath+".areaInfo", new AreaInfo());
                    latLongPanel.modelChanged();
                    areaInfoPanel.modelChanged();
                    hide(definedDiv);
                }
                else {
                    RefUtils.set(getElement(), propPath+".latLong", priorLatLong);
                    RefUtils.set(getElement(), propPath+".areaInfo", priorAreaInfo);
                    latLongPanel = new LatLongPanel("latLong", GeoLocationPanel.this, propPath + ".latLong", readOnly, feedback);
                    definedDiv.addOrReplace(latLongPanel);
                    areaInfoPanel = new AreaInfoPanel("areaInfo", GeoLocationPanel.this, propPath + ".areaInfo", readOnly, feedback);
                    definedDiv.addOrReplace(areaInfoPanel);
                    display(definedDiv);
                }
                target.addComponent(definedDiv);
            }
        };
        addReplaceable(undefinedCheckBox);
        definedDiv = new WebMarkupContainer("definedDiv");
        addReplaceable(definedDiv);
        areaInfoPanel = new AreaInfoPanel("areaInfo", this, propPath + ".areaInfo", readOnly, feedback);
        addReplaceableTo(areaInfoPanel, definedDiv);
        latLongPanel = new LatLongPanel("latLong", this, propPath + ".latLong", readOnly, feedback);
        addReplaceableTo(latLongPanel, definedDiv);
        if (geoLocation.isDefined()) {
            display(definedDiv);
        }
        else {
            hide(definedDiv);
        }
    }

}
