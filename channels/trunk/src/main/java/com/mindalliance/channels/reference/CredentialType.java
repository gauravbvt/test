// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.reference;

/**
 * A kind of credential.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public abstract class CredentialType extends Type {

    /**
     * Default constructor.
     */
    public CredentialType() {
        super();
    }

    /**
     * Default constructor.
     * @param name the name of this credential
     */
    public CredentialType( String name ) {
        super( name );
    }

}
