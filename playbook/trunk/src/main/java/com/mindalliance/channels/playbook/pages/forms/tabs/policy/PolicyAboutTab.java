package com.mindalliance.channels.playbook.pages.forms.tabs.policy;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ifm.project.environment.Policy;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 10, 2008
 * Time: 8:26:29 PM
 */
public class PolicyAboutTab extends AbstractFormTab {

    TextField nameField;
    TextArea descriptionField;
    CheckBox effectiveField;
    DropDownChoice edictChoice;

    public PolicyAboutTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        nameField = new TextField("name", new RefPropertyModel(getElement(), "name"));
        addInputField(nameField);
        descriptionField = new TextArea("description", new RefPropertyModel(getElement(), "description"));
        addInputField(descriptionField);
        effectiveField = new CheckBox("effective", new RefPropertyModel(getElement(), "effective"));
        addInputField(effectiveField);
        edictChoice = new DropDownChoice("edict", new RefPropertyModel(getElement(), "edict"), Policy.edictKinds);
        edictChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                String selectedEdict = edictChoice.getModelObjectAsString();
                RefUtils.set(getElement(), "edict", selectedEdict);
            }
        });
        addReplaceable(edictChoice);
    }
}
