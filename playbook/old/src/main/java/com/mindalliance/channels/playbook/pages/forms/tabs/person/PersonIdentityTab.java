package com.mindalliance.channels.playbook.pages.forms.tabs.person;

import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceIdentityTab;
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

    private static final long serialVersionUID = 1113046201776255358L;

    public PersonIdentityTab( String id, AbstractElementForm elementForm ) {
        super( id, elementForm );
    }

    protected void load() {
        super.load();
        // first name
        TextField<String> firstNameField = new TextField<String>(
                "firstName",
                new RefPropertyModel<String>( getElement(), "firstName" ) );
        addInputField( firstNameField, nameField );
        
        // middle name
        TextField<String> middleNameField = new TextField<String>(
                "middleName",
                new RefPropertyModel<String>( getElement(), "middleName" ) );
        addInputField( middleNameField, nameField );

        // last name
        TextField<String> lastNameField = new TextField<String>(
                "lastName",
                new RefPropertyModel<String>( getElement(), "lastName" ) );
        addInputField( lastNameField, nameField );
    }

    @Override
    protected void init() {
        super.init();
        nameField.setEnabled( false );
    }
}
