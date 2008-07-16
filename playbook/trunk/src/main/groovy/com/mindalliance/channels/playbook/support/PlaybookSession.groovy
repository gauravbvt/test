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

    private String userId;
    private Ref sessionUser;

    private PlaybookApplication application;

    private SessionMemory memory = new SessionMemory();

    public PlaybookSession(){
    }

    public String toString() {
        return "Session for $userId"
    }

    public PlaybookSession( AuthenticatedWebApplication application, Request request ) {
        super( application, request );
        this.application = (PlaybookApplication) application;

    }

    static PlaybookSession current() {
        return (PlaybookSession)Session.get()
    }

    void invalidate() {
        PlaybookApplication.current().sessionTimedOut(this)
        super.invalidate()
    }

    // For testing only
    public void setApplication(PlaybookApplication app) {
        application = app
    }

    public boolean authenticate( String id, String password ) {
        this.userId = id

        Ref user = user
        return user && user.password == password;
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
    }

    public boolean isAdmin() {
        Ref ref = user
        ref == null ? false : ref.admin
    }

    public boolean isManager() {
        Ref ref = user
        ref == null ? false : ref.manager
    }

    public boolean isAnalyst() {
        Ref ref = user
        ref == null ? false : ref.analyst
    }

    public Ref getUser() {
        if (sessionUser == null) {
            sessionUser = application.findUser( userId )
        }
        return sessionUser
    }

    public Ref getParticipation() {
        Ref user = user
        Ref project = project
        return project && user ? application.findParticipation( project, user ) : null
    }

    public Ref getProject() {
        Ref user = user
        Ref project = user?.selectedProject
        if ( !project && user ) {
            List refs = application.findProjectsForUser(user)
            if ( refs?.size() > 0 )
                project = (Ref) refs[0]
        }

        return project
    }

    public Ref getModel() {
        if ( !isAnalyst() )
            return null;

        Ref user = user
        Ref model = user?.selectedModel
        if ( !model && user ) {
            List<Ref> refs = application.findModelsForUser(user)
            if ( refs.size() > 0 )
                model = (Ref) refs[0]
        }
        return model
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
}