/*
 * Created on Apr 30, 2007
 *
 */
package com.mindalliance.channels.data.elements;

import java.util.List;

import com.mindalliance.channels.data.Contactable;
import com.mindalliance.channels.data.Knowledgeable;
import com.mindalliance.channels.data.beans.CanAccess;
import com.mindalliance.channels.data.beans.ContactInfo;
import com.mindalliance.channels.data.beans.Information;
import com.mindalliance.channels.data.beans.Pattern;

public abstract class InformationResource extends AbstractResource implements
		Contactable, Knowledgeable {
	
	class AccessAuthorization {
		Pattern<Contactable> accessAuthorization;
	}
		
	private List<ContactInfo> contactInfos;
	private List<AccessAuthorization> accessAuthorizations;
	private List<Information> interests;
	private List<Information> expertises;
	private List<CanAccess> canAccessAssertions;


	/* (non-Javadoc)
	 * @see com.mindalliance.channels.data.Accessible#getCanAccesses()
	 */
	public List<CanAccess> getCanAccessAssertions() {
		return canAccessAssertions;
	}


	/* (non-Javadoc)
	 * @see com.mindalliance.channels.data.Accessible#getContactInfos()
	 */
	public List<ContactInfo> getContactInfos() {
		return contactInfos;
	}



	public boolean hasAccess(Contactable contactable) {
		return false;
	}

	
	public boolean knows(Information information) {
		return false;
	}

	public boolean needsToKnow(Information information) {
		return false;
	}

}
