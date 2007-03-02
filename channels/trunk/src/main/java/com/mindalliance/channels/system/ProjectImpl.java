// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.system;

import java.beans.PropertyVetoException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.mindalliance.channels.Model;
import com.mindalliance.channels.Project;
import com.mindalliance.channels.User;
import com.mindalliance.channels.util.AbstractJavaBean;

import static com.mindalliance.channels.system.ParticipantType.*;

/**
 * Basic project implementation.
 *
 * @todo synchronize
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public class ProjectImpl extends AbstractJavaBean
        implements Project, Comparable<ProjectImpl> {

    private String name;
    private Set<Model> models = new HashSet<Model>();
    private Map<User,ParticipantType> participants =
                        Collections.synchronizedMap(
                                new HashMap<User,ParticipantType>() );

    /**
     * Default constructor.
     */
    public ProjectImpl() {
        this( "No Name" );
    }

    /**
     * Default constructor.
     * @param name name of the project
     */
    public ProjectImpl( String name ) {
        super();
        this.name = name;
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
        this.models = models;
    }

    /**
     * Add a model to this project.
     * @param model the new model.
     */
    public synchronized void addModel( Model model ) {
        this.models.add( model );
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
     * Remove a model from this project.
     * @param model the model to remove.
     */
    public synchronized void removeModel( Model model ) {
        this.models.remove( model );
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

    //---------------------------------
    /**
     * Return the value of name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the value of name.
     * @param name The new value of name
     * @throws PropertyVetoException if name conflicts with others.
     */
    public void setName( String name ) throws PropertyVetoException {
        if ( name == null )
            throw new NullPointerException();
        this.name = name;
    }

    //---------------------------------
    /**
     * Compares this object with the specified object for order.
     * @param o the specified object
     */
    public int compareTo( ProjectImpl o ) {
        return getName().trim().compareToIgnoreCase( o.getName().trim() );
    }

    /**
     * Return  a string representation of the object.
     */
    @Override
    public String toString() {
        return getName();
    }
}
