/*
 * Created on Apr 28, 2007
 */
package com.mindalliance.channels.services.base;

import java.util.ArrayList;
import java.util.List;

import org.acegisecurity.annotation.Secured;

import com.mindalliance.channels.User;
import com.mindalliance.channels.data.elements.project.Model;
import com.mindalliance.channels.data.elements.project.Project;
import com.mindalliance.channels.data.system.Portfolio;
import com.mindalliance.channels.data.system.System;
import com.mindalliance.channels.services.SystemService;
import com.mindalliance.channels.services.PortfolioService;

/**
 * Implementation of the Portfolio service.
 * 
 * @author jf
 */
public class PortfolioServiceImpl extends AbstractService implements
        PortfolioService {

    public PortfolioServiceImpl( SystemService systemService ) {
        super( systemService );
    }

    /**
     * @return the portfolio
     */
    private Portfolio getPortfolio() {
        return getSystem().getPortfolio();
    }

    public List<Project> getProjects() {
        return getProjects( getAuthenticatedUser() );
    }

    @Secured( { "ROLE_ADMIN" })
    public void setProjects( List<Project> projects ) {
        getPortfolio().setProjects( projects );
    }

    public List<Project> getProjects( User authenticatedUser ) {
        List<Project> visible = new ArrayList<Project>();
        if ( authenticatedUser != null ) {
            for ( Project project : getPortfolio().getProjects() ) {
                if ( project.hasParticipant( authenticatedUser ) ) {
                    visible.add( project );
                }
            }
        }
        // caching?
        return visible;
    }

    @Secured( { "ROLE_ADMIN" })
    public void addProject( Project project ) {
        getPortfolio().addProject( project );
    }

    @Secured( { "ROLE_ADMIN" })
    public void removeProject( Project project ) {
        getPortfolio().remove( project );
    }

    public List<User> getProjectManagers( Project project ) {
        List<User> managers = new ArrayList<User>();
        for ( User user : getAuthoritativeUsers( project ) ) {
            {
                managers.add( user );
            }
        }
        // caching?
        return managers;
    }

    // TODO obsolete
    public void addModel( Project project, Model model ) {
        project.addModel( model );
    }

    public void removeModel( Model model ) {
        model.getProject().removeModel( model );
    }

    /**
     * Returns whether a user is manager of a project.
     */
    public boolean isManager( User user, Project project ) {
        return hasAuthority( user, project );
    }

    /**
     * Returns whether a user is a participant in a project.
     */
    public boolean isParticipant( User user, Project project ) {
        return project.hasParticipant( user );
    }

}
