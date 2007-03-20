// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels;

import java.util.Set;

import org.acegisecurity.annotation.Secured;

/**
 * A modeling project.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 *
 * @assoc "*" - "*" User
 * @composed "" - "0..*" Model
 */
public interface Project extends JavaBean {

    /**
     * Return a descriptive name for the project.
     */
    String getName();

    /**
     * Return the participants in this project.
     */
    Set<User> getParticipants();

    /**
     * Add a participant in this project.
     * @param participant the new participant, previously defined
     * by an administrator.
     */
    @Secured( { "ROLE_ADMIN", "ROLE_MANAGER" } )
    void addParticipant( User participant );

    /**
     * Remove a participant from this project.
     * @param participant the participant to remove.
     */
    @Secured( { "ROLE_ADMIN", "ROLE_MANAGER" } )
    void removeParticipant( User participant );

    /**
     * Return the managers of this project.
     * Managers can add/remove participants and/or models.
     */
    Set<User> getManagers();

    /**
     * Add a manager to this project. Implies an addParticipant.
     * @param manager the new manager, previously defined
     * by an administrator.
     */
    @Secured( { "ROLE_ADMIN" } )
    void addManager( User manager );

    /**
     * Remove a manager from this project.
     * @param manager the manager to remove.
     */
    @Secured( { "ROLE_ADMIN" } )
    void removeManager( User manager );

    /**
     * Test if given user is a manager of this project.
     * @param user the user to consider.
     * @return true if a manager
     */
    boolean isManager( User user );

    /**
     * Test if given user is a participant of this project.
     * @param user the user to consider.
     * @return true if a participant
     */
    boolean isParticipant( User user );

    /**
     * Return the models resulting from this project (usually only one).
     */
    Set<Model> getModels();

    /**
     * Add a model to this project.
     * @param model the new model.
     */
    @Secured( { "ROLE_ADMIN", "ROLE_MANAGER" } )
    void addModel( Model model );

    /**
     * Remove a model from this project.
     * @param model the model to remove.
     */
    @Secured( { "ROLE_ADMIN", "ROLE_MANAGER" } )
    void removeModel( Model model );
}
