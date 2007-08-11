// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.discoveries;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.mindalliance.channels.models.Assertion;
import com.mindalliance.channels.profiles.Contactable;
import com.mindalliance.channels.support.CollectionType;

/**
 * Accessibility assertion for a resource.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Accessible extends Assertion {

    private List<AccessAuthorization> accessAuthorizations;

    /**
     * Default constructor.
     */
    public Accessible() {
        super();
    }

    /**
     * Test if a contactable resource has access to this one.
     * @param contactable the resource
     */
    public boolean hasAccess( final Contactable contactable ) {
        return CollectionUtils.exists( accessAuthorizations, new Predicate() {

            public boolean evaluate( Object object ) {
                AccessAuthorization accessAuthorization =
                    (AccessAuthorization) object;
                return accessAuthorization.isAccessAuthorized( contactable );
            }
        } );
    }

    /**
     * Return the access authorizations.
     */
    @CollectionType( type = AccessAuthorization.class )
    public List<AccessAuthorization> getAccessAuthorizations() {
        return accessAuthorizations;
    }

    /**
     * Set the access authorizations.
     * @param accessAuthorizations the accessAuthorizations to set
     */
    public void setAccessAuthorizations(
            List<AccessAuthorization> accessAuthorizations ) {
        this.accessAuthorizations = accessAuthorizations;
    }

    /**
     * Add an access authorization.
     * @param accessAuthorization the authorization
     */
    public void addAccessAuthorization(
            AccessAuthorization accessAuthorization ) {
        accessAuthorizations.add( accessAuthorization );
    }

    /**
     * Remove an access authorization.
     * @param accessAuthorization the authorization
     */
    public void removeAccessAuthorization(
            AccessAuthorization accessAuthorization ) {
        accessAuthorizations.remove( accessAuthorization );
    }

    /**
     * An access authorization.
     */
    public class AccessAuthorization {

        /**
         * Default constructor.
         */
        public AccessAuthorization() {
        }

        /**
         * Test for access authorization.
         * @param contactable the contacting resource
         */
        public boolean isAccessAuthorized( Contactable contactable ) {
            // TODO
            return true;
        }
    }
}
