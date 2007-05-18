// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import java.util.Set;
import java.util.TreeSet;

import com.mindalliance.channels.DisplayAs;
import com.mindalliance.channels.reference.Type;

/**
 * Something that can be done and/or known by an agent and thus
 * be the subject of communications and/or the cause of events.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 *
 * @opt attributes
 */
public abstract class ScenarioElement extends ModelElement
    implements Comparable<ScenarioElement> {

    private Scenario scenario;
    private String name;
    private String description;
    private Set<Type> types = new TreeSet<Type>();

    /**
     * Default constructor.
     */
    public ScenarioElement() {
        super();
    }

    /**
     * Default constructor.
     * @param scenario the scenario
     */
    public ScenarioElement( Scenario scenario ) {
        super( scenario.getModel() );
        setScenario( scenario );
    }

    /**
     * Return the value of name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the value of name.
     * @param name The new value of name
     */
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * Return the value of scenario.
     */
    @DisplayAs( direct = "in scenario {1}",
                reverse = "contains {1}",
                reverseMany = "contains:"
                )
    public Scenario getScenario() {
        return this.scenario;
    }

    /**
     * Set the value of scenario.
     * @param scenario The new value of scenario
     */
    public void setScenario( Scenario scenario ) {
        this.scenario = scenario;
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
     * Add a type.
     * @param type the type
     */
    public void addType( Type type ) {
        this.types.add( type );
    }

    /**
     * Remove a type.
     * @param type the type
     */
    public void removeType( Type type ) {
        this.types.remove( type );
    }

    /**
     * Compare for order (by name, ignoring case).
     * @param o the element to compare to
     */
    public int compareTo( ScenarioElement o ) {
        return getName().compareToIgnoreCase( o.getName() );
    }

    /**
     * Return the value of description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Set the value of description.
     * @param description The new value of description
     */
    public void setDescription( String description ) {
        this.description = description;
    }

    /**
     * Provide a useful printed representation.
     */
    @Override
    public String toString() {
        return getName();
    }
}
