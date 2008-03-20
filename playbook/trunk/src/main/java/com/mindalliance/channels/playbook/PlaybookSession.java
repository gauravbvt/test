package com.mindalliance.channels.playbook;

import com.mindalliance.channels.playbook.model.Project;
import com.mindalliance.channels.playbook.model.Todo;
import com.mindalliance.channels.playbook.mem.SessionMemory;
import org.apache.wicket.Request;
import org.apache.wicket.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authorization.strategies.role.Roles;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: denis Date: Mar 17, 2008 Time: 4:05:22 PM To change this template use File | Settings
 * | File Templates.
 */
public class PlaybookSession extends AuthenticatedWebSession {

    private String name;
    private boolean admin;

    private Project project;
    private List<Todo> todos = new ArrayList<Todo>();

    private SessionMemory memory = new SessionMemory();

    public PlaybookSession( AuthenticatedWebApplication application, Request request ) {
        super( application, request );

        setProject( ((PlaybookApplication) application).getProject() );

        todos.add( new Todo( "Todo 1", "High", new Date( System.currentTimeMillis() ) ));
        todos.add( new Todo( "Todo 2", "Medium", new Date( System.currentTimeMillis()+24*60*60*1000L ) ));
        todos.add( new Todo( "Todo 3", "Low", new Date( System.currentTimeMillis()+5*24*60*60*1000L ) ));
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

    public Project getProject() {
        return project;
    }

    public void setProject( Project project ) {
        this.project = project;
    }

    public List<Todo> getTodos() {
        return todos;
    }

    public void setTodos( List<Todo> todos ) {
        this.todos = todos;
    }


    public SessionMemory getMemory() {
        return memory;
    }
}
