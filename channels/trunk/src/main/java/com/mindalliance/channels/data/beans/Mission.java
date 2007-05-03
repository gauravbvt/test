/*
 * Created on May 1, 2007
 *
 */
package com.mindalliance.channels.data.beans;

import com.mindalliance.channels.data.Described;
import com.mindalliance.channels.data.TypeSet;
import com.mindalliance.channels.data.Typed;
import com.mindalliance.channels.util.AbstractJavaBean;

public class Mission extends AbstractJavaBean implements Described, Typed {
	
	private TypeSet types;
	private String description;

	public String getDescription() {
		return description;
	}

	public TypeSet getTypes() {
		return types;
	}

}
