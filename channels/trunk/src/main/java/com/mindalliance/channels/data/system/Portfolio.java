/*
 * Created on Apr 28, 2007
 */
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
 * Queryable project data
 * 
 * @author jf
 */
@SuppressWarnings( "serial")
public class Portfolio extends AbstractQueryable implements PortfolioService {

    private Set<Project> projects;

    public Portfolio() {}
    
    protected Portfolio( System system ) {
        super(system);
    }

    /**
     * @return the projects
     */
    @Secured( {"ROLE_ADMIN", "ROLE_RUN_AS_SYSTEM"})
    public Set<Project> getProjects() {
        return projects;
    }
    
    /**
     * Return the projects of the authenticated user
     */
    public Set<Project> getUserProjects() {
        return getProjects( getAuthenticatedUser() );
    }

    public List<User> getProjectManagers( Project project ) {
        List<User> managers = new ArrayList<User>();
        for ( User user : system.getAuthoritativeUsers( project ) ) {
            {
                managers.add( user );
            }
        }
        // caching?
        return managers;
    }

    /**
     * @param projects the projects to set
     */
    @Secured("ROLE_RUN_AS_SYSTEM")
    public void setProjects( Set<Project> projects ) {
        this.projects = projects;
    }

    @Secured("ROLE_ADMIN")
    public void addProject( Project project ) {
        projects.add(project);
    }

    /**
     * Get the projects a user participates in
     */
    @Secured("ROLE_ADMIN")
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

    public boolean isManager( User user, Project project ) {
        return system.hasAuthority( user, project );
    }

    public boolean isParticipant( User user, Project project ) {
        return project.hasParticipant( user );
    }

    @Secured("ROLE_ADMIN")
    public void removeProject( Project project ) {
        projects.remove( project );
    }

}
