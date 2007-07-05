// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui.editor.components;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

import com.beanview.BeanView;
import com.beanview.PropertyComponent;
import com.beanview.PropertyComponentFactory;
import com.mindalliance.channels.User;
import com.mindalliance.channels.services.SystemService;
import com.mindalliance.channels.ui.editor.ElementBrowser;
import com.mindalliance.channels.util.CollectionType;

/**
 * Factory for BeanView components for Collection properties that
 * include the CollectionType annotation.
 * 
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class ElementCollectionFactory implements PropertyComponentFactory {

    /**
     * Default constructor.
     */
    public ElementCollectionFactory() {

    }

    /**
     * Generates a BeanView component for a collection annotated with
     * a CollectionType.
     * 
     * @param key the name of the field to generate the component for
     * @param type the type of the field (should extend Collection)
     * @param bv the beanview instance
     * @see com.beanview.PropertyComponentFactory#getComponent(java.lang.String,
     *      java.lang.Class, com.beanview.BeanView)
     */
    public PropertyComponent getComponent( String key, Class type, BeanView bv ) {
        PropertyComponent result = null;
        if ( Collection.class.isAssignableFrom( type ) ) {
            Class parent = (Class) bv.getContext( "class" );
            try {
                // We need to extract the CollectionType annotation
                // for this field
                String methodName = "get" + key.substring( 0, 1 ).toUpperCase()
                        + key.substring( 1 );
                Method m = parent.getMethod( methodName, new Class[0] );
                Annotation[] a = m.getAnnotations();
                CollectionType ct = m.getAnnotation( CollectionType.class );
                if ( ct != null ) {
                    result = new ElementBrowser( ct.type(), type,
                            (SystemService) bv.getContext( "system" ),
                            (User) bv.getContext( "user" ) );

                }
            } catch ( SecurityException e ) {
                // The appropriate method or annotation wasn't found
                // -- Fall through to the general Collection handler
                result = null;
            } catch ( NoSuchMethodException e ) {
                // The appropriate method or annotation wasn't found
                // -- Fall through to the general Collection handler
                result = null;
            }

        }
        return result;
    }

}
