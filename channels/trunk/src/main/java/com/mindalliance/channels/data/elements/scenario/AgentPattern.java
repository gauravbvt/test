/*
 * Created on May 1, 2007
 *
 */
package com.mindalliance.channels.data.elements.scenario;

import java.util.List;

import com.mindalliance.channels.data.elements.assertions.OptOutable;
import com.mindalliance.channels.data.elements.resources.Role;
import com.mindalliance.channels.data.support.Pattern;

/**
 * All matching roles in project scope execute the task as separate activities.
 * @author jf
 *
 */
public class AgentPattern extends Agent implements OptOutable {

	private Pattern<Role> pattern;
	
	public List<Role> getMatchedRoles() {
		return null;
	}


}
