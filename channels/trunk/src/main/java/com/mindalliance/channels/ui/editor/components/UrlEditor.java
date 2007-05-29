// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor.components;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.IllegalFormatConversionException;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Textbox;

import com.beanview.PropertyComponent;


/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class UrlEditor extends Hbox implements PropertyComponent {

    public UrlEditor() {
        box = new Textbox();
        appendChild(box);
    }
    
    private Textbox box;
    
    public void setValue(Object obj) throws IllegalFormatConversionException {
        
        if (obj == null) {
            box.setText( "" );
        } else if (obj instanceof URL) {
            box.setText( obj.toString() );
        } else {
            throw new IllegalFormatConversionException('u', URL.class);
        }
    }
    
    
    public Object getValue() {
        try {
            if (box.getText() == null) {
                return null;
            }
            return new URL(box.getText());
        } catch ( Exception e ) {
            return null;
        }
    }
}
