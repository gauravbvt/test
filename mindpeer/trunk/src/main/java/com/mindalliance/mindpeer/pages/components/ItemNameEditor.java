// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.pages.components;

import com.mindalliance.mindpeer.model.NamedModelObject;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

/**
 * A list item that doubles as a name editor for an underlying object.
 * @param <T> the actual type of underlying object
 */
public abstract class ItemNameEditor<T extends NamedModelObject> extends Panel {

    private static final long serialVersionUID = 7764755437755357647L;

    /**
     * Create a new ItemNameEditor instance.
     *
     * @param id the given id
     * @param model the given model
     */
    public ItemNameEditor( String id, IModel<T> model ) {
        super( id, model );
        setRenderBodyOnly( true );

        add( new Form<T>( "form", new CompoundPropertyModel<T>( model ) ) {
            private static final long serialVersionUID = 141676435936030267L;
            private String oldName;

            /**
             * Called just after a component is rendered.
             */
            @Override
            protected void onAfterRender() {
                oldName = getModelObject().getName();
                super.onAfterRender();
            }

            @Override
            protected void onSubmit() {
                super.onSubmit();
                save( oldName, getModelObject() );
            }
        }
                .add( new TextField<String>( "name" ) ) );
    }

    /**
     * Save the edited object when form is submitted.
     * Name has already been set.
     *
     * @param oldName the name before the change
     * @param o the edited object
     */
    public abstract void save( String oldName, T o );
}
