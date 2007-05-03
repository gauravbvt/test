/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.elements.scenario;

import java.util.List;

import com.mindalliance.channels.data.components.Information;
import com.mindalliance.channels.data.elements.Actor;
import com.mindalliance.channels.data.elements.assertions.CanAccess;
import com.mindalliance.channels.data.elements.assertions.Connected;
import com.mindalliance.channels.data.elements.assertions.NeedsToKnow;
import com.mindalliance.channels.data.elements.assertions.Needy;
import com.mindalliance.channels.data.elements.assertions.OptOutable;
import com.mindalliance.channels.data.elements.assertions.OptedOut;
import com.mindalliance.channels.data.elements.resources.Knowledgeable;
import com.mindalliance.channels.data.elements.resources.Role;


/**
 * A specification of who is executing a task, together or separately.
 * @author jf
 *
 */
abstract public class Agent extends AbstractScenarioElement implements Actor, Needy, Connected, OptOutable, Knowledgeable {
	
	private Task task;
	
	/* (non-Javadoc)
	 * @see com.mindalliance.channels.data.Actor#getRoles()
	 */
	public List<Role> getRoles() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.mindalliance.channels.data.Connected#getCanAccessAssertions()
	 */
	public List<CanAccess> getCanAccessAssertions() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.mindalliance.channels.data.Needy#getNeedsToKnowAssertions()
	 */
	public List<NeedsToKnow> getNeedsToKnowAssertions() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.mindalliance.channels.data.Knowledgeable#getNeedToKnowAssertions()
	 */
	public List<NeedsToKnow> getNeedToKnowAssertions() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.mindalliance.channels.data.Knowledgeable#knows(com.mindalliance.channels.data.beans.Information)
	 */
	public boolean knows(Information information) {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.mindalliance.channels.data.Knowledgeable#needsToKnow(com.mindalliance.channels.data.beans.Information)
	 */
	public boolean needsToKnow(Information information) {
		return false;
	}

	/**
	 * @return the optedOutAssertions
	 */
	public List<OptedOut> getOptedOutAssertions() {
		return null;
	}


}
