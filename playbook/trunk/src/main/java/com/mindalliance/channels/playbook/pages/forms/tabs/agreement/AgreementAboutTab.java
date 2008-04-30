package com.mindalliance.channels.playbook.pages.forms.tabs.agreement;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.panels.TimingPanel;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.ifm.project.environment.Agreement;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.basic.Label;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 29, 2008
 * Time: 2:04:38 PM
 */
public class AgreementAboutTab  extends AbstractFormTab {

    CheckBox inEffectField;
    TextArea descriptionField;
    RadioChoice deliveryField;
    TimingPanel timingPanel;

    public AgreementAboutTab(String id, Ref element) {
        super(id, element);
    }

    protected void load() {
        super.load();
        inEffectField = new CheckBox("effective", new RefPropertyModel(element, "effective"));
        addInputField(inEffectField);
        descriptionField = new TextArea("description", new RefPropertyModel(element, "description"));
        addInputField(descriptionField);
        Label fromResourceLabel = new Label("fromResource", new RefPropertyModel(element, "fromResource.name"));
        add(fromResourceLabel);
        deliveryField = new RadioChoice("delivery", new RefPropertyModel(element, "delivery"), Agreement.getDeliveries());
        addInputField(deliveryField);
        Label toResourceLabel = new Label("toResource", new RefPropertyModel(element, "toResource.name"));
        add(toResourceLabel);
        timingPanel = new TimingPanel("timing", element, "timing", EDITABLE, feedback);
        addReplaceable(timingPanel);
    }
}
