// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor.components;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.IllegalFormatConversionException;

import org.zkoss.zul.Hbox;
import org.zkoss.zul.Textbox;

import com.beanview.PropertyComponent;

/**
 * BeanView property editor for URL's.
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class UrlEditor extends Hbox implements PropertyComponent {

    private Textbox box;

    /**
     * Default constructor.
     */
    public UrlEditor() {
        box = new Textbox();
        appendChild( box );
    }

    /**
     * Sets the URL to edit.
     * @param obj the URL to edit
     */
    public void setValue( Object obj )  {

        if ( obj == null ) {
            box.setText( "" );
        } else if ( obj instanceof URL ) {
            box.setText( obj.toString() );
        } else {
            throw new IllegalFormatConversionException( 'u', URL.class );
        }
    }

    /**
     * Retrieves the edited URL.
     * @return the edited URL
     */
    public Object getValue() {

        try {
            URL result = null;
            if ( box.getText() != null ) {
                result = new URL( box.getText() );
            }
            return result;

        } catch ( MalformedURLException e ) {
            return null;
        }

    }
}
