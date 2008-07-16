package com.mindalliance.channels.playbook.support.components;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 15, 2008
 * Time: 9:28:34 PM
 */
public class NoFormButton extends Button {
    public NoFormButton(String id) {
        super(id);
    }

    public NoFormButton(String id, IModel model) {
        super(id, model);
    }

    public Form getForm() {
        return null;
    }
}
