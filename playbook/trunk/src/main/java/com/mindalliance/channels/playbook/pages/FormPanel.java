package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * ...
 */
public class FormPanel extends Panel {

    public FormPanel( String id, IModel content ) {
        super( id, content );

        add( new Label( "form-details", new RefPropertyModel( content, "name" )) );
    }
}
