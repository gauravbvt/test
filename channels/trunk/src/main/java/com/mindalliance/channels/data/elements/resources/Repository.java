/*
 * Created on May 1, 2007
 */
package com.mindalliance.channels.data.elements.resources;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.data.Contactable;
import com.mindalliance.channels.data.components.ContactInfo;
import com.mindalliance.channels.data.components.Information;
import com.mindalliance.channels.util.GUID;

/**
 * A contactable resource that grants access to information.
 * 
 * @author jf
 */
public class Repository extends AccessibleResource implements Contactable {

    private Organization organization;
    // A specification of what assets the repository can be expected
    // to contain by default.
    private List<Information> contents;
    private Role administrator; // Role within the administration
                                // normally
    private List<ContactInfo> contactInfos;

    public Repository() {
        super();
    }

    public Repository( GUID guid ) {
        super( guid );
        contactInfos = new ArrayList<ContactInfo>();
        contents = new ArrayList<Information>();
    }

    /**
     * @return the administrator
     */
    public Role getAdministrator() {
        return administrator;
    }

    /**
     * @param administrator the administrator to set
     */
    public void setAdministrator( Role administrator ) {
        this.administrator = administrator;
    }

    /**
     * @return the contents
     */
    public List<Information> getContents() {
        return contents;
    }

    /**
     * @param contents the contents to set
     */
    public void setContents( List<Information> contents ) {
        this.contents = contents;
    }

    /**
     * @param information
     */
    public void addContent( Information information ) {
        contents.add( information );
    }

    /**
     * @param information
     */
    public void removeContent( Information information ) {
        contents.remove( information );
    }

    /**
     * @return the organization
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * @param organization the organization to set
     */
    public void setOrganization( Organization organization ) {
        this.organization = organization;
    }

    
    /**
     * Return the value of contactInfos.
     */
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
