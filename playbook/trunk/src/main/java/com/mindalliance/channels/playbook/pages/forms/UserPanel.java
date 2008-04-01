package com.mindalliance.channels.playbook.pages.forms;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.ajax.AjaxRequestTarget;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 1, 2008
 * Time: 1:35:16 AM
 */
public class UserPanel  extends AbstractElementPanel {
    TextField nameField;
    TextField passwordField;   // TODO -- find out why TextArea does not work

    public UserPanel(String id, Ref p) {
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
        passwordField = new TextField("password", new RefPropertyModel(element, "password"));
        addElementField(nameField);
        addElementField(passwordField);
    }

    @Override
     public void refresh(AjaxRequestTarget target) {
        super.refresh(target);
        element.changed("name"); // forces an immediate persist to session
    }

}
