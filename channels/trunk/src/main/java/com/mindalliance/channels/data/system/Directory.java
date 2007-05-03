/*
 * Created on Apr 28, 2007
 *
 */
package com.mindalliance.channels.data.system;

import java.util.List;

import com.mindalliance.channels.data.elements.resources.Channel;
import com.mindalliance.channels.data.elements.resources.Organization;
import com.mindalliance.channels.data.elements.resources.Person;
import com.mindalliance.channels.data.elements.resources.Team;

/**
 * All resources (organizations, persons and teams.
 * @author jf
 *
 */
public class Directory extends AbstractQueryable {

	private List<Organization> organizations;
	private List<Person> persons;
	private List<Team> teams;
	private List<Channel> channels;


}
