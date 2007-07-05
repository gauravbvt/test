// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.system;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.acegisecurity.annotation.Secured;

import com.mindalliance.channels.User;
import com.mindalliance.channels.data.elements.project.Project;
import com.mindalliance.channels.services.PortfolioService;

/**
 * Queryable project data.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
@SuppressWarnings( "serial" )
public class Portfolio extends AbstractQueryable implements PortfolioService {

    private Set<Project> projects = new TreeSet<Project>();

    /**
     * Default constructor.
     */
    public Portfolio() {
    }

    /**
     * Default constructor.
     * @param system the system
     */
    protected Portfolio( System system ) {
        super( system );
    }

    /**
     * Return all the projects.
     */
    @Secured( { "ROLE_ADMIN", "ROLE_RUN_AS_SYSTEM" } )
    public Set<Project> getProjects() {
        return projects;
    }

    /**
     * Return the projects of the authenticated user.
     */
    public Set<Project> getUserProjects() {
        return getProjects( getAuthenticatedUser() );
    }

    /**
     * Get the project managers for a given project.
     * @param project the project
     */
    public List<User> getProjectManagers( Project project ) {
        List<User> managers = new ArrayList<User>();
        for ( User user : getSystem().getAuthoritativeUsers( project ) )
            managers.add( user );

        // caching?
        return managers;
    }

    /**
     * Set all the projects.
     * @param projects the projects to set
     */
    @Secured( "ROLE_RUN_AS_SYSTEM" )
    public void setProjects( Set<Project> projects ) {
        this.projects = new TreeSet<Project>( projects );
    }

    /**
     * Add a project.
     * @param project the project
     */
    @Secured( "ROLE_ADMIN" )
    public void addProject( Project project ) {
        projects.add( project );
    }

    /**
     * Remove a project.
     * @param project the project
     */
    @Secured( "ROLE_ADMIN" )
    public void removeProject( Project project ) {
        projects.remove( project );
    }

    /**
     * Get the projects a user participates in.
     * @param user the user
     */
    @Secured( "ROLE_ADMIN" )
    public Set<Project> getProjects( User user ) {
        Set<Project> visible = new TreeSet<Project>();
        if ( user != null ) {
            for ( Project project : projects ) {
                if ( project.hasParticipant( user ) ) {
                    visible.add( project );
                }
            }
        }
        // caching?
        return visible;
    }

    /**
     * Test if a given user is a manager for the given project.
     * @param user the user
     * @param project the project
     */
    public boolean isManager( User user, Project project ) {
        return getSystem().hasAuthority( user, project );
    }

    /**
     * Test if a user is a participant in a given project.
     * @param user the user
     * @param project the project
     */
    public boolean isParticipant( User user, Project project ) {
        return project.hasParticipant( user );
    }
}
