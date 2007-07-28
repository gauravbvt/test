// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.profiles;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.data.definitions.Category.Taxonomy;
import com.mindalliance.channels.data.support.GUID;
import com.mindalliance.channels.util.CollectionType;

/**
 * A contactable resource.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public abstract class ContactableResource extends Resource
    implements Contactable {

    private List<ContactInfo> contactInfos;

    /**
     * Default constructor.
     */
    public ContactableResource() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public ContactableResource( GUID guid ) {
        super( guid, Taxonomy.Any );
        contactInfos = new ArrayList<ContactInfo>();
    }

    /**
     * Return the contact infos.
     */
    @CollectionType( type = ContactInfo.class )
    public List<ContactInfo> getContactInfos() {
        return contactInfos;
    }

    /**
     * Set the contact infos.
     * @param contactInfos the contactInfos to set
     */
    public void setContactInfos( List<ContactInfo> contactInfos ) {
        this.contactInfos = contactInfos;
    }

    /**
     * Add a contact info.
     * @param contactInfo the info
     */
    public void addContactInfo( ContactInfo contactInfo ) {
        contactInfos.add( contactInfo );
    }

    /**
     * Remove a contact info.
     * @param contactInfo the info
     */
    public void removeContactInfo( ContactInfo contactInfo ) {
        contactInfos.remove( contactInfo );
    }
}
