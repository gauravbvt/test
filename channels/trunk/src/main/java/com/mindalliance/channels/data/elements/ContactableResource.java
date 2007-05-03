/*
 * Created on May 3, 2007
 *
 */
package com.mindalliance.channels.data.elements;

import java.util.List;

import com.mindalliance.channels.data.Contactable;
import com.mindalliance.channels.data.beans.ContactInfo;

public abstract class ContactableResource extends AbstractResource implements Contactable {

	private List<ContactInfo> contactInfos;

	/* (non-Javadoc)
	 * @see com.mindalliance.channels.data.Contactable#getContactInfos()
	 */
	public List<ContactInfo> getContactInfos() {
		return contactInfos;
	}

}
