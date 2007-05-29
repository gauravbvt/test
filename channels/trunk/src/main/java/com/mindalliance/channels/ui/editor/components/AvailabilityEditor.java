// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor.components;

import java.util.IllegalFormatConversionException;

import org.zkoss.zul.Vbox;

import com.beanview.PropertyComponent;
import com.mindalliance.channels.data.support.Availability;
import com.mindalliance.channels.ui.editor.ElementBeanViewPanel;


/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class AvailabilityEditor extends Vbox implements PropertyComponent {

    ElementBeanViewPanel<Availability> panel;
    
    
    public AvailabilityEditor() {
        panel = new ElementBeanViewPanel<Availability>(Availability.class);
        this.appendChild(panel);
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
        Availability val;
        if (arg0 == null) {
            val = new Availability();
        } else if (arg0 instanceof Availability) {
            val = (Availability)arg0;
        } else {
            throw new IllegalFormatConversionException('d', Availability.class);
        }
        panel.setDataObject(val);
    }

}
