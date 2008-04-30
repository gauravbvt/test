package com.mindalliance.channels.playbook.pages.forms;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.Component;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ifm.project.resources.Organization;
import com.mindalliance.channels.playbook.ifm.project.Project;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 31, 2008
 * Time: 2:49:58 PM
 */
public class SystemPanel extends AbstractElementPanel {
    TextField nameField;
    TextArea descriptionField;
    TextArea accessField;
    AutoCompleteTextField organizationField;

    public SystemPanel(String id, Ref p) {
        super(id, p);
    }

    @Override
    protected void init() {
        super.init();
        // Anything else?
    }

    @Override
    protected void load() {
        super.load();
        nameField = new TextField("name", new RefPropertyModel(element, "name"));
        nameField.setRequired(true);
        descriptionField = new TextArea("description", new RefPropertyModel(element, "description"));
        accessField = new TextArea("access", new RefPropertyModel(element, "access"));
        String orgName = (String)RefUtils.getOrDefault(element, "organization.name", "");
        organizationField = new AutoCompleteTextField("organization", new Model(orgName)) {
            protected Iterator getChoices(String input) {
                List<String> orgNames = Organization.findAllOrganizationNames();
                List<String>choices = new ArrayList<String>();
                for (String orgName : orgNames) {
                    if (orgName.toLowerCase().startsWith(input.toLowerCase())) {
                        choices.add(orgName);
                    }
                }
                return choices.iterator();
            }
        };
        organizationField.setOutputMarkupId(true);
        addElementField(nameField);
        addElementField(descriptionField);
        addElementField(organizationField);
        addElementField(accessField);
    }



    protected void updatedField(Component component, AjaxRequestTarget target) {
        if (component == organizationField) {
            String orgName = valueOf(organizationField);
            Project project = (Project)Project.current().deref();
            Ref organization = project.findResourceNamed("Organization", orgName);
            if (organization != null) {
                RefUtils.set(element, "organization", organization);
            }
        }
    }



}
