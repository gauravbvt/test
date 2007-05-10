/*
 * Created on May 1, 2007
 *
 */
package com.mindalliance.channels.data.components;

import com.mindalliance.channels.data.Described;
import com.mindalliance.channels.data.Typed;
import com.mindalliance.channels.data.support.TypeSet;
import com.mindalliance.channels.util.AbstractJavaBean;

public class Mission extends AbstractJavaBean implements Described, Typed {
	
	private TypeSet typeSet;
	private String description;
	
	public Mission() {}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the typeSet
	 */
	public TypeSet getTypeSet() {
		return typeSet;
	}

	/**
	 * @param typeSet the typeSet to set
	 */
	public void setTypeSet(TypeSet typeSet) {
		this.typeSet = typeSet;
	}

}
