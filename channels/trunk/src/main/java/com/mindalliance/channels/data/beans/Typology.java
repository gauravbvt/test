/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.beans;

import java.util.List;

import com.mindalliance.channels.data.Described;
import com.mindalliance.channels.data.Named;
import com.mindalliance.channels.util.AbstractJavaBean;
/**
 * A taxonomy
 * @author jf
 *
 */
public class Typology extends AbstractJavaBean implements Named, Described {
	
	private List<Type> types;
	private Type root; // The type all types imply by default.

	public String getName() {
		return null;
	}

	public String getDescription() {
		return null;
	}

}
