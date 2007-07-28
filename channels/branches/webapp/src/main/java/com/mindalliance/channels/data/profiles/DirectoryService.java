// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.profiles;

import java.util.List;

import com.mindalliance.channels.JavaBean;
import com.mindalliance.channels.data.definitions.Organization;
import com.mindalliance.channels.data.support.Service;

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
    List<Channel> getChannels();

    /**
     * Set the channels.
     * @param channels the channels
     */
    void setChannels( List<Channel> channels );

    /**
     * Add a channel.
     * @param channel the channel
     */
    void addChannel( Channel channel );

    /**
     * Remove a channel.
     * @param channel the channel
     */
    void removeChannel( Channel channel );

    /**
     * Set the organizations.
     * @param organizations the organizations to set
     */
    void setOrganizations( List<Organization> organizations );

    /**
     * Return the persons.
     */
    List<Person> getPersons();

    /**
     * Set the persons.
     * @param persons the persons to set
     */
    void setPersons( List<Person> persons );

    /**
     * Add a person.
     * @param person the person
     */
    void addPerson( Person person );

    /**
     * Remove a person.
     * @param person the person
     */
    void removePerson( Person person );

    /**
     * Return the teams.
     */
    List<Team> getTeams();

    /**
     * Set the teams.
     * @param teams the teams to set
     */
    void setTeams( List<Team> teams );

    /**
     * Add a team.
     * @param team the team
     */
    void addTeam( Team team );

    /**
     * Remove a team.
     * @param team the team
     */
    void removeTeam( Team team );
}
