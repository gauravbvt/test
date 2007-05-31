// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor.components;

import org.zkoss.zul.Vbox;

import com.beanview.PropertyComponent;
import com.mindalliance.channels.data.support.Latency;
import com.mindalliance.channels.ui.editor.ElementBeanViewPanel;


/**
 * BeanView editor component for the Latency class.
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class LatencyEditor extends Vbox implements PropertyComponent {

    private ElementBeanViewPanel<Latency> panel;
    
    /**
     * 
     * Default constructor.
     */
    public LatencyEditor() {
        panel = new ElementBeanViewPanel<Latency>(Latency.class);
        panel.setSubView( new String[]{"minimum", "maximum", "average"}, false, false );
        this.appendChild(panel);
    }
    
    /**
     * Returns the Latency instance being edited.
     * @return the latency instance
     * @see com.beanview.PropertyComponent#getValue()
     */
    public Object getValue() {
        panel.updateObjectFromPanel();
        return panel.getDataObject();
    }

    /**
     * Sets the Latency instance to edit.
     * @param arg0 the instance to be edited
     * @see com.beanview.PropertyComponent#setValue(java.lang.Object)
     */
    public void setValue( Object arg0 ){
        panel.setDataObject( arg0 );
    }

}
