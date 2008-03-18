package com.mindalliance.channels.playbook;

import com.mindalliance.channels.playbook.model.Project;
import com.mindalliance.channels.playbook.model.Resource;
import com.mindalliance.channels.playbook.model.Scenario;
import org.apache.wicket.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 */
public class PlaybookApplication extends AuthenticatedWebApplication
{

    Project project;

    /**
     * Constructor
     */
	public PlaybookApplication()
	{
        super();

        // A default project for everyone, for now...
        Project p = new Project();
        p.setName( "Generic" );
        p.add( new Scenario( "Scenario A" ) );
        p.add( new Scenario( "Scenario B" ) );
        p.add( new Scenario( "Scenario C" ) );

        p.add( new Resource( "Resource 1" ) );
        p.add( new Resource( "Resource 2" ) );
        p.add( new Resource( "Anonymous 1" ) );

        setProject( p );
    }

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

    public Project getProject() {
        return project;
    }

    public void setProject( Project project ) {
        this.project = project;
    }

}
