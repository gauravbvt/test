/*
 * Created on Apr 30, 2007
 *
 */
package com.mindalliance.channels.data.elements.resources;

import java.util.List;

import com.mindalliance.channels.data.elements.Actor;


/**
 * A team aggregates roles possibly across organizations.
 * @author jf
 *
 */
public class Team extends AccessibleResource implements Actor {
	
	class Membership {
		
		private Role role;
		private Integer count;
		private boolean pointOfContact; // is a point of contact for the team
	}
	
	private List<Membership> memberships;

	public List<Role> getRoles() {
		return null;
	}

}
