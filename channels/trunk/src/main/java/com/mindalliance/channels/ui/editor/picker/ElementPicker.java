// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui.editor.picker;

import java.util.Collection;

import com.beanview.BeanView;
import com.beanview.annotation.PropertyOptions;
import com.mindalliance.channels.util.AbstractJavaBean;

/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class ElementPicker<T extends AbstractJavaBean> extends AbstractPicker<T> {

    private T object;
    
    public ElementPicker(Class<T> c) {
        super(c);
    }
    
    public ElementPicker(Class<T> c, PickerHelper pickerHelper) {
        super(c, pickerHelper);
    }
    
    /**
     * @return
     */
    @PropertyOptions( options = ".findObjects", label = " ")
    public T getObject() {

        return object;
    }

    /**
     * @param obj
     */
    public void setObject( T obj ) {
        object = obj;
    }

    public Collection<T> findObjects(BeanView bv) {
        return super.findObjects(bv);
    }
    
}
