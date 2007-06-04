// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor;

import java.util.ArrayList;
import java.util.Collection;

import com.mindalliance.channels.User;
import com.mindalliance.channels.UserExistsException;
import com.mindalliance.channels.data.elements.project.Project;
import com.mindalliance.channels.data.elements.resources.Channel;
import com.mindalliance.channels.data.elements.resources.Organization;
import com.mindalliance.channels.data.elements.resources.Person;
import com.mindalliance.channels.data.elements.resources.Role;
import com.mindalliance.channels.data.elements.resources.Team;
import com.mindalliance.channels.services.SystemService;


/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class ChooserHelper {

    public ChooserHelper() { 
        
    }
    
    public Collection<Role> findRole(SystemService system, User user) {
        Collection<Role> results = new ArrayList<Role>();
        for (int inx = 0 ; inx < 100 ; inx++) {
            Role role = new Role();
            role.setName( "Role " + inx );
            results.add( role );
        }
        return results;
    }
    
    
    
    public Collection<Organization> findOrganization(SystemService system, User user) {
        Collection<Organization> results = new ArrayList<Organization>();
        if (system != null) {
            results.addAll( system.getDirectoryService().getOrganizations() );
        }
        
        return results;
    }
    
    public void addOrganization(Organization org, SystemService system, User user) {
        system.getDirectoryService().addOrganization( org );
    }
    
    public void removeOrganization(Organization org, SystemService system, User user) {
        system.getDirectoryService().removeOrganization( org );
    }
    
    public Collection<Channel>findChannel(SystemService system, User user) {
        Collection<Channel> results = new ArrayList<Channel>();
        if (system != null) {
            results.addAll( system.getDirectoryService().getChannels() );
        }
        
        return results;
    }
    
    public void addChannel(Channel org, SystemService system, User user) {
        system.getDirectoryService().addChannel( org );
    }
    
    public void removeChannel(Channel org, SystemService system, User user) {
        system.getDirectoryService().removeChannel( org );
    }
    
    public Collection<Person>findPerson(SystemService system, User user) {
        Collection<Person> results = new ArrayList<Person>();
        if (system != null) {
            results.addAll( system.getRegistryService().getPersons() );
        }
        
        return results;
    }
    
    public void addPerson(Person org, SystemService system, User user) {
        //system.getDirectoryService().addPerson( org );
        system.getRegistryService().addPerson( org );
    }
    
    public void removePerson(Person org, SystemService system, User user) {
        //system.getDirectoryService().removePerson( org );
        system.getRegistryService().removePerson( org );
    }
    
    public Collection<Team>findTeam(SystemService system, User user) {
        Collection<Team> results = new ArrayList<Team>();
        if (system != null) {
            results.addAll( system.getDirectoryService().getTeams() );
        }
        
        return results;
    }
    
    public void addTeam(Team org, SystemService system, User user) {
        system.getDirectoryService().addTeam( org );
    }
    
    public void removeTeam(Team org, SystemService system, User user) {
        system.getDirectoryService().removeTeam( org );
    }
    
    public Collection<Project>findProject(SystemService system, User user) {
        Collection<Project> results = new ArrayList<Project>();
        if (system != null) {
            results.addAll( system.getPortfolioService().getProjects() );
        }
        
        return results;
    }
    
    public void addProject(Project org, SystemService system, User user) {
        system.getPortfolioService().addProject( org );
    }
    
    public void removeProject(Project org, SystemService system, User user) {
        system.getPortfolioService().removeProject( org );
    }
    
    public Collection<User>findUser(SystemService system, User user) {
        Collection<User> results = new ArrayList<User>();
        if (system != null) {
            results.addAll( system.getRegistryService().getUsers() );
        }
        
        return results;
    }
    
    public void addUser(User org, SystemService system, User user) {
        try {
            system.getRegistryService().registerUser( org.getName(), org.getUsername(), org.getPassword() );
        } catch ( UserExistsException e ) {
            e.printStackTrace();
        }
    }
    
    public void removeUser(User org, SystemService system, User user) {
        //system.getPortfolioService().removeProject( org );
    }  
}
