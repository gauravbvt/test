/*
 * Created on Apr 25, 2007
 */
package com.mindalliance.channels.services;

import java.util.List;

import org.acegisecurity.annotation.Secured;

import com.mindalliance.channels.data.elements.project.Model;
import com.mindalliance.channels.data.elements.project.Project;
import com.mindalliance.channels.User;

public interface PortfolioService extends Service {

    /**
     * Get all projects visible to a user.
     * 
     * @param user
     * @return
     */
    @Secured( "ROLE_USER")
    List<Project> getProjects( User user );

    /**
     * Return the projects of the authenticated user
     */
   public List<Project> getUserProjects();
    
    /**
     * Test if given user is a manager of this project.
     * 
     * @param user the user to consider.
     * @return true if a manager
     */
    @Secured( "ROLE_USER")
    boolean isManager( User user, Project project );

    /**
     * Test if given user is a participant of this project.
     * 
     * @param user the user to consider.
     * @return true if a participant
     */
    @Secured( "ROLE_USER")
    boolean isParticipant( User user, Project project );

    /**
     * Add a new project.
     * 
     * @param project the new project.
     */
    @Secured( "ROLE_ADMIN")
    void addProject( Project project );

    /**
     * Remove a project.
     * 
     * @param project the project to get rid of.
     */
    @Secured( "ROLE_ADMIN")
    void removeProject( Project project );

}
