// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.pages.components;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.FormComponent;

/**
 * A form component that adds an "invalid" CSS style attribute to invalid components.
 */
public class ValidationStyler extends AbstractBehavior {

    /**
     * Create a new ValidStyler instance.
     */
    public ValidationStyler() {
    }

    /**
     * Add valid/invalid class tag, depending on validity.
     *
     * @param component the form component
     * @param tag attached html attributes
     */
    @Override
    public void onComponentTag( Component component, ComponentTag tag ) {
        FormComponent<?> comp = (FormComponent<?>) component;

        String value = comp.isValid() ? comp.getConvertedInput() == null ? null : "valid"
                                      : "invalid";

        if ( value != null )
            tag.put( "class", value );

        super.onComponentTag( component, tag );
    }
}
