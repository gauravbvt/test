// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor.components;

import com.beanview.BeanView;
import com.mindalliance.zk.beanview.ZkComponentFactory;


/**
 * Extends the ZkComponentFactory to insert component factories specific to Channels.
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class ElementComponentFactory extends ZkComponentFactory {

    /**
     * 
     * Default constructor.
     * @param key the key to generate a factory for
     * @param c the type of the class to generate a factory for
     * @param bv the Beanview instance
     */
    public ElementComponentFactory( String key, Class c, BeanView bv ) {
        super(key, c, bv);
    }

    /**
     * Installs the Channel specific factories ahead of the default factories.
     */
    protected void installDefaultFactories() {
        factories.add(new ElementFactory());
        factories.add(new ElementCollectionFactory());
        super.installDefaultFactories();
    }
}
