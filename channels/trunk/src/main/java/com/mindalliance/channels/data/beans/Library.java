/*
 * Created on Apr 28, 2007
 *
 */
package com.mindalliance.channels.data.beans;

import java.util.List;


/**
 * Access to all reference data: environment, typologies, locations, policies and templates
 * @author jf
 *
 */
public class Library extends AbstractQueryable {
	
	private List<Typology> typologies;
	private List<Location> locations;
	private List<Policy> policies;
	private List<Environment> environments;
	private List<Template> templates;
	
	/**
	 * @return the locations
	 */
	public List<Location> getLocations() {
		return locations;
	}
	/**
	 * @param locations the locations to set
	 */
	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}
	/**
	 * @return the policies
	 */
	public List<Policy> getPolicies() {
		return policies;
	}
	/**
	 * @param policies the policies to set
	 */
	public void setPolicies(List<Policy> policies) {
		this.policies = policies;
	}
	/**
	 * @return the templates
	 */
	public List<Template> getTemplates() {
		return templates;
	}
	/**
	 * @param templates the templates to set
	 */
	public void setTemplates(List<Template> templates) {
		this.templates = templates;
	}
	/**
	 * @return the typologies
	 */
	public List<Typology> getTypologies() {
		return typologies;
	}
	/**
	 * @param typologies the typologies to set
	 */
	public void setTypologies(List<Typology> typologies) {
		this.typologies = typologies;
	}

}
