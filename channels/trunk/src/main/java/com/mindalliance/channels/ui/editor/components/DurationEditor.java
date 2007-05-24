// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor.components;

import java.util.IllegalFormatConversionException;

import com.beanview.PropertyComponent;
import com.mindalliance.channels.data.support.Duration;
import com.mindalliance.zk.beanview.ZkBeanViewPanel;


/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class DurationEditor extends ZkBeanViewPanel<Duration> implements PropertyComponent {

    /* (non-Javadoc)
     * @see com.beanview.PropertyComponent#getValue()
     */
    public Object getValue() {
        updateObjectFromPanel();
        return getDataObject();
    }

    /* (non-Javadoc)
     * @see com.beanview.PropertyComponent#setValue(java.lang.Object)
     */
    public void setValue( Object arg0 ) throws IllegalFormatConversionException {
        if (!(arg0 instanceof Duration)) {
            throw new IllegalFormatConversionException('d', Duration.class);
        }
        setDataObject(arg0);
    }

}
