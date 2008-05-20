package com.mindalliance.channels.playbook.support

import com.mindalliance.channels.playbook.mem.SessionMemory
import com.mindalliance.channels.playbook.ref.Ref
import org.apache.wicket.Request
import org.apache.wicket.authentication.AuthenticatedWebApplication
import org.apache.wicket.authorization.strategies.role.Roles
import org.apache.wicket.Session
import com.mindalliance.channels.playbook.query.QueryCache

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 21, 2008
* Time: 11:20:47 AM
*/
class PlaybookSession extends KludgeWebSession implements Transactionable, Serializable {

    private Ref participation;
    private Ref user;
    private Ref project;
    private Ref model;

    private PlaybookApplication application;     // TODO

    private SessionMemory memory = new SessionMemory();

    public PlaybookSession(){
    }

    public PlaybookSession( AuthenticatedWebApplication application, Request request ) {
        super( application, request );
        this.application = (PlaybookApplication) application;

    }

    static PlaybookSession current() {
        return (PlaybookSession)Session.get()
    }

    // For testing only
    public void setApplication(PlaybookApplication app) {
        application = app
    }

    public boolean authenticate( String id, String password ) {
        participation = null;
        user = application.findUser( id );
        if ( user != null && user.password == password ) {
            project = user.selectedProject
            if ( project == null )
                project = application.findProjectsForUser( user )[0]
            if ( project != null )
                participation = application.findParticipation( project, user )

            model = user.selectedModel
            if ( user.analyst && model == null )
                model = application.findModelsForUser( user )[0]

            return true;
        }
        else
            return false;
    }

    public Roles getRoles() {
        if ( isSignedIn() ) {
            List<String> roles = [ "USER" ]
            if ( isAdmin() )
                roles.add( "ADMIN" )
            if ( isManager() )
                roles.add( "MANAGER" )
            if ( isAnalyst() )
                roles.add( "ANALYST" )

            return new Roles( roles.toArray( new String[ roles.size() ] ) )
        } else
            return null
        return isSignedIn()?
               new Roles( isAdmin()? Roles.ADMIN : Roles.USER ) : null;
    }

    public boolean isAdmin() {
        return user.admin;
    }

    public boolean isManager() {
        return user.manager;
    }

    public boolean isAnalyst() {
        return user.analyst;
    }

    public Ref getParticipation() {
        return participation;
    }

    public Ref getProject() {
        return project;
    }

    public Ref getModel() {
        return model;
    }

    public SessionMemory getMemory() {
        return memory;
    }

    public QueryCache getQueryCache() {
        return memory.queryCache
    }

    public void commit() {
        memory.commit()
    }

    public void abort() {
        memory.abort()
    }

    public void commit(Ref ref) {
        memory.commit(ref)
    }

    public void reset(Ref ref) {
        memory.reset(ref)
    }

    public int getPendingChangesCount() {
        return memory.size;
    }

    int getPendingDeletesCount() {
        return memory.getPendingDeletesCount()
    }

    // Java support

    Ref getUser() {
        return user
    }


}