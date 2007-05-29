// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor.components;

import java.util.IllegalFormatConversionException;

import org.zkoss.zul.Vbox;

import com.beanview.PropertyComponent;
import com.mindalliance.channels.data.reference.Location;
import com.mindalliance.channels.ui.editor.ElementBeanViewPanel;


/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class LocationEditor extends Vbox implements PropertyComponent {

    private ElementBeanViewPanel<Location> panel;
    
    public LocationEditor() {
        panel = new ElementBeanViewPanel<Location>(Location.class);
        appendChild(panel);
    }
    
    /* (non-Javadoc)
     * @see com.beanview.PropertyComponent#getValue()
     */
    public Object getValue() {
        panel.updateObjectFromPanel();
        return panel.getDataObject();
    }

    /* (non-Javadoc)
     * @see com.beanview.PropertyComponent#setValue(java.lang.Object)
     */
    public void setValue( Object arg0 ) throws IllegalFormatConversionException {
        if (arg0 == null) {
            panel.setDataObject( new Location() );
        } else {
            panel.setDataObject( arg0 );
        }
    }

}
