/*
 * Created on Apr 28, 2007
 */
package com.mindalliance.channels.data.system;

import java.util.List;

import com.mindalliance.channels.data.elements.project.Project;

/**
 * Queryable project data
 * 
 * @author jf
 */
@SuppressWarnings( "serial")
public class Portfolio extends AbstractQueryable {

    private List<Project> projects;

    /**
     * @return the projects
     */
    public List<Project> getProjects() {
        return projects;
    }

    /**
     * @param projects the projects to set
     */
    public void setProjects( List<Project> projects ) {
        this.projects = projects;
    }

    public void addProject( Project project ) {
    }

    public void remove( Project project ) {
    }

}
