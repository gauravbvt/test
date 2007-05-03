/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.elements;

import java.util.List;

import com.mindalliance.channels.data.Located;
import com.mindalliance.channels.data.beans.Location;
import com.mindalliance.channels.data.beans.Mission;

/**
 * A resource composed of roles and repositories. An organization may be within a larger organization. 
 * An organization also has sharing agreements.
 * @author jf
 *
 */
public class Organization extends AbstractResource implements Located {
	
	private List<Mission> missions;
	private Organization parent;
	private Location location;
	private Location jurisdiction;
	private List<Role> roles;
	private List<Repository> repositories;
	private List<Agreement> agreements;
	
	public Location getLocation() {
		return location;
	}
}
