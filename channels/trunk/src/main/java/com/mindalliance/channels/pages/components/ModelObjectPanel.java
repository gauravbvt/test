package com.mindalliance.channels.pages.components;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import com.mindalliance.channels.ModelObject;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 12, 2009
 * Time: 7:05:45 PM
 */
public class ModelObjectPanel extends Panel {

    protected ModelObject mo;

    public ModelObjectPanel( String id, IModel<? extends ModelObject> model ) {
        super( id, model );
        mo = model.getObject();
        init();
    }

    private void init() {
        Form moForm = new Form( "mo-form" );
        add( moForm );
        WebMarkupContainer moDetailsDiv = new WebMarkupContainer( "mo-details" );
        moForm.add( moDetailsDiv );
        moDetailsDiv.add(
                new TextField<String>( "name",                                            // NON-NLS
                        new PropertyModel<String>( mo, "name" ) ) );
        moDetailsDiv.add(
                new TextArea<String>( "description",                                      // NON-NLS
                        new PropertyModel<String>( mo, "description" ) ) );

    }

}
