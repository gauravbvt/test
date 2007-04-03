/**
 * 
 */
package com.mindalliance.zk.component;

import com.beanview.BeanView;
import com.beanview.PropertyComponent;
import com.beanview.PropertyComponentFactory;
import com.beanview.util.Configuration;
import com.mindalliance.zk.beanview.model.ZkEnumModel;

/**
 * A factory that generates a single-selectable/1-row ZK list box for data in an enum.
 *
 */
public class ZkEnumFactory implements PropertyComponentFactory {

	/* (non-Javadoc)
	 * @see com.beanview.PropertyComponentFactory#getComponent(java.lang.String, java.lang.Class, com.beanview.BeanView)
	 */
	public PropertyComponent getComponent(String key, Class type, BeanView bean) {
        if (type.isEnum())
        {
            SettableZkSingleList result = new SettableZkSingleList();

            ZkEnumModel model = new ZkEnumModel(type, false);
            result.setModel(model);

			Configuration configuration = new Configuration(bean);
			result.setDisabled(!configuration.editable(key));

            return result;
        }

        return null;
	}

}
