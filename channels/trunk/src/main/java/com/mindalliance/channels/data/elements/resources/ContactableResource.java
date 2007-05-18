/*
 * Created on May 3, 2007
 */
package com.mindalliance.channels.data.elements.resources;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.data.Contactable;
import com.mindalliance.channels.data.components.ContactInfo;
import com.mindalliance.channels.util.GUID;

public abstract class ContactableResource extends AbstractResource implements
        Contactable {

    private List<ContactInfo> contactInfos;

    public ContactableResource() {
        super();
    }

    public ContactableResource( GUID guid ) {
        super( guid );
        contactInfos = new ArrayList<ContactInfo>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mindalliance.channels.data.Contactable#getContactInfos()
     */
    public List<ContactInfo> getContactInfos() {
        return contactInfos;
    }

    /**
     * @param contactInfos the contactInfos to set
     */
    public void setContactInfos( List<ContactInfo> contactInfos ) {
        this.contactInfos = contactInfos;
    }

    /**
     * @param contactInfo
     */
    public void addContactInfo( ContactInfo contactInfo ) {
        contactInfos.add( contactInfo );
    }

    /**
     * @param contactInfo
     */
    public void removeContactInfo( ContactInfo contactInfo ) {
        contactInfos.remove( contactInfo );
    }

}
