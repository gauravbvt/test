package com.mindalliance.channels.playbook;

import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.Request;

/**
 * Created by IntelliJ IDEA. User: denis Date: Mar 17, 2008 Time: 4:05:22 PM To change this template use File | Settings
 * | File Templates.
 */
public class PlaybookSession extends AuthenticatedWebSession {

    private String name;
    private boolean admin;

    private String project = "Default";

    public PlaybookSession( AuthenticatedWebApplication authenticatedWebApplication, Request request ) {
        super( authenticatedWebApplication, request );
    }

    public boolean authenticate( String name, String password ) {
        if ( "admin".equals( name ) && "admin".equals( password ) )
            admin = true;
        this.name = name;
        return true;
    }

    public Roles getRoles() {
        return isSignedIn()?
               new Roles( admin? Roles.ADMIN : Roles.USER )
             : null;
    }

    public String getName() {
        return name;
    }

    public boolean isAdmin() {
        return admin;
    }

    public String getProject() {
        return project;
    }

    public void setProject( String project ) {
        this.project = project;
    }

}
