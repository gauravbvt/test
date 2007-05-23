// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui.editor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

import com.beanview.BeanView;
import com.beanview.PropertyComponent;
import com.beanview.PropertyComponentFactory;
import com.mindalliance.channels.User;
import com.mindalliance.channels.data.elements.project.Scenario;
import com.mindalliance.channels.util.AbstractJavaBean;
import com.mindalliance.channels.util.CollectionType;

/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class ElementCollectionFactory implements PropertyComponentFactory {

    /*
     * (non-Javadoc)
     * 
     * @see com.beanview.PropertyComponentFactory#getComponent(java.lang.String,
     *      java.lang.Class, com.beanview.BeanView)
     */
    public PropertyComponent getComponent( String key, Class type, BeanView bv ) {
        if ( Collection.class.isAssignableFrom( type ) ) {
            Class parent = (Class) bv.getContext( "class" );
            if ( AbstractJavaBean.class.isAssignableFrom( (Class) bv.getContext( "class" ) ) ) {
                try {
                    // We need to extract the CollectionType annotation
                    // for this field
                    String methodName = "get" + key.substring( 0, 1 ).toUpperCase()
                            + key.substring( 1 );
                    Method m = parent.getMethod( methodName, new Class[0] );
                    Annotation[] a = m.getAnnotations();
                    CollectionType ct = m.getAnnotation( CollectionType.class );
                    if ( ct != null ) {
                        return new ElementMultiChooser( ct.type(),
                                (System) bv.getContext( "system" ),
                                (Scenario) bv.getContext( "scenario" ),
                                (User) bv.getContext( "user" ) );

                    }
                } catch ( Exception e ) {
                    // The appropriate method or annotation wasn't found -- Fall through to the general Collection handler
                }
            }
        }
        return null;
    }

}
