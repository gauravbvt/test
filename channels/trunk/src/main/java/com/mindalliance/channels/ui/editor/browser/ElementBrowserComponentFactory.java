// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor.browser;

import com.beanview.BeanView;
import com.mindalliance.channels.ui.editor.ElementCollectionFactory;
import com.mindalliance.channels.ui.editor.ElementFactory;
import com.mindalliance.zk.beanview.ZkComponentFactory;


/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class ElementBrowserComponentFactory extends ZkComponentFactory {
    public ElementBrowserComponentFactory( String key, Class c, BeanView bv ) {
        super(key, c, bv);
    }

    @Override
    protected void installDefaultFactories()
    {
        factories.add(new BrowserFactory());
        super.installDefaultFactories();
    }
}
