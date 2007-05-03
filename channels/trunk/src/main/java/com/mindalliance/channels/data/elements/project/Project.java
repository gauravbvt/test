/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.elements.project;

import java.util.List;

import com.mindalliance.channels.User;
import com.mindalliance.channels.data.elements.AbstractElement;
import com.mindalliance.channels.data.elements.UserProfile;
import com.mindalliance.channels.data.elements.analysis.Issue;
import com.mindalliance.channels.data.elements.reference.Type;
import com.mindalliance.channels.data.elements.resources.Organization;
import com.mindalliance.channels.data.elements.resources.Role;
import com.mindalliance.channels.data.support.Pattern;
import com.mindalliance.channels.data.support.TypeSet;

public class Project extends AbstractElement {

	private TypeSet missions = new TypeSet(Type.MISSION);
	private List<Model> models;
	private List<Pattern<Role>> participation;
	private List<Pattern<Organization>> scope;
	private List<Issue> issues;
	
	/**
	 * Return whether the user matches at least one of the participation criteria.
	 * @param authenticatedUser
	 * @return
	 */
	public boolean hasParticipant(User authenticatedUser) {
		return false;
	}

}
