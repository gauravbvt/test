// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor.browser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

import com.beanview.BeanView;
import com.beanview.PropertyComponent;
import com.beanview.PropertyComponentFactory;
import com.beanview.util.Configuration;
import com.beanview.util.FactoryResolver;
import com.mindalliance.channels.util.AbstractJavaBean;
import com.mindalliance.channels.util.CollectionType;


/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class BrowserFactory implements PropertyComponentFactory {

    /* (non-Javadoc)
     * @see com.beanview.PropertyComponentFactory#getComponent(java.lang.String, java.lang.Class, com.beanview.BeanView)
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
                        BrowserList result = new BrowserList();

                        Collection temp = new FactoryResolver().getValues(key, bv);
                        if (temp == null)
                            throw new IllegalArgumentException(
                                    "Unable to find factory for collection.");
                        result.setMultipleSelectOptions(new BrowserListModel(temp, type,
                                null, false));

                        //result.setSelectionMode(ListSelectionModel.MULTIPLE_SELECTION);
                        result.setMultiple(true);
                        Configuration configuration = new Configuration(bv);
                        result.setDisabled(!configuration.editable(key));

                        return result;

                    }
                } catch ( Exception e ) {
                    // The appropriate method or annotation wasn't found -- Fall through to the general Collection handler
                }
            }
        }
        return null;
    }



}
