// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.profiles;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.data.definitions.Information;
import com.mindalliance.channels.data.support.GUID;
import com.mindalliance.channels.util.CollectionType;

/**
 * A contactable resource that grants access to information.
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 * @composed - contents * Information
 * @navassoc - admin 1 Actor
 */
public class Repository extends ContactableResource {

    private OrganizationImpl organization;

    /**
     * A specification of what assets the repository can be expected
     * to contain by default.
     */
    private List<Information> contents = new ArrayList<Information>();

    /** Role within the administration, normally. */
    private Actor administrator;

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
    }

    /**
     * Return the administrator.
     */
    public Actor getAdministrator() {
        return administrator;
    }

    /**
     * Set the administrator.
     * @param administrator the administrator
     */
    public void setAdministrator( Actor administrator ) {
        this.administrator = administrator;
    }

    /**
     * Return the contents.
     */
    @CollectionType( type = Information.class )
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
    public OrganizationImpl getOrganization() {
        return organization;
    }

    /**
     * Set the organization.
     * @param organization the organization to set
     */
    public void setOrganization( OrganizationImpl organization ) {
        this.organization = organization;
    }
}
