/*
 * Created on May 1, 2007
 *
 */
package com.mindalliance.channels.data.elements.scenario;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.data.elements.project.Project;
import com.mindalliance.channels.data.elements.resources.Role;
import com.mindalliance.channels.util.GUID;

/**
 * One or more identical roles executing the task separately.
 * @author jf
 *
 */
public class RoleAgent extends Agent {

	private Role role;
	private Integer number;
	
	

	public RoleAgent() {
		super();
	}

	public RoleAgent(GUID guid) {
		super(guid);
	}

	@Override
	public List<Role> getRoles(Project project) {
		// TODO - first check that the role is still within the scope of the project?
		List<Role> roles = new ArrayList<Role>();
		roles.add(role);
		return roles;
	}

	/**
	 * @return the number
	 */
	public Integer getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(Integer number) {
		this.number = number;
	}

	/**
	 * @return the role
	 */
	public Role getRole() {
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(Role role) {
		this.role = role;
	}
}
