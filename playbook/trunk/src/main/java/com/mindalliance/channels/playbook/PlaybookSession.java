package com.mindalliance.channels.playbook;

import com.mindalliance.channels.playbook.mem.SessionMemory;
import com.mindalliance.channels.playbook.model.Participation;
import com.mindalliance.channels.playbook.model.Project;
import com.mindalliance.channels.playbook.model.User;
import org.apache.wicket.Request;
import org.apache.wicket.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authorization.strategies.role.Roles;

/**
 * ...
 */
public class PlaybookSession extends AuthenticatedWebSession {

    private Participation participation;
    private User user;
    private Project project;

    private PlaybookApplication application;

    private SessionMemory memory = new SessionMemory();

    public PlaybookSession( AuthenticatedWebApplication application, Request request ) {
        super( application, request );
        this.application = (PlaybookApplication) application;

    }

    public boolean authenticate( String name, String password ) {
        participation = null;
        user = application.getUser( name );
        if ( user != null && user.getPassword().equals( password ) ) {
            project = application.getProjects( user ).get( 0 );
            participation = application.getParticipation( project, user );
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
        return participation != null && participation.getUser().getAdmin();
    }

    public Participation getParticipation() {
        return participation;
    }

    public Project getProject() {
        return project;
    }

    public SessionMemory getMemory() {
        return memory;
    }
}
