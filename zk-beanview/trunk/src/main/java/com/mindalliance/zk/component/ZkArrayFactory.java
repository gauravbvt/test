/**
 * 
 */
package com.mindalliance.zk.component;

import com.beanview.BeanView;
import com.beanview.PropertyComponent;
import com.beanview.PropertyComponentFactory;
import com.beanview.util.Configuration;
import com.mindalliance.zk.beanview.model.ZkArrayModel;

/**
 * A factory that generates a multi-selectable list box for data in an array.
 *
 */
public class ZkArrayFactory implements PropertyComponentFactory {

	/* (non-Javadoc)
	 * @see com.beanview.PropertyComponentFactory#getComponent(java.lang.String, java.lang.Class, com.beanview.BeanView)
	 */
	public PropertyComponent getComponent(String key, Class type, BeanView bean) {
		if (type.isArray())
		{
			SettableZkList result = new SettableZkList();
			
			
			Class componentType = type.getComponentType();

			if (componentType.isEnum())
			{
				Object[] enums = componentType.getEnumConstants();
				result.setModel(new ZkArrayModel(enums, null, false));
			}
			result.setMultiple(true);
			//result.setSelectionMode(ListSelectionModel.MULTIPLE_SELECTION);
			
			Configuration configuration = new Configuration(bean);
			result.setDisabled(!configuration.editable(key));
			return result;
		}

		return null;
	}

}
