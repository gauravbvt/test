/*
 * Created on Apr 28, 2007
 *
 */
package com.mindalliance.channels.data.beans;

import java.util.ArrayList;
import java.util.List;

import org.acegisecurity.annotation.Secured;

import com.mindalliance.channels.data.elements.User;
import com.mindalliance.channels.data.elements.Project;

/** 
 * Queryable project data
 * @author jf
 *
 */
@SuppressWarnings("serial")
public class Portfolio extends AbstractQueryable {
	
	private List<Project> projects;
	
	public List<Project>getProjects() {
		return getProjects(getAuthenticatedUser());
	}
	
	@Secured( { "ROLE_ADMIN" } )
	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	private List<Project> getProjects(User authenticatedUser) {
		List<Project> visible = new ArrayList<Project>();
		if (authenticatedUser != null) {
			for(Project project : projects) {
				if (project.hasParticipant(authenticatedUser)) {
					visible.add(project);
				}
			}
		}
		// caching?
		return visible;
	}
	
    @Secured( { "ROLE_ADMIN" } )
	public void addProject(Project project) {
		projects.add(project);
	}
	
    @Secured( { "ROLE_ADMIN" } )
	public boolean removeProject(Project project) {
		return projects.remove(project);
	}
    
    public List<User> getProjectManagers(Project project) {
    	List<User> managers = new ArrayList<User>();
    	for (User user : getAuthoritativeUsers(project)) {{
    			managers.add(user);
    		}
    	}
    	// caching?
    	return managers;
    }


}
