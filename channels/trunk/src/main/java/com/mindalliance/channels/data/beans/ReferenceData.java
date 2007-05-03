/*
 * Created on May 3, 2007
 *
 */
package com.mindalliance.channels.data.beans;

import com.mindalliance.channels.data.Described;
import com.mindalliance.channels.data.Named;
import com.mindalliance.channels.data.Unique;
import com.mindalliance.channels.util.AbstractJavaBean;
import com.mindalliance.channels.util.GUID;
/**
 * ReferenceData data
 * @author jf
 *
 */
public class ReferenceData extends AbstractJavaBean implements Unique, Described,
		Named {
	
	private GUID guid;
	private String name;
	private String description;

	public GUID getGuid() {
		return guid;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

}
