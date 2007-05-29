// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor.components;

import com.beanview.BeanView;
import com.mindalliance.zk.beanview.ZkComponentFactory;
import com.mindalliance.zk.component.ZkArrayFactory;
import com.mindalliance.zk.component.ZkClassOptionsFactory;
import com.mindalliance.zk.component.ZkCollectionFactory;
import com.mindalliance.zk.component.ZkEnumFactory;
import com.mindalliance.zk.component.ZkPrimitiveFactory;


/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class ElementComponentFactory extends ZkComponentFactory {

    public ElementComponentFactory( String key, Class c, BeanView bv ) {
        super(key, c, bv);
    }

    protected void installDefaultFactories()
    {
        factories.add(new ElementFactory());
        factories.add(new ElementCollectionFactory());
        super.installDefaultFactories();
    }
}
