// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.system;

import com.mindalliance.channels.User;
import com.mindalliance.channels.support.GUID;

/**
 * Identity certification.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class IdentityCertification extends Certification {

    private User userCertified;
    private GUID personGUID;

    /**
     * Default constructor.
     */
    public IdentityCertification() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public IdentityCertification( GUID guid ) {
        super( guid );
    }

    /**
     * Return the certified personification.
     */
    public GUID getPersonGUID() {
        return personGUID;
    }

    /**
     * Set the certified personification.
     * @param personGUID the personGUID to set
     */
    public void setPersonGUID( GUID personGUID ) {
        this.personGUID = personGUID;
    }

    /**
     * Return the user being certified.
     */
    public User getUserCertified() {
        return userCertified;
    }

    /**
     * Set the user being certified.
     * @param userCertified the userCertified to set
     */
    public void setUserCertified( User userCertified ) {
        this.userCertified = userCertified;
    }
}
