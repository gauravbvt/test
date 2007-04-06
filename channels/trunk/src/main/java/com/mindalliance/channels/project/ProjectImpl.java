// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.project;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.mindalliance.channels.Model;
import com.mindalliance.channels.Project;
import com.mindalliance.channels.User;
import com.mindalliance.channels.reference.RoleType;
import com.mindalliance.channels.system.Organization;
import com.mindalliance.channels.system.ParticipantType;
import com.mindalliance.channels.system.SystemObject;

import static com.mindalliance.channels.system.ParticipantType.Manager;
import static com.mindalliance.channels.system.ParticipantType.Participant;

/**
 * Basic project implementation.
 *
 * @todo synchronize
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 *
 * @opt attributes
 * @composed - - * Model
 * @navassoc * managers * User
 * @navassoc * participants * User
 * @composed - - * Participation
 */
public class ProjectImpl extends SystemObject
        implements Project {

    private Set<Model> models = new HashSet<Model>();
    private Map<User,ParticipantType> participants =
                        Collections.synchronizedMap(
                                new HashMap<User,ParticipantType>() );
    private List<String> objectives = new ArrayList<String>();
    private Set<Participation> participations = new HashSet<Participation>();

    /**
     * Default constructor.
     */
    public ProjectImpl() {
        super();
    }

    /**
     * Default constructor.
     * @param name name of the project
     * @throws PropertyVetoException on name clashes
     */
    public ProjectImpl( String name ) throws PropertyVetoException {
        super( name );
    }

    //---------------------------------
    /**
     * Return the value of models.
     */
    public final Set<Model> getModels() {
        return this.models;
    }

    /**
     * Set the value of models.
     * @param models The new value of models
     */
    public synchronized void setModels( Set<Model> models ) {
        if ( models == null )
            throw new NullPointerException();
        this.models = new HashSet<Model>( models );
    }

    /**
     * Add a model to this project.
     * @param model the new model.
     */
    public synchronized void addModel( Model model ) {
        this.models.add( model );
    }

    /**
     * Remove a model from this project.
     * @param model the model to remove.
     */
    public synchronized void removeModel( Model model ) {
        this.models.remove( model );
    }

    //---------------------------------
    /**
     * Add a manager to this project. Implies an addParticipant.
     * @param manager the new manager, previously defined
     * by an administrator.
     */
    public void addManager( User manager ) {
        this.participants.put( manager, Manager );
    }

    /**
     * Test if given user is a manager of this project.
     * @param user the user to consider.
     * @return true if a manager
     */
    public boolean isManager( User user ) {
        return Manager.equals( this.participants.get( user ) );
    }

    /**
     * Return the managers of this project.
     * Managers can add/remove participants and/or models.
     */
    public Set<User> getManagers() {
        SortedSet<User> result = new TreeSet<User>();
        synchronized ( this.participants ) {
            for ( User u : this.participants.keySet() )
                if ( this.participants.get( u ).equals( Manager ) )
                    result.add( u );
        }

        return Collections.unmodifiableSortedSet( result );
    }

    /**
     * Set project participants to given list.
     * Warning: also resets managers and guests...
     * @param managers the new list of participants.
     */
    public void setManagers( Set<User> managers ) {
        // Demote current managers to regular participants
        for ( User user : getManagers() )
            this.participants.put( user, Participant );

        for ( User user : managers )
            addManager( user );
    }

    /**
     * Remove a manager from this project.
     * Actually demotes the manager to a simple participant
     * @param manager the manager to remove.
     */
    public void removeManager( User manager ) {
        this.participants.put( manager, Participant );
    }

    //---------------------------------
    /**
     * Return the participants in this project.
     */
    public Set<User> getParticipants() {
        return Collections.unmodifiableSortedSet(
                new TreeSet<User>( this.participants.keySet() ) );
    }

    /**
     * Set project participants to given list.
     * Warning: also resets managers and guests...
     * @param participants the new list of participants.
     */
    public void setParticipants( Set<User> participants ) {
        this.participants.clear();
        for ( User user : participants )
            addParticipant( user );
    }

    /**
     * Add a participant in this project.
     * @param participant the new participant, previously defined
     * by an administrator.
     */
    public void addParticipant( User participant ) {
        this.participants.put( participant, Participant );
    }

    /**
     * Remove a participant from this project.
     * @param participant the participant to remove.
     */
    public void removeParticipant( User participant ) {
        this.participants.remove( participant );
    }

    /**
     * Test if given user is a participant of this project.
     * @param user the user to consider.
     * @return true if a participant
     */
    public boolean isParticipant( User user ) {
        ParticipantType type = this.participants.get( user );
        return type != null
            && ( Participant.equals( type )
                 || Manager.equals( type ) );
    }

    /**
     * Return the value of objectives.
     */
    public List<String> getObjectives() {
        return this.objectives;
    }

    /**
     * Set the value of objectives.
     * @param objectives The new value of objectives
     */
    public void setObjectives( List<String> objectives ) {
        this.objectives = objectives;
    }

    /**
     * Add an objective.
     * @param objective the objective
     */
    public void addObjective( String objective ) {
        this.objectives.add( objective );
    }

    /**
     * Remove an objective.
     * @param objective the objective
     */
    public void deleteObjective( String objective ) {
        this.objectives.remove( objective );
    }

    /**
     * Return the value of participations.
     */
    public Set<Participation> getParticipations() {
        return this.participations;
    }

    /**
     * Set the value of participations.
     * @param participations The new value of participations
     */
    public void setParticipations( Set<Participation> participations ) {
        this.participations = participations;
    }

    /**
     * Add a praticipation criteria.
     * @param participation the participation spec.
     */
    public void addParticipation( Participation participation ) {
        this.participations.add( participation );
    }

    /**
     * Remove a praticipation criteria.
     * @param participation the participation spec.
     */
    public void deleteParticipation( Participation participation ) {
        this.participations.remove( participation );
    }

    //=================================================
    /**
     * Blueprint of project participation. This allows some categories of
     * users to become participants without involving the project managers.
     *
     * @opt attributes
     * @navassoc - - 1 Organization
     */
    public static class Participation {

        private boolean allowed;
        private Organization organization;
        private Set<RoleType> roleTypes = new TreeSet<RoleType>();

        /**
         * Default constructor.
         */
        public Participation() {
        }

        /**
         * Return the value of allowed.
         */
        public boolean isAllowed() {
            return this.allowed;
        }

        /**
         * Set the value of allowed.
         * @param allowed The new value of allowed
         */
        public void setAllowed( boolean allowed ) {
            this.allowed = allowed;
        }

        /**
         * Return the value of organization.
         */
        public Organization getOrganization() {
            return this.organization;
        }

        /**
         * Set the value of organization.
         * @param organization The new value of organization
         */
        public void setOrganization( Organization organization ) {
            this.organization = organization;
        }

        /**
         * Return the value of roleTypes.
         */
        public Set<RoleType> getRoleTypes() {
            return this.roleTypes;
        }

        /**
         * Set the value of roleTypes.
         * @param roleTypes The new value of roleTypes
         */
        public void setRoleTypes( Set<RoleType> roleTypes ) {
            this.roleTypes = roleTypes;
        }

        /**
         * Add a role type.
         * @param roleType the role type
         */
        public void addRoleType( RoleType roleType ) {
            this.roleTypes.add( roleType );
        }

        /**
         * Remove a role type.
         * @param roleType the role type
         */
        public void removeRoleType( RoleType roleType ) {
            this.roleTypes.remove( roleType );
        }
    }
}
