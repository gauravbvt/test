/*
 * Created on May 2, 2007
 *
 */
package com.mindalliance.channels.data.beans;

import com.mindalliance.channels.data.Described;
import com.mindalliance.channels.data.Occurrence;
import com.mindalliance.channels.util.AbstractJavaBean;
/**
 * An effect on an occurrence traceable to an issue, possibly caused indirectly by another impact of that issue. 
 * @author jf
 *
 */
public class Impact extends AbstractJavaBean implements Described {
	
	enum Effect {ENABLED, DISABLED};

	private String description;
	private Issue issue;
	private Occurrence impacted;
	private Effect effect;
	private Impact causedBy;
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	

}
