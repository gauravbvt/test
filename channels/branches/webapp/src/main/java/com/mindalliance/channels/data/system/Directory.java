// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.system;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.data.elements.resources.Channel;
import com.mindalliance.channels.data.elements.resources.Organization;
import com.mindalliance.channels.data.elements.resources.Person;
import com.mindalliance.channels.data.elements.resources.Team;
import com.mindalliance.channels.services.DirectoryService;

/**
 * All resources (organizations, persons and teams.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Directory extends AbstractQueryable implements DirectoryService {

    private List<Organization> organizations = new ArrayList<Organization>();
    private List<Person> persons = new ArrayList<Person>();
    private List<Team> teams = new ArrayList<Team>();
    private List<Channel> channels = new ArrayList<Channel>();

    /**
     * Default constructor.
     */
    public Directory() {
    }

    /**
     * Default constructor.
     * @param system the system
     */
    protected Directory( System system ) {
        super( system );
    }

    /**
     * Return the channels.
     */
    public List<Channel> getChannels() {
        return channels;
    }

    /**
     * Set the channels.
     * @param channels the channels
     */
    public void setChannels( List<Channel> channels ) {
        this.channels = channels;
    }

    /**
     * Add a channel.
     * @param channel the channel
     */
    public void addChannel( Channel channel ) {
        this.channels.add( channel );
    }

    /**
     * Remove a channel.
     * @param channel the channel
     */
    public void removeChannel( Channel channel ) {
        this.channels.remove( channel );
    }

    /**
     * Return the organizations.
     */
    public List<Organization> getOrganizations() {
        return organizations;
    }

    /**
     * Set the organizations.
     * @param organizations the organizations to set
     */
    public void setOrganizations( List<Organization> organizations ) {
        this.organizations = organizations;
    }

    /**
     * Add an organization.
     * @param organization the organization
     */
    public void addOrganization( Organization organization ) {
        this.organizations.add( organization );
    }

    /**
     * Remove an organization.
     * @param organization the organization
     */
    public void removeOrganization( Organization organization ) {
        this.organizations.remove( organization );
    }

    /**
     * Return the persons.
     */
    public List<Person> getPersons() {
        return persons;
    }

    /**
     * Set the persons.
     * @param persons the persons to set
     */
    public void setPersons( List<Person> persons ) {
        this.persons = persons;
    }

    /**
     * Add a person.
     * @param person the person
     */
    public void addPerson( Person person ) {
        this.persons.add( person );
    }

    /**
     * Remove a person.
     * @param person the person
     */
    public void removePerson( Person person ) {
        this.persons.remove( person );
    }

    /**
     * Return the teams.
     */
    public List<Team> getTeams() {
        return teams;
    }

    /**
     * Set the teams.
     * @param teams the teams to set
     */
    public void setTeams( List<Team> teams ) {
        this.teams = teams;
    }

    /**
     * Add a team.
     * @param team the team
     */
    public void addTeam( Team team ) {
        this.teams.add( team );
    }

    /**
     * Remove a team.
     * @param team the team
     */
    public void removeTeam( Team team ) {
        this.teams.remove( team );
    }
}
