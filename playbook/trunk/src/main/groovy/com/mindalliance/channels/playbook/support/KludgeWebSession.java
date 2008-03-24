package com.mindalliance.channels.playbook.support;

import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authentication.AuthenticatedWebApplication;
import org.apache.wicket.Request;
import org.apache.wicket.authorization.strategies.role.Roles;

/**
 * Just here because maven stub generation require default
 * constructor on superclasses
 */
public class KludgeWebSession extends AuthenticatedWebSession {

    public KludgeWebSession( AuthenticatedWebApplication authenticatedWebApplication, Request request ) {
        super( authenticatedWebApplication, request );
    }

    public boolean authenticate( String s, String s1 ) {
        return true;
    }

    public Roles getRoles() {
        return null;
    }

    public KludgeWebSession(){
        super( null, null );
    }
}

