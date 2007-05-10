/*
 * Created on May 3, 2007
 *
 */
package com.mindalliance.channels.data.elements.resources;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.mindalliance.channels.data.Accessible;
import com.mindalliance.channels.data.Contactable;
import com.mindalliance.channels.data.support.Pattern;
import com.mindalliance.channels.util.GUID;
/**
 * A resource that controls access to itself.
 * @author jf
 *
 */
public abstract class AccessibleResource extends AbstractResource implements
		Accessible {
	
	public class AccessAuthorization {
		private Pattern<Contactable> accessAuthorization;

		public boolean isAccessAuthorized(Contactable contactable) {
			return accessAuthorization.matches(contactable);
		}
		/**
		 * @return the accessAuthorization
		 */
		public Pattern<Contactable> getAccessAuthorization() {
			return accessAuthorization;
		}

		/**
		 * @param accessAuthorization the accessAuthorization to set
		 */
		public void setAccessAuthorization(Pattern<Contactable> accessAuthorization) {
			this.accessAuthorization = accessAuthorization;
		}
	}
	
	private List<AccessAuthorization> accessAuthorizations;

	public AccessibleResource() {
		super();
	}

	public AccessibleResource(GUID guid) {
		super(guid);
	}

	public boolean hasAccess(final Contactable contactable) {
		return CollectionUtils.exists(accessAuthorizations, new Predicate() {
			public boolean evaluate(Object object) {
				AccessAuthorization accessAuthorization = (AccessAuthorization)object;
				return accessAuthorization.isAccessAuthorized(contactable);
			}
		});
	}

	/**
	 * @return the accessAuthorizations
	 */
	public List<AccessAuthorization> getAccessAuthorizations() {
		return accessAuthorizations;
	}

	/**
	 * @param accessAuthorizations the accessAuthorizations to set
	 */
	public void setAccessAuthorizations(
			List<AccessAuthorization> accessAuthorizations) {
		this.accessAuthorizations = accessAuthorizations;
	}
	/**
	 * 
	 * @param accessAuthorization
	 */
	public void addAccessAuthorization(AccessAuthorization accessAuthorization) {
		accessAuthorizations.add(accessAuthorization);
	}
	/**
	 * 
	 * @param accessAuthorization
	 */
	public void removeAccessAuthorization(AccessAuthorization accessAuthorization) {
		accessAuthorizations.remove(accessAuthorization);
	}

}
