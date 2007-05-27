// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.elements.resources;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.mindalliance.channels.data.components.Accessible;
import com.mindalliance.channels.data.components.Contactable;
import com.mindalliance.channels.data.reference.Pattern;
import com.mindalliance.channels.util.CollectionType;
import com.mindalliance.channels.util.GUID;

/**
 * A resource that controls access to itself.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public abstract class AccessibleResource extends AbstractResource
    implements Accessible {

    private List<AccessAuthorization> accessAuthorizations;

    /**
     * Default constructor.
     */
    public AccessibleResource() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public AccessibleResource( GUID guid ) {
        super( guid );
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

        private Pattern<Contactable> accessAuthorization;

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
            return accessAuthorization.matches( contactable );
        }

        /**
         * Return the access authorization pattern.
         */
        public Pattern<Contactable> getAccessAuthorization() {
            return accessAuthorization;
        }

        /**
         * Set the access authorization pattern.
         * @param accessAuthorization the pattern
         */
        public void setAccessAuthorization(
                Pattern<Contactable> accessAuthorization ) {
            this.accessAuthorization = accessAuthorization;
        }
    }

}
