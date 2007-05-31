// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui.editor;

import java.util.IllegalFormatConversionException;

import com.beanview.PropertyComponent;
import com.mindalliance.channels.User;
import com.mindalliance.channels.services.SystemService;
import com.mindalliance.channels.ui.editor.picker.ElementPicker;
import com.mindalliance.channels.util.AbstractJavaBean;

/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class ElementChooser<T extends AbstractJavaBean> extends
        AbstractChooser<T, ElementPicker<T>> implements PropertyComponent {

    public ElementChooser( Class<T> c, SystemService system,
            User user ) {
        super( c, system, user );
        setDataObject( new ElementPicker<T>( c ) );
    }

    public T getSelected() {
        browser.updateObjectFromPanel();
        ElementPicker<T> picker = browser.getDataObject();
        return picker.getObject();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.beanview.PropertyComponent#getValue()
     */
    public Object getValue() {
        return getSelected();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.beanview.PropertyComponent#setValue(java.lang.Object)
     */
    public void setValue( Object obj ) throws IllegalFormatConversionException {
        ElementPicker<T> picker = browser.getDataObject();
        if (obj instanceof AbstractJavaBean || obj == null) {
            picker.setObject( (T)obj );
        } else {
            throw new IllegalFormatConversionException('c', obj.getClass());
        }
        browser.updatePanelFromObject();
    }
}
