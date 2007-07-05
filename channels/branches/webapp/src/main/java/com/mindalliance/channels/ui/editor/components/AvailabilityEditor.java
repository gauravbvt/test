// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor.components;

import java.util.IllegalFormatConversionException;

import org.zkoss.zul.Vbox;

import com.beanview.PropertyComponent;
import com.mindalliance.channels.data.support.Availability;
import com.mindalliance.channels.ui.editor.ElementBeanViewPanel;


/**
 * BeanView editor for availability instances.
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class AvailabilityEditor extends Vbox implements PropertyComponent {

    private ElementBeanViewPanel<Availability> panel;
    
    /**
     * 
     * Default constructor.
     */
    public AvailabilityEditor() {
        panel = new ElementBeanViewPanel<Availability>(Availability.class);
        this.appendChild(panel);
    }
    
    /**
     * Retrieves the edited Availability instance.
     * @see com.beanview.PropertyComponent#getValue()
     */
    public Object getValue() {
        panel.updateObjectFromPanel();
        return panel.getDataObject();
    }

    /**
     * Sets the Availability instance to be edited.
     * @param instance the Availability instance to be edited
     * @see com.beanview.PropertyComponent#setValue(java.lang.Object)
     */
    public void setValue( Object instance ) {
        Availability val;
        if (instance == null) {
            val = new Availability();
        } else if (instance instanceof Availability) {
            val = (Availability)instance;
        } else {
            throw new IllegalFormatConversionException('d', Availability.class);
        }
        panel.setDataObject(val);
    }

}
