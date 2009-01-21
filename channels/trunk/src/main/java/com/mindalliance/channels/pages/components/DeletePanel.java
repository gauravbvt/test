package com.mindalliance.channels.pages.components;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import com.mindalliance.channels.Deletable;

/**
 * Checkbox on a deletable
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 21, 2009
 * Time: 1:34:32 PM
 */
public class DeletePanel extends Panel {

    /**
     * The deletable
     */
    private Deletable deletable;

    public DeletePanel( String id, IModel<Deletable> model ) {
        super( id, model );
        this.deletable = model.getObject();
        init();
    }

    private void init() {
        CheckBox checkBox = new CheckBox( "check", new PropertyModel<Boolean>( deletable, "markedForDeletion" ));
        add( checkBox );
    }

}
