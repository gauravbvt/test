/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.elements;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.data.Actor;


/**
 * A role in an organization either played by identified persons or by a group of anonymous, interchangeable persons.
 * A role can be internal to the organization (not accessible from the outside) or external.
 * @author jf
 *
 */
public class Role extends InformationResource implements Actor {
	
	private Organization organization;
	// Either persons or group is set, not both.
	private List<Person> persons;
	private Group group;
	private boolean isInternal; // Is this role known only internally to the organization (e.g. morale officer)
	private List<Role> managers;
	private List<Role> alternates;
	private boolean pointOfContact; // is a point of contact for the organization
	private Location jurisdiction; // Area of jurisdiction for the role
	
	public List<Role> getRoles() {
		List<Role> roles = new ArrayList();
		roles.add(this);
		return roles;
	}
}
