// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.user;

import com.mindalliance.channels.data.elements.ElementEvent;
import com.mindalliance.channels.util.GUID;

/**
 * Request to be notified when something happens element matching a
 * specification . The element events to be notified about are
 * created, modified (definition or status), deleted, or any. The set
 * of elements to be notified about is defined in terms of two
 * conjugated scopes: User scope: All elements created, modified
 * etc... - USER = by a given user - ROLE = by users in a given type
 * of role - ORGANIZATION = by users having a role in a given
 * organization - DOMAIN = by users having a role in a given domain -
 * ALL = by any user Element scope" All elements created, modified
 * etc... - ORGANIZATION = that belong to a given organization -
 * DOMAIN = that belong to a given domain - SCENARIO = that belong to
 * a scenario - MODEL = that belong to a model - PROJECT = that belong
 * to a project - ELEMENT = (a specific element) - ALL = (without
 * restriction) By default, the user is notified about events on all
 * elements for which the user is contributor or commenter An
 * "exclude" notification request means: do *not* notify me about any
 * such that..., and it overrides overlapping "include" notification
 * requests.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class NotificationRequest extends UserRequest {

    /**
     * The user scope of a notification request.
     */
    enum UserScope {
        /** Notify all users. */
        ALL, USER, ROLE, ORGANIZATION, DOMAIN
    };

    /**
     * The element scope of a notification request.
     */
    enum ElementScope {
        /** Notify for all elements. */
        ALL, ELEMENT, ORGANIZATION, SCENARIO, MODEL, PROJECT, DOMAIN
    };

    private ElementEvent elementEvent;
    private boolean include = true;
    private UserScope userScope;
    private GUID userScopeGUID;
    private ElementScope elementScope;
    private GUID elementScopeGUID;

    /**
     * Default constructor.
     */
    public NotificationRequest() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public NotificationRequest( GUID guid ) {
        super( guid );
    }

    /**
     * Return the element event.
     */
    public ElementEvent getElementEvent() {
        return elementEvent;
    }

    /**
     * Set the element event.
     * @param elementEvent the elementEvent to set
     */
    public void setElementEvent( ElementEvent elementEvent ) {
        this.elementEvent = elementEvent;
    }

    /**
     * Return the element scope.
     */
    public ElementScope getElementScope() {
        return elementScope;
    }

    /**
     * Set the element scope.
     * @param elementScope the elementScope to set
     */
    public void setElementScope( ElementScope elementScope ) {
        this.elementScope = elementScope;
    }

    /**
     * Return the guid of project, scenario, model, organization,
     * element etc., or null.
     */
    public GUID getElementScopeGUID() {
        return elementScopeGUID;
    }

    /**
     * Set the element scope guid.
     * @param elementScopeGUID the elementScopeGUID to set
     */
    public void setElementScopeGUID( GUID elementScopeGUID ) {
        this.elementScopeGUID = elementScopeGUID;
    }

    /**
     * If this request is included.
     */
    public boolean isInclude() {
        return include;
    }

    /**
     * Mark this request as included.
     * @param include the include to set
     */
    public void setInclude( boolean include ) {
        this.include = include;
    }

    /**
     * Return the user scope.
     */
    public UserScope getUserScope() {
        return userScope;
    }

    /**
     * Set the user scope.
     * @param userScope the userScope to set
     */
    public void setUserScope( UserScope userScope ) {
        this.userScope = userScope;
    }

    /**
     * Return the guid of user profile, role type, organization,
     * domain type etc.
     */
    public GUID getUserScopeGUID() {
        return userScopeGUID;
    }

    /**
     * Set the user scope GUID.
     * @param userScopeGUID the userScopeGUID to set
     */
    public void setUserScopeGUID( GUID userScopeGUID ) {
        this.userScopeGUID = userScopeGUID;
    }
}
