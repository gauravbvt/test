package com.mindalliance.channels.playbook.pages.forms.tabs.user;

import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 24, 2008
 * Time: 10:33:02 AM
 */
public class UserAboutTab extends AbstractFormTab {

    TextField idField;
    TextField nameField;
    PasswordTextField passwordField;
    CheckBox managerField;
    CheckBox analystField;
    CheckBox adminField;

    public UserAboutTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        // id
        idField = new TextField("id", new RefPropertyModel(getElement(), "userId"));
        addInputField(idField);
        // name
        nameField = new TextField("name", new RefPropertyModel(getElement(), "name"));
        addInputField(nameField);
        // password
        passwordField = new PasswordTextField("password", new RefPropertyModel(getElement(), "password"));
        addInputField(passwordField);
        // privileges
        managerField = new CheckBox("manager", new RefPropertyModel(getElement(), "manager"));
        addInputField(managerField);
        analystField = new CheckBox("analyst", new RefPropertyModel(getElement(), "analyst"));
        addInputField(analystField);
        adminField = new CheckBox("admin", new RefPropertyModel(getElement(), "admin"));
        addInputField(adminField);
    }
}
