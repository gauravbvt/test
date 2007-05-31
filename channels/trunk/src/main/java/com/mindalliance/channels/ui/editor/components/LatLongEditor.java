// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor.components;

import org.zkoss.zul.Vbox;

import com.beanview.PropertyComponent;
import com.mindalliance.channels.data.support.LatLong;
import com.mindalliance.zk.beanview.ZkBeanViewPanel;

/**
 * A BeanView editor component for the LatLong class.
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class LatLongEditor extends Vbox implements PropertyComponent {

    private ZkBeanViewPanel<LatLong> panel;
    
    /**
     * 
     * Default constructor.
     */
    public LatLongEditor() {
        panel = new ZkBeanViewPanel<LatLong>();
        appendChild(panel);
    }
    
    /**
     * Retrieves the LatLong instance being edited.
     * @return the instance
     * @see com.beanview.PropertyComponent#getValue()
     */
    public Object getValue() {
        return panel.getDataObject();
    }

    /**
     * Sets the LatLong instance to be edited.
     * @param arg0 the instance to be edited
     * @see com.beanview.PropertyComponent#setValue(java.lang.Object)
     */
    public void setValue( Object arg0 ) {
        if (arg0 == null) {
            panel.setDataObject( new LatLong() );
        } else {
            panel.setDataObject( arg0 );
        }
    }

}
