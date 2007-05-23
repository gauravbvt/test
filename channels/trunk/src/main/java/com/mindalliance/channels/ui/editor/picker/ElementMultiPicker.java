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
public class ElementMultiPicker<T extends AbstractJavaBean> extends AbstractPicker<T> {
    private Collection<T> objects;
    
    public ElementMultiPicker(Class<T> c) {
        super(c);
    }
    
    public ElementMultiPicker(Class<T> c, PickerHelper pickerHelper) {
        super(c, pickerHelper);
    }
    
    /**
     * 
     * @return
     */
    @PropertyOptions( options = ".findObjects" , label = " ")
    public Collection<T> getObjects() {
        return objects;
    }

    /**
     * 
     * @param obj
     */
    public void setObjects(Collection<T> obj) {
        objects = obj;
    }

    public Collection<T> findObjects(BeanView bv) {
        return super.findObjects(bv);
    }
}
