/**
 * 
 */
package com.mindalliance.zk.component;

import java.util.Collection;

import com.beanview.BeanView;
import com.beanview.PropertyComponent;
import com.beanview.PropertyComponentFactory;
import com.beanview.util.FactoryResolver;
import com.mindalliance.zk.beanview.model.ZkCollectionModel;

/**
 * @author dfeeney
 *
 */
public class ZkClassOptionsFactory implements PropertyComponentFactory {

	/* (non-Javadoc)
	 * @see com.beanview.PropertyComponentFactory#getComponent(java.lang.String, java.lang.Class, com.beanview.BeanView)
	 */
	public PropertyComponent getComponent(String key, Class type, BeanView bean) {

        Collection temp = new FactoryResolver().getValues(key, bean);
        if (temp != null)
        {
            SettableZkSingleList result = new SettableZkSingleList();
            result.setModel(new ZkCollectionModel(temp, type, null, false));
            return result;
        }

        return null;
	}

}
