/*
 * Created on May 2, 2007
 *
 */
package com.mindalliance.channels.data.elements.analysis;

import com.mindalliance.channels.data.elements.AbstractElement;
import com.mindalliance.channels.data.elements.scenario.Occurrence;
/**
 * An effect on an occurrence traceable to an issue, possibly caused indirectly by another impact of that issue. 
 * @author jf
 *
 */
public class Impact extends AbstractElement {
	
	enum Effect {ENABLED, DISABLED};

	private Issue issue;
	private Occurrence impacted;
	private Effect effect;
	private Impact causedBy;	

}
