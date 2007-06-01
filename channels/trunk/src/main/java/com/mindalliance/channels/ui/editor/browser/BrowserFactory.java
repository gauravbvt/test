// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor.browser;

import java.util.Collection;

import com.beanview.BeanView;
import com.beanview.PropertyComponent;
import com.beanview.PropertyComponentFactory;
import com.beanview.util.Configuration;
import com.beanview.util.FactoryResolver;
import com.mindalliance.channels.User;
import com.mindalliance.channels.services.SystemService;
import com.mindalliance.channels.util.AbstractJavaBean;


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

                    ChooserBrowser browser = new ChooserBrowser(parent, (SystemService)bv.getContext("system"), (User)bv.getContext( "user" ));

                    Collection temp = new FactoryResolver().getValues(key, bv);
                    if (temp == null)
                        throw new IllegalArgumentException(
                                "Unable to find factory for collection.");
                    browser.setValue( temp );

                    Configuration configuration = new Configuration(bv);
                    //browser.setDisabled(!configuration.editable(key));

                    return browser;

                } catch ( Exception e ) {
                    // The appropriate method or annotation wasn't found -- Fall through to the general Collection handler
                }
            }
        }
        return null;
    }



}
