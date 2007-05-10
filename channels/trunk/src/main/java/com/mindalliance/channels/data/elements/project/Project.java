/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.elements.project;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.mindalliance.channels.User;
import com.mindalliance.channels.data.elements.AbstractElement;
import com.mindalliance.channels.data.elements.reference.Type;
import com.mindalliance.channels.data.elements.resources.Organization;
import com.mindalliance.channels.data.elements.resources.Role;
import com.mindalliance.channels.data.support.Pattern;
import com.mindalliance.channels.data.support.TypeSet;
import com.mindalliance.channels.util.GUID;

public class Project extends AbstractElement {

	public class Participation {
		
		private Pattern<Role> rolePattern;
		
		/**
		 * Whether a role matches the pattern for participation in the project.
		 * @param role
		 * @return
		 */
		public boolean allows(Role role) {
			return rolePattern.matches(role);
		}
		/**
		 * Whether any of the roles matches the pattern for participation in the project.
		 * @param roles
		 * @return
		 */
		public boolean allows(User user) {
			List<Role> roles = user.getRoles();
			return CollectionUtils.exists(roles, new Predicate() {
				public boolean evaluate(Object object) {
					Role role = (Role)object;
					return allows(role);
				}
			});
		}

		/**
		 * @return the rolePattern
		 */
		public Pattern<Role> getRolePattern() {
			return rolePattern;
		}

		/**
		 * @param rolePattern the rolePattern to set
		 */
		public void setRolePattern(Pattern<Role> rolePattern) {
			this.rolePattern = rolePattern;
		}	
	}
	
	public class InScope {
		
		private Pattern<Organization> organizationPattern;
		
		public boolean includes(Organization organization) {
			if (organizationPattern.matches(organization)) {
				return true;
			}
			else {
				return CollectionUtils.exists(organization.getParents(), new Predicate() {
					public boolean evaluate(Object object) {
						Organization organization = (Organization)object;
						return organizationPattern.matches(organization);
					}
				});
			}
		}
		/**
		 * Whether the organization falls within the scope of the project.
		 * @param organization
		 * @return
		 */
		public boolean isInScope(Organization organization) {
			return organizationPattern.matches(organization);
		}
		/**
		 * @return the organizationPattern
		 */
		public Pattern<Organization> getOrganizationPattern() {
			return organizationPattern;
		}
		/**
		 * @param organizationPattern the organizationPattern to set
		 */
		public void setOrganizationPattern(Pattern<Organization> organizationPattern) {
			this.organizationPattern = organizationPattern;
		}
	}
	
	private TypeSet missions = new TypeSet(Type.MISSION);
	private List<Model> models;
	private List<Participation> participations;
	private List<InScope> inScopes;
	
	
	public Project() {
		super();
	}

	public Project(GUID guid) {
		super(guid);
	}

	/**
	 * Return whether the user matches at least one of the participation criteria.
	 * @param authenticatedUser
	 * @return
	 */
	public boolean hasParticipant(final User authenticatedUser) {
		if (participations == null || participations.isEmpty()) {
			return true; // empty or null means no restriction
		}
		else {
			return CollectionUtils.exists(participations, new Predicate() {
				public boolean evaluate(Object object) {
					Participation participation = (Participation)object;
					return participation.allows(authenticatedUser);
				}
			});
		}
	}
	
	public boolean includes(final Organization organization) {
		if (inScopes == null || inScopes.isEmpty()) {
			return true;  // empty or null means no restriction
		}
		else {
			return CollectionUtils.exists(inScopes, new Predicate() {
				public boolean evaluate(Object object) {
					InScope inScope = (InScope)object;
					return inScope.includes(organization);
				}
			});
		}
	}

	public void addModel(Model model) {
		models.add(model);
	}

	public void removeModel(Model model) {
		models.remove(model);
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
	 * @return the inScopes
	 */
	public List<InScope> getInScopes() {
		return inScopes;
	}

	/**
	 * @param inScopes the inScopes to set
	 */
	public void setInScopes(List<InScope> inScopes) {
		this.inScopes = inScopes;
	}
	/**
	 * 
	 * @param inScope
	 */
	public void addInScope(InScope inScope) {
		inScopes.add(inScope);
	}
	/**
	 * 
	 * @param inScope
	 */
	public void removeInScope(InScope inScope) {
		inScopes.remove(inScope);
	}

	/**
	 * @return the participations
	 */
	public List<Participation> getParticipations() {
		return participations;
	}

	/**
	 * @param participations the participations to set
	 */
	public void setParticipations(List<Participation> participations) {
		this.participations = participations;
	}
	/**
	 * 
	 * @param participation
	 */
	public void addParticipation(Participation participation) {
		participations.add(participation);
	}
	/**
	 * 
	 * @param participation
	 */
	public void removeParticipation(Participation participation) {
		participations.remove(participation);
	}

}
