/**
 * 
 */
package com.mindalliance.zk.component;

import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Date;

import com.beanview.BeanView;
import com.beanview.PropertyComponent;
import com.beanview.PropertyComponentFactory;
import com.beanview.util.Configuration;

/**
 * Generates a ZK component for editing primitive types.  A Checkbox is returned
 * for boolean properties, while a Textbox is returened for all others.
 *
 */
public class ZkPrimitiveFactory implements PropertyComponentFactory {

	/* (non-Javadoc)
	 * @see com.beanview.PropertyComponentFactory#getComponent(java.lang.String, java.lang.Class, com.beanview.BeanView)
	 */
	public PropertyComponent getComponent(String key, Class type, BeanView bean) {
		String typeName = type.getCanonicalName();
		Configuration configuration = new Configuration(bean);
		if (typeName.compareTo(Boolean.class.getName()) == 0)
		{
			SettableZkCheckBox result = new SettableZkCheckBox();
			result.setDisabled(!configuration.editable(key));
			return new SettableZkCheckBox();
		}

		if (typeName.compareTo("boolean") == 0)
		{
			SettableZkCheckBox result = new SettableZkCheckBox();
			result.setDisabled(!configuration.editable(key));
			return new SettableZkCheckBox();
		}

		Class[] primitives =
		{ Double.class, Double.TYPE, Float.class, Float.TYPE, Integer.class,
				Integer.TYPE, Long.class, Long.TYPE, Short.class, Short.TYPE,
				String.class, Timestamp.class, Date.class, Time.class };

		for (Class primitive : primitives)
		{
			if (typeName.compareTo(primitive.getName()) == 0)
			{
				SettableZkTextbox result = new SettableZkTextbox();
				result.setBeanView(bean);			
				result.setDisabled(!configuration.editable(key));
				return result;
			}
		}

		return null;
	}

}
