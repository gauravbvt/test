package com.mindalliance.channels.playbook.pages.forms.tabs.person;

import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceIdentityTab;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.markup.html.form.TextField;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 24, 2008
 * Time: 3:25:39 PM
 */
public class PersonIdentityTab extends ResourceIdentityTab {

    TextField firstNameField;
    TextField middleNameField;
    TextField lastNameField;

    public PersonIdentityTab(String id, Ref element) {
        super(id, element);
    }

    protected void load() {
        super.load();
        // first name
        firstNameField = new TextField("firstName", new RefPropertyModel(element, "firstName"));
        addInputField(firstNameField, nameField);
        // middle name
        middleNameField = new TextField("middleName", new RefPropertyModel(element, "middleName"));
        addInputField(middleNameField, nameField);
        // last name
        lastNameField = new TextField("lastName", new RefPropertyModel(element, "lastName"));
        addInputField(lastNameField, nameField);
    }

    protected void init() {
        super.init();
        nameField.setEnabled(false);
    }
}
