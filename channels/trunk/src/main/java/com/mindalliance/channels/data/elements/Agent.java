/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.elements;

import java.util.List;

import com.mindalliance.channels.data.Actor;
import com.mindalliance.channels.data.Connected;
import com.mindalliance.channels.data.Knowledgeable;
import com.mindalliance.channels.data.Needy;
import com.mindalliance.channels.data.OptOutable;
import com.mindalliance.channels.data.beans.CanAccess;
import com.mindalliance.channels.data.beans.Information;
import com.mindalliance.channels.data.beans.NeedsToKnow;
import com.mindalliance.channels.data.beans.OptedOut;


/**
 * A specification of who is executing a task, together or separately.
 * @author jf
 *
 */
abstract public class Agent extends AbstractScenarioElement implements Actor, Needy, Connected, OptOutable, Knowledgeable {
	
	private Task task;
	private List<NeedsToKnow> needsToKnowAssertions;
	private List<CanAccess> canAccessAssertions;
	private List<OptedOut> optedOutAssertions;
	
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
		return canAccessAssertions;
	}

	/* (non-Javadoc)
	 * @see com.mindalliance.channels.data.Needy#getNeedsToKnowAssertions()
	 */
	public List<NeedsToKnow> getNeedsToKnowAssertions() {
		return needsToKnowAssertions;
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
		return optedOutAssertions;
	}


}
