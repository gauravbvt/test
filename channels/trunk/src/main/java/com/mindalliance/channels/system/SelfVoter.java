// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.system;

import org.acegisecurity.Authentication;
import org.acegisecurity.ConfigAttribute;
import org.acegisecurity.ConfigAttributeDefinition;
import org.acegisecurity.vote.AccessDecisionVoter;

/**
 * Voter for SELF tag.
 * Vote to authorize the users to modify their own record, or calls
 * to methods having their own record as a parameter.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 * @hidden
 */
public class SelfVoter implements AccessDecisionVoter {

    private static final String SELF = "SELF";

    /**
     * Default constructor.
     */
    public SelfVoter() {
    }

    /**
     * Test if this voter can handle a configuration attribute.
     * @param config the configuration attribute
     */
    public boolean supports( ConfigAttribute config ) {
        return SELF.equals( config.getAttribute() );
    }

    /**
     * Test if this voter applies to a class of objects.
     * @param clazz the class
     * @return true if class is UserImpl
     */
    public boolean supports( Class clazz ) {
        return UserImpl.class == clazz ;
    }

    /**
     * Grant access if the object is the same as the current user's
     * details. Abstain otherwise.
     * @param authentication the current authentication
     * @param object the object of the authorization
     * @param configs contextual configuration attributes
     */
    public int vote(
            Authentication authentication, Object object,
            ConfigAttributeDefinition configs ) {

        UserImpl user = (UserImpl) authentication.getDetails();
        return user == object ? ACCESS_GRANTED : ACCESS_ABSTAIN ;
    }

}
