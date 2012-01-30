package com.mindalliance.channels.playbook.support;

import org.apache.wicket.Request;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authorization.strategies.role.Roles;

/**
 * Just here because maven stub generation require default
 * constructor on superclasses
 */
public class KludgeWebSession extends AuthenticatedWebSession {

    private static final long serialVersionUID = 6272266771278806737L;

    public KludgeWebSession(){
        super( null );
    }

    public KludgeWebSession( Request request ) {
        super( request );
    }

    public boolean authenticate( String s, String s1 ) {
        return true;
    }

    public Roles getRoles() {
        return null;
    }

}

