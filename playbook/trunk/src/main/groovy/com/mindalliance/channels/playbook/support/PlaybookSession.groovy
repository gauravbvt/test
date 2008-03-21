package com.mindalliance.channels.playbook.support

import org.apache.wicket.authentication.AuthenticatedWebSession
import com.mindalliance.channels.playbook.mem.SessionMemory
import org.apache.wicket.authentication.AuthenticatedWebApplication
import org.apache.wicket.Request
import com.mindalliance.channels.playbook.ref.Reference
import org.apache.wicket.authorization.strategies.role.Roles

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 21, 2008
* Time: 11:20:47 AM
*/
class PlaybookSession  extends AuthenticatedWebSession implements Transactionable {

    private Reference participation;
    private Reference user;
    private Reference project;

    private PlaybookApplication application;

    private SessionMemory memory = new SessionMemory();

    public PlaybookSession( AuthenticatedWebApplication application, Request request ) {
        super( application, request );
        this.application = (PlaybookApplication) application;

    }

    public boolean authenticate( String name, String password ) {
        participation = null;
        user = application.findUserNamed( name );
        if ( user != null && user.getPassword().equals( password ) ) {
            project = application.findProjectsForUser( user )[0]
            participation = application.findParticipation( project, user );
            return true;
        }
        else
            return false;
    }

    public Roles getRoles() {
        return isSignedIn()?
               new Roles( isAdmin()? Roles.ADMIN : Roles.USER ) : null;
    }

    public boolean isAdmin() {
        return participation != null && participation.user.admin;
    }

    public Reference getParticipation() {
        return participation;
    }

    public Reference getProject() {
        return project;
    }

    public SessionMemory getMemory() {
        return memory;
    }

    public void commit() {
        memory.commit()
    }

    public void abort() {
        memory.abort()
    }

    public int getTransactionCount() {
        return memory.size;
    }
}