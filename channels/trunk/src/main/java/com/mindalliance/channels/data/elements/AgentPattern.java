/*
 * Created on May 1, 2007
 *
 */
package com.mindalliance.channels.data.elements;

import java.util.List;

import com.mindalliance.channels.data.OptOutable;
import com.mindalliance.channels.data.beans.OptedOut;
import com.mindalliance.channels.data.beans.Pattern;

/**
 * All matching roles in project scope execute the task as separate activities.
 * @author jf
 *
 */
public class AgentPattern extends Agent implements OptOutable {

	private Pattern<Role> pattern;
	private List<OptedOut> exclusions;
	
	public List<OptedOut> getExclusions() {
		return exclusions;
	}
	
	public List<Role> getMatchedRoles() {
		return null;
	}


}
