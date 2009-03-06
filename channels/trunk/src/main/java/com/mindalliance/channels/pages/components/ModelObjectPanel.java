package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.ModelObject;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 12, 2009
 * Time: 7:05:45 PM
 */
public class ModelObjectPanel extends Panel {
    /**
     * The model object being edited
     */
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
        addSpecifics( moDetailsDiv );
        moDetailsDiv.add( new AttachmentPanel( "attachments", new Model<ModelObject>( mo ) ) );

    }

    protected void addSpecifics( WebMarkupContainer moDetailsDiv ) {
        // do nothing
    }


}
