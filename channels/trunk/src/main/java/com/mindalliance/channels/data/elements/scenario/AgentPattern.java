/*
 * Created on May 1, 2007
 *
 */
package com.mindalliance.channels.data.elements.scenario;

import java.util.List;

import com.mindalliance.channels.data.OptOutable;
import com.mindalliance.channels.data.elements.project.Project;
import com.mindalliance.channels.data.elements.resources.Role;
import com.mindalliance.channels.data.support.Pattern;
import com.mindalliance.channels.util.GUID;

/**
 * All matching roles in project scope execute the task as separate activities.
 * @author jf
 *
 */
public class AgentPattern extends Agent implements OptOutable {

	private Pattern<Role> pattern;
	
	
	
	public AgentPattern() {
		super();
	}

	public AgentPattern(GUID guid) {
		super(guid);
	}

	public List<Role> getMatchedRoles() {
		return null;
	}

	@Override
	public List<Role> getRoles(Project project) {
		return null; //TODO
	}

	/**
	 * @return the pattern
	 */
	public Pattern<Role> getPattern() {
		return pattern;
	}

	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(Pattern<Role> pattern) {
		this.pattern = pattern;
	}


}
