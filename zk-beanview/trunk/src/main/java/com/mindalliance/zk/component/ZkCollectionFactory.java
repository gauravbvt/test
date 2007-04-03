/**
 * 
 */
package com.mindalliance.zk.component;

import java.util.Collection;

import com.beanview.BeanView;
import com.beanview.PropertyComponent;
import com.beanview.PropertyComponentFactory;
import com.beanview.util.Configuration;
import com.beanview.util.FactoryResolver;
import com.mindalliance.zk.beanview.model.ZkCollectionModel;

/**
 * A factory that generates a multi-selectable list box for data in a collection.
 *
 */
public class ZkCollectionFactory implements PropertyComponentFactory {

	/* (non-Javadoc)
	 * @see com.beanview.PropertyComponentFactory#getComponent(java.lang.String, java.lang.Class, com.beanview.BeanView)
	 */
	public PropertyComponent getComponent(String key, Class type, BeanView bean) {
        if (type.isAssignableFrom(Collection.class))
        {
            SettableZkList result = new SettableZkList();

            Collection temp = new FactoryResolver().getValues(key, bean);
            if (temp == null)
                throw new IllegalArgumentException(
                        "Unable to find factory for collection.");
            result.setMultipleSelectOptions(new ZkCollectionModel(temp, type,
                    null, false));

            //result.setSelectionMode(ListSelectionModel.MULTIPLE_SELECTION);
            result.setMultiple(true);
            Configuration configuration = new Configuration(bean);
            result.setDisabled(!configuration.editable(key));

            return result;
        }
        return null;
	}

}
