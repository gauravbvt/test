/*
 * Created on May 1, 2007
 *
 */
package com.mindalliance.channels.data.elements;

import java.util.List;

import com.mindalliance.channels.data.Accessible;
import com.mindalliance.channels.data.Contactable;
import com.mindalliance.channels.data.beans.Information;
/**
 * A contactable  resource that grants access to information.
 * @author jf
 *
 */
public class Repository extends ContactableResource implements Accessible {
	
	private Organization organization;
	// A specification of what assets the repository can be expected to contain by default.
	private List<Information> contents; 
	private Role administrator; // Role within the administration normally


	public boolean hasAccess(Contactable contactable) {
		return false;
	}

}
