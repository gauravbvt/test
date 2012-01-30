package com.mindalliance.channels.playbook.pages.forms.tabs.resource;

import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 23, 2008
 * Time: 5:19:20 PM
 */
public class ResourceIdentityTab extends AbstractFormTab {

    private static final long serialVersionUID = 2788062768258451499L;
    protected TextField<String> nameField;

    public ResourceIdentityTab( String id, AbstractElementForm elementForm ) {
        super( id, elementForm );
    }

    @Override
    protected void load() {
        super.load();
        // name
        nameField = new TextField<String>(
            "name", new RefPropertyModel<String>( getElement(), "name" ) );
        addInputField( nameField );
        // description
        addInputField( new TextArea<String>(
            "description",
             new RefPropertyModel<String>( getElement(), "description" ) ) );
    }
}
