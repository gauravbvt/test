// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.system;

import java.beans.PropertyVetoException;
import java.util.Set;
import java.util.TreeSet;

import com.mindalliance.channels.reference.Type;

/**
 * A reachable and available person, role or information resource.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 *
 * @opt attributes
 * @composed - - * Position
 * @navassoc * alternates * Agent
 */
public abstract class Agent extends SystemObject {

    private Set<Type> types = new TreeSet<Type>();
    private String about;
    private Set<Agent> alternates = new TreeSet<Agent>();
    private Set<Position> positions;

    /**
     * Default constructor.
     */
    public Agent() {
    }

    /**
     * Default constructor.
     * @param name the name of this object
     * @throws PropertyVetoException if name clashes with another agent
     */
    public Agent( String name ) throws PropertyVetoException {
        super( name );
    }

    /**
     * Return the value of about.
     */
    public String getAbout() {
        return this.about;
    }

    /**
     * Set the value of about.
     * @param about The new value of about
     */
    public void setAbout( String about ) {
        this.about = about;
    }

    /**
     * Return the value of alternates.
     */
    public Set<Agent> getAlternates() {
        return this.alternates;
    }

    /**
     * Set the value of alternates.
     * @param alternates The new value of alternates
     */
    public void setAlternates( Set<Agent> alternates ) {
        this.alternates = alternates;
    }

    /**
     * Add an alternate to this agent.
     * @param alternate the alternate to add
     */
    public void addAlternate( Agent alternate ) {
        this.alternates.add( alternate );
    }

    /**
     * Remove an alternate from this agent.
     * @param alternate the alternate to delete
     */
    public void removeAlternate( Agent alternate ) {
        this.alternates.remove( alternate );
    }

    /**
     * Return the value of types.
     */
    public Set<Type> getTypes() {
        return this.types;
    }

    /**
     * Set the value of types.
     * @param types The new value of types
     */
    public void setTypes( Set<Type> types ) {
        this.types = types;
    }

    /**
     * Add a type to this agent.
     * @param type the type to add
     */
    public void addType( Type type ) {
        this.types.add( type );
    }

    /**
     * Remove a type from this agent.
     * @param type the type to delete
     */
    public void removeType( Type type ) {
        this.types.remove( type );
    }

    /**
     * Return the value of positions.
     */
    public Set<Position> getPositions() {
        return this.positions;
    }

    /**
     * Set the value of positions.
     * @param positions The new value of positions
     */
    public void setPositions( Set<Position> positions ) {
        this.positions = positions;
    }

    /**
     * Add a position.
     * @param position the position
     */
    public void addPosition( Position position ) {
        this.positions.add( position );
    }

    /**
     * Remove a position.
     * @param position the position
     */
    public void removePosition( Position position ) {
        this.positions.remove( position );
    }
}
