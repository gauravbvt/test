// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.elements.resources;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.data.components.ContactInfo;
import com.mindalliance.channels.data.components.Contactable;
import com.mindalliance.channels.data.reference.Information;
import com.mindalliance.channels.util.CollectionType;
import com.mindalliance.channels.util.GUID;

/**
 * A contactable resource that grants access to information.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Repository extends AccessibleResource implements Contactable {

    private Organization organization;

    /**
     * A specification of what assets the repository can be expected
     * to contain by default.
     */
    private List<Information> contents;

    /** Role within the administration, normally. */
    private Role administrator;

    private List<ContactInfo> contactInfos;

    /**
     * Default constructor.
     */
    public Repository() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Repository( GUID guid ) {
        super( guid );
        contactInfos = new ArrayList<ContactInfo>();
        contents = new ArrayList<Information>();
    }

    /**
     * Return the administrator.
     */
    public Role getAdministrator() {
        return administrator;
    }

    /**
     * Set the administrator.
     * @param administrator the administrator to set
     */
    public void setAdministrator( Role administrator ) {
        this.administrator = administrator;
    }

    /**
     * Return the contents.
     */
    @CollectionType(type=Information.class)
    public List<Information> getContents() {
        return contents;
    }

    /**
     * Set the contents.
     * @param contents the contents to set
     */
    public void setContents( List<Information> contents ) {
        this.contents = contents;
    }

    /**
     * Add some content.
     * @param information the content
     */
    public void addContent( Information information ) {
        contents.add( information );
    }

    /**
     * Remove some content.
     * @param information the content
     */
    public void removeContent( Information information ) {
        contents.remove( information );
    }

    /**
     * Return the organization.
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * Set the organization.
     * @param organization the organization to set
     */
    public void setOrganization( Organization organization ) {
        this.organization = organization;
    }

    /**
     * Return the value of contactInfos.
     */
    @CollectionType(type=ContactInfo.class)
    public List<ContactInfo> getContactInfos() {
        return contactInfos;
    }

    /**
     * Set the value of contactInfos.
     * @param contactInfos The new value of contactInfos
     */
    public void setContactInfos( List<ContactInfo> contactInfos ) {
        this.contactInfos = contactInfos;
    }

    /**
     * Add a contact.
     * @param contactInfo the contact
     */
    public void addContactInfo( ContactInfo contactInfo ) {
        contactInfos.add( contactInfo );
    }

    /**
     * Remove a contact.
     * @param contactInfo the contact
     */
    public void removeContactInfo( ContactInfo contactInfo ) {
        contactInfos.remove( contactInfo );
    }
}
