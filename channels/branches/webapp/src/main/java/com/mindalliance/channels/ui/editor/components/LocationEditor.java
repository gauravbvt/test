// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor.components;

import org.zkoss.zul.Vbox;

import com.beanview.PropertyComponent;
import com.mindalliance.channels.data.definitions.Location;
import com.mindalliance.channels.ui.editor.ElementBeanViewPanel;


/**
 * A BeanView editor for the Location class.
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class LocationEditor extends Vbox implements PropertyComponent {

    private ElementBeanViewPanel<Location> panel;
    
    /**
     * 
     * Default constructor.
     */
    public LocationEditor() {
        panel = new ElementBeanViewPanel<Location>(Location.class);
        appendChild(panel);
    }
    
    /**
     * Returns the Location instance to be edited.
     * @return the instance
     * @see com.beanview.PropertyComponent#getValue()
     */
    public Object getValue() {
        panel.updateObjectFromPanel();
        return panel.getDataObject();
    }

    /**
     * Sets the Location instance to be edited.
     * @param arg0 the instance
     * @see com.beanview.PropertyComponent#setValue(java.lang.Object)
     */
    public void setValue( Object arg0 ) {
        if (arg0 == null) {
            panel.setDataObject( new Location() );
        } else {
            panel.setDataObject( arg0 );
        }
    }

}
