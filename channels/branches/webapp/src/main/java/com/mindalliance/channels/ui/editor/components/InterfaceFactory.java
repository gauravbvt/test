// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor.components;

import java.lang.reflect.Modifier;

import com.beanview.BeanView;
import com.beanview.PropertyComponent;
import com.beanview.PropertyComponentFactory;
import com.mindalliance.channels.User;
import com.mindalliance.channels.data.system.SystemService;
import com.mindalliance.channels.data.system.UserImpl;


/**
 * Generates BeanView components for to Channels interface types.
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class InterfaceFactory implements PropertyComponentFactory {
    /**
     * Default constructor.
     */
    public InterfaceFactory() {
        
    }
    
    /**
     * This implementation of getComponent should test for interface types.
     * @see com.beanview.PropertyComponentFactory#getComponent(java.lang.String, java.lang.Class, com.beanview.BeanView)
     */
    public PropertyComponent getComponent( String key, Class type,
            BeanView bv ) {
        if (User.class == type) {
            return new SingleElementBrowser(UserImpl.class, (SystemService)bv.getContext( "system"), (User)bv.getContext( "user" ));
        } else if ((Modifier.isAbstract(type.getModifiers()) || type.isInterface() ) 
                && !type.isPrimitive()) {
            return new SingleElementBrowser(type, (SystemService)bv.getContext( "system"), (User)bv.getContext( "user" ) );
        }
            
            
        
        return null;
    }

}
