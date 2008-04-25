package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.AbstractComponentPanel;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ifm.info.LatLong;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.validation.validator.NumberValidator;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 22, 2008
 * Time: 9:00:53 PM
 */
public class LatLongPanel extends AbstractComponentPanel {

    WebMarkupContainer editableDiv;
    WebMarkupContainer readOnlyDiv;
    LatLong latLong;

    public LatLongPanel(String id, Ref element, String propPath, boolean readOnly) {
        super(id, element, propPath, readOnly);
    }

    public LatLongPanel(String id, Ref element, String propPath) {
        super(id, element, propPath);
    }
    
    protected void load() {
        super.load();
        latLong = (LatLong) RefUtils.get(element, propPath);
        editableDiv = new WebMarkupContainer("editable");
        addToPanel(editableDiv);
        readOnlyDiv = new WebMarkupContainer("readOnly");
        addToPanel(readOnlyDiv);
        loadEditable();
        loadReadOnly();
        if (isReadOnly()) {
            editableDiv.setVisible(false);
        }
        else {
            readOnlyDiv.setVisible(false);
        }
    }

    private void loadEditable() {
        final TextField longitudeField = new TextField("longitude", new Model("" + latLong.getLongitude()));
        longitudeField.add(NumberValidator.minimum(-180.0));
        longitudeField.add(NumberValidator.maximum(180.0));
        longitudeField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                double value = new Double(longitudeField.getModelObjectAsString());
                latLong.setLongitude(value);
                elementChanged();
            }
        });
        final TextField latitudeField = new TextField("latitude", new Model("" + latLong.getLatitude()));
        latitudeField.add(NumberValidator.minimum(-90.0));
        latitudeField.add(NumberValidator.maximum(90.0));
        latitudeField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                double value = new Double(latitudeField.getModelObjectAsString());
                latLong.setLatitude(value);
                elementChanged();
            }
        });        
        editableDiv.add(longitudeField);
        editableDiv.add(latitudeField);
    }

    private void loadReadOnly() {
        Label latLongLabel = new Label("latLongString", latLong.toString());
        readOnlyDiv.add(latLongLabel);
    }

    protected void init() {
        super.init();
        //
    }
    
}
