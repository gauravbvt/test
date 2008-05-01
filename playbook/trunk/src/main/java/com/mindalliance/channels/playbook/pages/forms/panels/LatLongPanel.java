package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.AbstractComponentPanel;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ifm.info.LatLong;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.validation.validator.NumberValidator;
import org.apache.log4j.Logger;

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

    public LatLongPanel(String id, Ref element, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, element, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        latLong = (LatLong) RefUtils.get(element, propPath);
        editableDiv = new WebMarkupContainer("editable");
        addReplaceable(editableDiv);
        readOnlyDiv = new WebMarkupContainer("readOnly");
        addReplaceable(readOnlyDiv);
        loadEditable();
        loadReadOnly();
        if (isReadOnly()) {
            editableDiv.setVisible(false);
        } else {
            readOnlyDiv.setVisible(false);
        }
    }

    private void loadEditable() {
        final TextField longitudeField = new TextField("longitude", new Model(latLong.getLongitude()));
        longitudeField.setType(Double.class);
        longitudeField.add(NumberValidator.minimum(-180.0));
        longitudeField.add(NumberValidator.maximum(180.0));
        longitudeField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                double value = 0.0;
                try {
                    value = new Double(longitudeField.getModelObjectAsString());
                }
                catch (NumberFormatException e) {
                    longitudeField.error("Not a number");
                    longitudeField.setModelObject(0);
                    target.addComponent(longitudeField);
                }
                latLong.setLongitude(value);
                elementChanged();
                target.addComponent(feedback);
            }

            @Override
            protected void onError(AjaxRequestTarget target, RuntimeException e) {
                Logger.getLogger(this.getClass()).error("Error updating " + longitudeField + ": " + e);
                longitudeField.clearInput();
                target.addComponent(longitudeField);
                target.addComponent(feedback);
            }
        });
        final TextField latitudeField = new TextField("latitude", new Model(latLong.getLatitude()));
        latitudeField.setType(Double.class);
        latitudeField.add(NumberValidator.minimum(-90.0));
        latitudeField.add(NumberValidator.maximum(90.0));
        latitudeField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                double value = 0.0;
                try {
                    value = new Double(latitudeField.getModelObjectAsString());
                }
                catch (NumberFormatException e) {
                    latitudeField.error("Not a number");
                    latitudeField.setModelObject(0);
                    target.addComponent(latitudeField);
                }
                latLong.setLatitude(value);
                elementChanged();
                target.addComponent(feedback);
            }

            @Override
            protected void onError(AjaxRequestTarget target, RuntimeException e) {
                Logger.getLogger(this.getClass()).error("Error updating " + latitudeField + ": " + e);
                latitudeField.clearInput();
                target.addComponent(latitudeField);
                target.addComponent(feedback);
            }
        });
        editableDiv.add(longitudeField);
        editableDiv.add(latitudeField);
    }

    private void loadReadOnly() {
        Label latLongLabel = new Label("latLongString", latLong.toString());
        readOnlyDiv.add(latLongLabel);
    }

}
