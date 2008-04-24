package com.mindalliance.channels.playbook.pages.forms.tabs.user;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.CheckBox;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 24, 2008
 * Time: 10:33:02 AM
 */
public class UserAboutTab extends AbstractFormTab {

    TextField nameField;
    TextField passwordField;
    CheckBox managerField;
    CheckBox analystField;
    CheckBox adminField;

    public UserAboutTab(String id, Ref element) {
        super(id, element);
    }

    protected void load() {
        super.load();
        // name
        nameField = new TextField("name", new RefPropertyModel(element, "name"));
        addInputField(nameField);
        // password
        passwordField = new TextField("password", new RefPropertyModel(element, "password"));
        addInputField(passwordField);
        // privileges
        managerField = new CheckBox("manager", new RefPropertyModel(element, "manager"));
        addInputField(managerField);
        analystField = new CheckBox("analyst", new RefPropertyModel(element, "analyst"));
        addInputField(analystField);
        adminField = new CheckBox("admin", new RefPropertyModel(element, "admin"));
        addInputField(adminField);
    }
}
