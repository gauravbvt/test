/**
 * 
 */
package com.mindalliance.zk.component;

import java.util.IllegalFormatConversionException;

import org.zkoss.zul.Checkbox;

import com.beanview.PropertyComponent;

/**
 * A ZK checkbox that can be set and read by BeanView
 *
 */
public class SettableZkCheckBox extends Checkbox implements PropertyComponent {

	private static final long serialVersionUID = -1007914582104996871L;
	/* (non-Javadoc)
	 * @see com.beanview.PropertyComponent#getValue()
	 */
	public Object getValue() {
		return this.isChecked();
	}

	/* (non-Javadoc)
	 * @see com.beanview.PropertyComponent#setValue(java.lang.Object)
	 */
	public void setValue(Object checked) throws IllegalFormatConversionException {
		if (checked == null) {
			this.setChecked(false);
		} else {
			this.setChecked((Boolean)checked);
		}
	}

}
