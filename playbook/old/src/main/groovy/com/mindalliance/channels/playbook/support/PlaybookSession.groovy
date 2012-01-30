package com.mindalliance.channels.playbook.support

import com.mindalliance.channels.playbook.mem.SessionMemory
import com.mindalliance.channels.playbook.ref.Ref
import org.apache.wicket.Request
import org.apache.wicket.authentication.AuthenticatedWebApplication
import org.apache.wicket.authorization.strategies.role.Roles
import org.apache.wicket.Session
import com.mindalliance.channels.playbook.query.QueryCache
import javax.servlet.http.HttpServletRequest
import com.mindalliance.channels.playbook.mem.Transactionable
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 21, 2008
* Time: 11:20:47 AM
*/
class PlaybookSession extends KludgeWebSession implements Transactionable, Serializable {

    private static final Logger Log = LoggerFactory.getLogger( PlaybookSession.class );
    private String userId;
    private Ref sessionUser;

    private PlaybookApplication application;

    private SessionMemory memory = new SessionMemory();

    public PlaybookSession(){
        println "allo"
    }

    public String toString() {
        return "Session for $userId"
    }

    public PlaybookSession( Request request ) {
        super( request );
        this.application = (PlaybookApplication) PlaybookApplication.get();
    }

    static PlaybookSession current() {
        return (PlaybookSession)Session.get()
    }


    void takeUserFromRequest( HttpServletRequest request ) {
        userId = request.getParameter("username");
    }

    void invalidate() {
        PlaybookApplication.current().sessionTimedOut(this)
        super.invalidate()

        if ( Log.isInfoEnabled() )
            Log.info( "User " + id + " logged out session " + super.getId() )
    }

    // For testing only
    public void setApplication(PlaybookApplication app) {
        application = app
    }

    public boolean authenticate( String id, String password ) {
        this.userId = id
        Ref aUser = getUser()
        boolean result = aUser && aUser.password == password
        if ( result ) {
            if ( Log.isInfoEnabled() )
                Log.info( "User " + id + " logged in session " + super.getId() )
        } else if ( Log.isWarnEnabled() )
            Log.warn( "Bad password for user " + id )

        return result;
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

    public Ref getTaxonomy() {
        if ( !isAnalyst() )
            return null;

        Ref user = user
        Ref taxonomy = user?.selectedTaxonomy
        if ( !taxonomy && user ) {
            List<Ref> refs = application.findTaxonomiesForUser(user)
            if ( refs.size() > 0 )
                taxonomy = (Ref) refs[0]
        }
        return taxonomy
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

}