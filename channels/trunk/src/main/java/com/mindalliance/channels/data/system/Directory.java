/*
 * Created on Apr 28, 2007
 */
package com.mindalliance.channels.data.system;

import java.util.List;

import com.mindalliance.channels.data.elements.resources.Channel;
import com.mindalliance.channels.data.elements.resources.Organization;
import com.mindalliance.channels.data.elements.resources.Person;
import com.mindalliance.channels.data.elements.resources.Team;

/**
 * All resources (organizations, persons and teams.
 * 
 * @author jf
 */
public class Directory extends AbstractQueryable {

    private List<Organization> organizations;
    private List<Person> persons;
    private List<Team> teams;
    private List<Channel> channels;

    /**
     * @return the system
     */
    public List<Channel> getChannels() {
        return channels;
    }

    /**
     * @param system the system to set
     */
    public void setChannels( List<Channel> channels ) {
        this.channels = channels;
    }

    /**
     * @return the organizations
     */
    public List<Organization> getOrganizations() {
        return organizations;
    }

    /**
     * @param organizations the organizations to set
     */
    public void setOrganizations( List<Organization> organizations ) {
        this.organizations = organizations;
    }

    /**
     * @return the persons
     */
    public List<Person> getPersons() {
        return persons;
    }

    /**
     * @param persons the persons to set
     */
    public void setPersons( List<Person> persons ) {
        this.persons = persons;
    }

    /**
     * @return the teams
     */
    public List<Team> getTeams() {
        return teams;
    }

    /**
     * @param teams the teams to set
     */
    public void setTeams( List<Team> teams ) {
        this.teams = teams;
    }

}
