// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor.components;

import java.util.IllegalFormatConversionException;

import org.zkoss.zul.Vbox;

import com.beanview.PropertyComponent;
import com.mindalliance.channels.data.support.Latency;
import com.mindalliance.channels.ui.editor.ElementBeanViewPanel;


/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class LatencyEditor extends Vbox implements PropertyComponent {

    private ElementBeanViewPanel<Latency> panel;
    
    public LatencyEditor() {
        panel = new ElementBeanViewPanel<Latency>(Latency.class);
        panel.setSubView( new String[]{"minimum", "maximum", "average"}, false, false );
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
        panel.setDataObject( arg0 );
    }

}
