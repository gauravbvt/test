package com.mindalliance.channels.playbook.pages.forms.tabs.agreement;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.panels.TimingPanel;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ifm.project.environment.Agreement;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.model.Model;

import java.util.List;
import java.io.Serializable;

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
    DropDownChoice deliveryField;
    TimingPanel timingPanel;

    Agreement agreement;

    public AgreementAboutTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        agreement = (Agreement)element.deref();
        inEffectField = new CheckBox("effective", new RefPropertyModel(element, "effective"));
        addInputField(inEffectField);
        descriptionField = new TextArea("description", new RefPropertyModel(element, "description"));
        addInputField(descriptionField);
        AjaxLink fromResourceLink = new AjaxLink("fromResourceLink") {
            public void onClick(AjaxRequestTarget target) {
                edit((Ref)RefUtils.get(element, "fromResource"), target);
            }
        };
        add(fromResourceLink);
        Label fromResourceLabel = new Label("fromResource", new RefPropertyModel(element, "fromResource.name"));
        fromResourceLink.add(fromResourceLabel);
        deliveryField = new DropDownChoice("delivery", new RefPropertyModel(agreement, "delivery"), Agreement.getDeliveries());
        addInputField(deliveryField);
        AjaxLink toResourceLink = new AjaxLink("toResourceLink") {
            public void onClick(AjaxRequestTarget target) {
                edit((Ref)RefUtils.get(element, "toResource"), target);
            }
        };
        add(toResourceLink);
        Label toResourceLabel = new Label("toResource", new RefPropertyModel(element, "toResource.name"));
        toResourceLink.add(toResourceLabel);
        timingPanel = new TimingPanel("timing", element, "maxDelay", EDITABLE, feedback);
        addReplaceable(timingPanel);
    }
}
