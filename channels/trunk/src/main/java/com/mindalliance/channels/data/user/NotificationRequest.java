/*
 * Created on May 3, 2007
 *
 */
package com.mindalliance.channels.data.user;

import com.mindalliance.channels.data.elements.ElementEvent;
import com.mindalliance.channels.util.GUID;

/**
 * Request to be notified when something happens element matching a specification .
 * The element events to be notified about are created, modified (definition or status), deleted, or any.
 * The set of elements to be notified about is defined in terms of two conjugated scopes:
 * 	User scope:
 * 		All elements created, modified etc...
 *  	- USER = by a given user
 *  	- ROLE = by users in a given type of role
 *  	- ORGANIZATION = by users having a role in a given organization
 *  	- DOMAIN = by users having a role in a given domain
 *  	- ALL = by any user
 *  Element scope"
 * 		All elements created, modified etc...
 *  	- ORGANIZATION = that belong to a given organization 
 *  	- DOMAIN = that belong to a given domain 
 *  	- SCENARIO = that belong to a scenario
 *  	- MODEL = that belong to a model
 *  	- PROJECT = that belong to a project
 *  	- ELEMENT = (a specific element)
 *  	- ALL = (without restriction)
 * By default, the user is notified about events on all elements for which the user is contributor or commenter 
 * An "exclude" notification request means: do *not* notify me about any such that..., and it overrides
 * overlapping "include" notification requests.
 * @author jf
 *
 */
public class NotificationRequest extends UserRequest {
	
	enum UserScope {ALL, USER, ROLE, ORGANIZATION, DOMAIN};
	enum ElementScope {ALL, ELEMENT, ORGANIZATION, SCENARIO, MODEL, PROJECT, DOMAIN};

	private ElementEvent elementEvent;
	private boolean include = true; // else exclude
	private UserScope userScope;
	private GUID userScopeGUID; // guid of user profile, role type, organization, domain type etc., or null
	private ElementScope elementScope;
	private GUID elementScopeGUID; // guid of project, scenario, model, organization, element etc., or null

	public NotificationRequest() {
		super();
	}
	/**
	 * @return the elementEvent
	 */
	public ElementEvent getElementEvent() {
		return elementEvent;
	}
	/**
	 * @param elementEvent the elementEvent to set
	 */
	public void setElementEvent(ElementEvent elementEvent) {
		this.elementEvent = elementEvent;
	}
	/**
	 * @return the elementScope
	 */
	public ElementScope getElementScope() {
		return elementScope;
	}
	/**
	 * @param elementScope the elementScope to set
	 */
	public void setElementScope(ElementScope elementScope) {
		this.elementScope = elementScope;
	}
	/**
	 * @return the elementScopeGUID
	 */
	public GUID getElementScopeGUID() {
		return elementScopeGUID;
	}
	/**
	 * @param elementScopeGUID the elementScopeGUID to set
	 */
	public void setElementScopeGUID(GUID elementScopeGUID) {
		this.elementScopeGUID = elementScopeGUID;
	}
	/**
	 * @return the include
	 */
	public boolean isInclude() {
		return include;
	}
	/**
	 * @param include the include to set
	 */
	public void setInclude(boolean include) {
		this.include = include;
	}
	/**
	 * @return the userScope
	 */
	public UserScope getUserScope() {
		return userScope;
	}
	/**
	 * @param userScope the userScope to set
	 */
	public void setUserScope(UserScope userScope) {
		this.userScope = userScope;
	}
	/**
	 * @return the userScopeGUID
	 */
	public GUID getUserScopeGUID() {
		return userScopeGUID;
	}
	/**
	 * @param userScopeGUID the userScopeGUID to set
	 */
	public void setUserScopeGUID(GUID userScopeGUID) {
		this.userScopeGUID = userScopeGUID;
	}
}
