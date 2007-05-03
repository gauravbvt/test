/*
 * Created on May 3, 2007
 *
 */
package com.mindalliance.channels.data.elements;

import java.util.List;

import com.mindalliance.channels.data.Accessible;
import com.mindalliance.channels.data.Contactable;
import com.mindalliance.channels.data.beans.Pattern;
/**
 * A resource that controls access to itself.
 * @author jf
 *
 */
public abstract class AccessibleResource extends AbstractResource implements
		Accessible {
	
	class AccessAuthorization {
		private Pattern<Contactable> authorization;
	}
	
	private List<AccessAuthorization> accessAuthorizations;

	public boolean hasAccess(Contactable contactable) {
		return false;
	}

}
