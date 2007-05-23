// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IllegalFormatConversionException;

import com.beanview.PropertyComponent;
import com.mindalliance.channels.User;
import com.mindalliance.channels.data.elements.project.Scenario;
import com.mindalliance.channels.ui.editor.picker.ElementMultiPicker;
import com.mindalliance.channels.util.AbstractJavaBean;


/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class ElementMultiChooser<T extends AbstractJavaBean> extends AbstractChooser<T,ElementMultiPicker<T>> implements PropertyComponent {
    public ElementMultiChooser(Class<T> c, System system, Scenario scenario, User user) {
        super(c, system, scenario, user);
        setDataObject(new ElementMultiPicker<T>(c));
    }
    
    public Collection<T> getSelected() {
        browser.updateObjectFromPanel();
        ElementMultiPicker<T> picker = browser.getDataObject();
        return picker.getObjects();
    }

    /* (non-Javadoc)
     * @see com.beanview.PropertyComponent#getValue()
     */
    public Object getValue() {
        return getSelected();
    }

    /* (non-Javadoc)
     * @see com.beanview.PropertyComponent#setValue(java.lang.Object)
     */
    public void setValue( Object obj ) throws IllegalFormatConversionException {
        ElementMultiPicker<T> picker = browser.getDataObject();
        if (obj == null) {
            picker.setObjects( new ArrayList() );
        } else if (obj instanceof Collection) {
            picker.setObjects( (Collection<T>)obj );
        } else {
            throw new IllegalFormatConversionException('c', obj.getClass());
        }
        browser.updatePanelFromObject();
    }
    
    
}
