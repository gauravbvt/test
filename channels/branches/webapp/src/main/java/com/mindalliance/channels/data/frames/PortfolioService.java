// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.frames;

import java.util.Set;

import org.acegisecurity.annotation.Secured;

import com.mindalliance.channels.User;
import com.mindalliance.channels.data.support.Service;

/**
 * The portfolio service.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public interface PortfolioService extends Service {

    /**
     * Get all projects.
     */
    @Secured( { "ROLE_ADMIN", "ROLE_RUN_AS_SYSTEM" } )
    Set<Project> getProjects();

    /**
     * Get all projects visible to a user.
     * @param user the user
     */
    @Secured( "ROLE_USER" )
    Set<Project> getProjects( User user );

    /**
     * Test if given user is a manager of a project.
     * @param user the user to consider.
     * @param project the project
     * @return true if a manager
     */
    @Secured( "ROLE_USER" )
    boolean isManager( User user, Project project );

    /**
     * Test if given user is a participant of a project.
     * @param user the user to consider.
     * @param project the project
     * @return true if a participant
     */
    @Secured( "ROLE_USER" )
    boolean isParticipant( User user, Project project );

    /**
     * Add a new project.
     * @param project the new project.
     */
    @Secured( "ROLE_ADMIN" )
    void addProject( Project project );

    /**
     * Remove a project.
     * @param project the project to get rid of.
     */
    @Secured( "ROLE_ADMIN" )
    void removeProject( Project project );

}
