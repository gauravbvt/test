package com.mindalliance.channels.playbook.pages.forms.tabs.policy;

import com.mindalliance.channels.playbook.ifm.project.environment.Policy;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 10, 2008
 * Time: 8:26:29 PM
 */
public class PolicyAboutTab extends AbstractFormTab {

    private Policy policy;
    private TextField nameField;
    private TextArea descriptionField;
    private CheckBox effectiveField;
    private DropDownChoice edictChoice;
    private AjaxLink organizationLink;
    private Label organizationLabel;

    public PolicyAboutTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        policy = (Policy)getElement().deref();
        nameField = new TextField("name", new RefPropertyModel(getElement(), "name"));
        addInputField(nameField);
        descriptionField = new TextArea("description", new RefPropertyModel(getElement(), "description"));
        addInputField(descriptionField);

        organizationLink = new AjaxLink("organizationLink") {
            public void onClick(AjaxRequestTarget target) {
                edit(policy.getOrganization(), target);
            }
        };
        addReplaceable(organizationLink);
        String orgName = (String)RefUtils.get(getElement(), "organization.name");
        organizationLabel = new Label("organization", new Model<String>(orgName));
        organizationLink.add(organizationLabel);
        
        effectiveField = new CheckBox("effective", new RefPropertyModel(getElement(), "effective"));
        addInputField(effectiveField);
        edictChoice = new DropDownChoice("edict", new RefPropertyModel(getElement(), "edict"), Policy.getEdictKinds());
        edictChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                String selectedEdict = edictChoice.getDefaultModelObjectAsString();
                setProperty("edict", selectedEdict);
            }
        });
        addReplaceable(edictChoice);
    }
}
