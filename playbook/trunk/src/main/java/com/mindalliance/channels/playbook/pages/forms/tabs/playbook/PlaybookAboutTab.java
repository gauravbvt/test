package com.mindalliance.channels.playbook.pages.forms.tabs.playbook;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.TextArea;

import java.util.List;
import java.io.Serializable;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 10, 2008
 * Time: 5:08:12 PM
 */
public class PlaybookAboutTab extends AbstractFormTab {

    TextField nameField;
    TextArea descriptionField;

    public PlaybookAboutTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        // name
        nameField = new TextField("name", new RefPropertyModel(getElement(), "name"));
        addInputField(nameField);
        // description
        descriptionField = new TextArea("description", new RefPropertyModel(getElement(), "description"));
        addInputField(descriptionField);
    }}
