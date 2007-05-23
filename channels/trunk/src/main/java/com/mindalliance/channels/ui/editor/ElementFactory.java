// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor;

import com.beanview.BeanView;
import com.beanview.PropertyComponent;
import com.beanview.PropertyComponentFactory;
import com.mindalliance.channels.User;
import com.mindalliance.channels.data.elements.project.Scenario;
import com.mindalliance.channels.util.AbstractJavaBean;


/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class ElementFactory implements PropertyComponentFactory {

    /* (non-Javadoc)
     * @see com.beanview.PropertyComponentFactory#getComponent(java.lang.String, java.lang.Class, com.beanview.BeanView)
     */
    public PropertyComponent getComponent(String key, Class type,
            BeanView bv) {
        if (AbstractJavaBean.class.isAssignableFrom(type)) {
            return new ElementChooser(type, (System)bv.getContext( "system"), (Scenario)bv.getContext( "scenario" ), (User)bv.getContext( "user" ));
        }
        return null;
    }

}
