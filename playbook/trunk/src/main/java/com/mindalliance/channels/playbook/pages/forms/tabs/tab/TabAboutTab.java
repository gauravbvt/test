package com.mindalliance.channels.playbook.pages.forms.tabs.tab;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.CheckBox;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 24, 2008
 * Time: 2:00:47 PM
 */
public class TabAboutTab  extends AbstractFormTab {

    TextField nameField;
    TextArea descriptionField;
    CheckBox sharedField;

    public TabAboutTab(String id, Ref element) {
        super(id, element);
    }

    protected void load() {
        super.load();
        // name
        nameField = new TextField("name", new RefPropertyModel(element, "name"));
        addInputField(nameField);
        // description
        descriptionField = new TextArea("description", new RefPropertyModel(element, "description"));
        addInputField(descriptionField);
        // shared
        sharedField = new CheckBox("shared", new RefPropertyModel(element, "shared"));
        addInputField(sharedField);
    }
}
