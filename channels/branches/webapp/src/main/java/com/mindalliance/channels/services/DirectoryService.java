// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.services;

import java.util.List;

import com.mindalliance.channels.JavaBean;
import com.mindalliance.channels.data.elements.resources.Channel;
import com.mindalliance.channels.data.elements.resources.Organization;
import com.mindalliance.channels.data.elements.resources.Person;
import com.mindalliance.channels.data.elements.resources.Team;

/**
 * The directory service.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public interface DirectoryService extends Service, JavaBean {

    /**
     * Return the organizations knows to this service.
     */
    List<Organization> getOrganizations();

    /**
     * Add an organization to the list.
     * @param organization the organization
     */
    void addOrganization( Organization organization );

    /**
     * Remove an organization from the list.
     * @param organization the organization
     */
    void removeOrganization( Organization organization );

    
    /**
     * Return the channels.
     */
    public abstract List<Channel> getChannels();

    /**
     * Set the channels.
     * @param channels the channels
     */
    public abstract void setChannels( List<Channel> channels );

    /**
     * Add a channel.
     * @param channel the channel
     */
    public abstract void addChannel( Channel channel );

    /**
     * Remove a channel.
     * @param channel the channel
     */
    public abstract void removeChannel( Channel channel );

    /**
     * Set the organizations.
     * @param organizations the organizations to set
     */
    public abstract void setOrganizations( List<Organization> organizations );


    /**
     * Return the persons.
     */
    public abstract List<Person> getPersons();

    /**
     * Set the persons.
     * @param persons the persons to set
     */
    public abstract void setPersons( List<Person> persons );

    /**
     * Add a person.
     * @param person the person
     */
    public abstract void addPerson( Person person );

    /**
     * Remove a person.
     * @param person the person
     */
    public abstract void removePerson( Person person );

    /**
     * Return the teams.
     */
    public abstract List<Team> getTeams();

    /**
     * Set the teams.
     * @param teams the teams to set
     */
    public abstract void setTeams( List<Team> teams );

    /**
     * Add a team.
     * @param team the team
     */
    public abstract void addTeam( Team team );

    /**
     * Remove a team.
     * @param team the team
     */
    public abstract void removeTeam( Team team );
}
