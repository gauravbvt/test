/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.elements;

import java.util.List;

import com.mindalliance.channels.data.beans.Pattern;
import com.mindalliance.channels.data.beans.Type;
import com.mindalliance.channels.data.elements.User;
import com.mindalliance.channels.data.TypeSet;

public class Project extends AbstractElement {

	private TypeSet missions = new TypeSet(Type.MISSION);
	private List<Model> models;
	private List<Pattern<Role>> participation;
	private List<Pattern<Organization>> scope;
	
	/**
	 * Return whether the user matches at least one of the participation criteria.
	 * @param authenticatedUser
	 * @return
	 */
	public boolean hasParticipant(User authenticatedUser) {
		return false;
	}

	/**
	 * @return the missions
	 */
	public TypeSet getMissions() {
		return missions;
	}

	/**
	 * @param missions the missions to set
	 */
	public void setMissions(TypeSet missions) {
		this.missions = missions;
	}

	/**
	 * @return the models
	 */
	public List<Model> getModels() {
		return models;
	}

	/**
	 * @param models the models to set
	 */
	public void setModels(List<Model> models) {
		this.models = models;
	}

	/**
	 * @return the participation
	 */
	public List<Pattern<Role>> getParticipation() {
		return participation;
	}

	/**
	 * @param participation the participation to set
	 */
	public void setParticipation(List<Pattern<Role>> participation) {
		this.participation = participation;
	}

	/**
	 * @return the scope
	 */
	public List<Pattern<Organization>> getScope() {
		return scope;
	}

	/**
	 * @param scope the scope to set
	 */
	public void setScope(List<Pattern<Organization>> scope) {
		this.scope = scope;
	}
	
}
