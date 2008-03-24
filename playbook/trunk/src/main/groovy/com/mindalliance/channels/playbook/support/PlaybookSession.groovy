package com.mindalliance.channels.playbook.support

import com.mindalliance.channels.playbook.mem.SessionMemory
import com.mindalliance.channels.playbook.ref.Ref
import org.apache.wicket.Request
import org.apache.wicket.authentication.AuthenticatedWebApplication
import org.apache.wicket.authorization.strategies.role.Roles

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 21, 2008
* Time: 11:20:47 AM
*/
class PlaybookSession extends KludgeWebSession implements Transactionable {

    private Ref participation;
    private Ref user;
    private Ref project;

    private PlaybookApplication application;     // TODO

    private SessionMemory memory = new SessionMemory();

    public PlaybookSession(){
    }

    public PlaybookSession( AuthenticatedWebApplication application, Request request ) {
        super( application, request );
        this.application = (PlaybookApplication) application;

    }

    // For testing only
    public void setApplication(PlaybookApplication app) {
        application = app
    }

    public boolean authenticate( String id, String password ) {
        participation = null;
        user = application.findUser( id );
        if ( user != null && user.password == password ) {
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

    public Ref getParticipation() {
        return participation;
    }

    public Ref getProject() {
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