/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.elements;

import java.util.List;

public class Model extends AbstractElement {
	
	private Project project;
	private List<Scenario> scenarios;
	private List<Environment> environments;
	
	/**
	 * @return the environments
	 */
	public List<Environment> getEnvironments() {
		return environments;
	}
	/**
	 * @param environments the environments to set
	 */
	public void setEnvironments(List<Environment> environments) {
		this.environments = environments;
	}
	/**
	 * @return the scenarios
	 */
	public List<Scenario> getScenarios() {
		return scenarios;
	}
	/**
	 * @param scenarios the scenarios to set
	 */
	public void setScenarios(List<Scenario> scenarios) {
		this.scenarios = scenarios;
	}
	/**
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}
	/**
	 * @param project the project to set
	 */
	public void setProject(Project project) {
		this.project = project;
	}

}
