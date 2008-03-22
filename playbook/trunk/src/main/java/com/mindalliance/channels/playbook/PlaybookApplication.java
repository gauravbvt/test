package com.mindalliance.channels.playbook;

import com.mindalliance.channels.playbook.mem.ApplicationMemory;
import com.mindalliance.channels.playbook.model.Organization;
import com.mindalliance.channels.playbook.model.Participation;
import com.mindalliance.channels.playbook.model.Person;
import com.mindalliance.channels.playbook.model.Project;
import com.mindalliance.channels.playbook.model.Scenario;
import com.mindalliance.channels.playbook.model.User;
import org.apache.wicket.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 */
public class PlaybookApplication extends AuthenticatedWebApplication
{
    Map<String, Project> projects = new HashMap<String,Project>();
    Map<String, User> users = new HashMap<String,User>();
    Set<Participation> participations = new HashSet<Participation>();

    private ApplicationMemory memory;
    Project project;

    /**
     * Constructor
     */
	public PlaybookApplication() {
        super();
        memory = new ApplicationMemory(this);
        load();
    }

    private void load() {
        add( defaultProject() );
        User user = new User( "admin", "Administrator", "admin" );
        user.setAdmin( true );
        add( user );
    }

    private Project defaultProject() {
        // A default project for everyone, for now...
        Project p = new Project();
        p.setName( "Generic" );
        p.addScenario( (Scenario) new Scenario( "Scenario A" ) );
        p.addScenario( (Scenario) new Scenario( "Scenario B" ) );
        p.addScenario( (Scenario) new Scenario( "Scenario C" ) );

        p.addResource( new Person( "Joe Shmoe" ) );
        p.addResource( new Organization( "ACME Inc." ) );
//        p.add( new Resource( "Anonymous 1" ) );

        return p;
    }

    //----------------------
    @Override
    public Class getHomePage() {
        return HomePage.class;
    }

    @Override
    protected Class<? extends AuthenticatedWebSession> getWebSessionClass() {
        return PlaybookSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return LoginPage.class;
    }

    //----------------------
    public List<Project> getProjects() {
        return new ArrayList<Project>( projects.values() );
    }

    public void add( Project project ) {
        projects.put( project.getName(), project );
    }

    public void add( User user ) {
        users.put( user.getId(), user );
    }

    public void add( Participation p ) {
        participations.add( p );
    }

    public void remove( Project project ) {
        projects.remove( project.getName() );
    }

    public void remove( User user ) {
        projects.remove( user.getId() );
    }

    public void remove( Participation p ) {
        participations.remove( p );
    }

    //----------------------
    public User getUser( String id ) {
        return users.get( id );
    }

    public Project getProject( String name ) {
        return projects.get( name );
    }

    public List<Project> getProjects( User user ) {
        List<Project> result = new ArrayList<Project>();
        if ( user.getAdmin() )
            result.addAll( projects.values() );
        else
            for ( Participation p : participations )
                if ( p.getUser() == user )
                    result.add( p.getProject() );

        return result;
    }

    public Participation getParticipation( Project project, User user ) {
        for ( Participation p : participations )
            if ( p.getUser() == user && p.getProject() == project )
                return p;

        return null;
    }

    //----------------------
    public ApplicationMemory getMemory() {
        return memory;
    }
}
