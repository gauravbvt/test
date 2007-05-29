// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor.components;

import java.util.IllegalFormatConversionException;

import org.zkoss.zul.Vbox;

import com.beanview.PropertyComponent;
import com.mindalliance.channels.data.support.LatLong;
import com.mindalliance.zk.beanview.ZkBeanViewPanel;


/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class LatLongEditor extends Vbox implements PropertyComponent {

    private ZkBeanViewPanel<LatLong> panel;
    
    public LatLongEditor() {
        panel = new ZkBeanViewPanel<LatLong>();
        appendChild(panel);
    }
    
    /* (non-Javadoc)
     * @see com.beanview.PropertyComponent#getValue()
     */
    public Object getValue() {
        return panel.getDataObject();
    }

    /* (non-Javadoc)
     * @see com.beanview.PropertyComponent#setValue(java.lang.Object)
     */
    public void setValue( Object arg0 ) throws IllegalFormatConversionException {
        if (arg0 == null) {
            panel.setDataObject( new LatLong() );
        } else {
            panel.setDataObject( arg0 );
        }
    }

}
