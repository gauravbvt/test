// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui.editor.picker;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import com.beanview.BeanView;
import com.mindalliance.channels.util.AbstractJavaBean;

/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public abstract class AbstractPicker<T extends AbstractJavaBean> {

    protected Class<T> c;
    protected PickerHelper pickerHelper;

    AbstractPicker(){}
    
    public AbstractPicker(Class<T> c) {
        this(c, new PickerHelper());
    }

    public AbstractPicker(Class<T> c, PickerHelper pickerHelper) {
        this.c = c;
        this.pickerHelper = pickerHelper;
    }

    /**
     * @param bean
     * @return
     */
    public Collection<T> findObjects( BeanView bean ) {
        Collection<T> result = new ArrayList<T>();

        try {
            Class[] paramTypes = { BeanView.class };
            Object[] args = new Object[] { bean };
            Method m = pickerHelper.getClass().getMethod(
                    "find" + c.getSimpleName(), paramTypes );
            result = (Collection<T>) m.invoke( pickerHelper, args );
        } catch ( Exception e ) {
            // Do nothing -- This class hasn't been mapped yet.
            // Return an empty list.
        }

        return result;
    }
}
