// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.mindalliance.channels.definitions.Situation;
import com.mindalliance.channels.definitions.TypedObject;
import com.mindalliance.channels.support.CollectionType;
import com.mindalliance.channels.support.GUID;

/**
 * The model.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 *
 * @composed - - * Storyline
 * @navassoc - - * Situation
 */
public class Scenario extends TypedObject {

    private Set<Storyline> storylines = new TreeSet<Storyline>();
    private List<Situation> environments = new ArrayList<Situation>();

    /**
     * Default constructor.
     */
    public Scenario() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the GUID
     */
    public Scenario( GUID guid ) {
        super( guid );
    }

    /**
     * Return the environments where this model applies.
     */
    @CollectionType( type = Situation.class )
    public List<Situation> getEnvironments() {
        return environments;
    }

    /**
     * Set the environments where this model applies.
     * @param environments the environments to set
     */
    public void setEnvironments( List<Situation> environments ) {
        this.environments = environments;
    }

    /**
     * Add an environment.
     * @param environment the environment
     */
    public void addEnvironment( Situation environment ) {
        environments.add( environment );
    }

    /**
     * Remove an environment.
     * @param environment the environment
     */
    public void removeEnvironment( Situation environment ) {
        environments.remove( environment );
    }

    /**
     * Return the storylines in this model.
     */
    @CollectionType( type = Storyline.class )
    public Set<Storyline> getStorylines() {
        return storylines;
    }

    /**
     * Sets the storylines in this model.
     * @param storylines the storylines to set
     */
    public void setStorylines( Set<Storyline> storylines ) {
        this.storylines = new TreeSet<Storyline>( storylines );
    }

    /**
     * Add a storyline.
     * @param storyline the storyline
     */
    public void addStoryline( Storyline storyline ) {
        storylines.add( storyline );
    }

    /**
     * Remove a storyline.
     * @param storyline the storyline
     */
    public void removeStoryline( Storyline storyline ) {
        storylines.remove( storyline );
    }

}
