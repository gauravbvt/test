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
import com.mindalliance.channels.playbook.ifm.context.environment.Organization;
import com.mindalliance.channels.playbook.ifm.project.Project;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import groovyjarjarantlr.collections.impl.LList;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 31, 2008
 * Time: 2:49:58 PM
 */
public class SystemPanel extends AbstractElementPanel {
    TextField nameField;
    TextField descriptionField;   // TODO -- find out why TextArea does not work
    TextField accessField;
    TextField organizationField;
    AutoCompleteTextField organizationPanel;

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
        descriptionField = new TextField("description", new RefPropertyModel(element, "description"));
        accessField = new TextField("access", new RefPropertyModel(element, "access"));
        organizationField = new AutoCompleteTextField("organization", new Model("")) {
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
        addElementField(nameField);
        addElementField(descriptionField);
        addElementField(organizationField);
        addElementField(accessField);
    }

    @Override
     public void refresh(AjaxRequestTarget target) {
        super.refresh(target);
        element.changed("name"); // forces an immediate persist to session
    }

    @Override
    protected void updatedField(Component component) {
        if (component == organizationField) {
            String orgName = valueOf(organizationField);
            Project project = (Project)Project.currentProject().deref();
            Ref organization = project.findResourceNamed("Organization", orgName);
            if (organization != null) {
                RefUtils.set(element, "organization", organization);
            }
        }
    }



}
