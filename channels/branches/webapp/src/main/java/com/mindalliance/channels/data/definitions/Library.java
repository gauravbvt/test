// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.definitions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mindalliance.channels.data.support.AuditedObject;

/**
 * Access to all reference data: environment, typologies, locations,
 * policies and templates.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 *
 * @composed - - 1 Typology
 * @composed - - * Location
 * @composed - - * Policy
 * @composed - - * Situation
 */
public class Library extends AuditedObject implements LibraryService {

    private Map<String,Typology> typologies = new HashMap<String,Typology>();
    private List<Location> locations = new ArrayList<Location>();
    private List<Policy> policies = new ArrayList<Policy>();
    private List<Situation> situations = new ArrayList<Situation>();

    /**
     * Default constructor.
     */
    public Library() {
        super();
    }

    /**
     * Return the locations.
     */
    public List<Location> getLocations() {
        return locations;
    }

    /**
     * Set locations.
     * @param locations the locations to set
     */
    public void setLocations( List<Location> locations ) {
        this.locations = locations;
    }

    /**
     * Return the policies.
     */
    public List<Policy> getPolicies() {
        return policies;
    }

    /**
     * Set the policies.
     * @param policies the policies to set
     */
    public void setPolicies( List<Policy> policies ) {
        this.policies = policies;
    }

    /**
     * Return the typologies.
     */
    public Collection<Typology> getTypologies() {
        return typologies.values();
    }

    /**
     * Set the typologies.
     * @param typologies the typologies to set
     */
    public void setTypologies( Collection<Typology> typologies ) {
        for ( Typology typology : typologies ) {
            addTypology( typology );
        }
    }

    /**
     * Get a typology given its name.
     * @param name the name
     */
    public Typology getTypology( String name ) {
        Typology typology = typologies.get( name );
        if ( typology == null ) {
            typology = new Typology( null, name );
            addTypology( typology );
        }
        return typology;

    }

    /**
     * Add a typology.
     * @param typology the typology
     */
    public void addTypology( Typology typology ) {
        typologies.put( typology.getName(), typology );
    }

    /**
     * Return the value of situations.
     */
    public List<Situation> getSituations() {
        return this.situations;
    }

    /**
     * Set the value of situations.
     * @param situations The new value of situations
     */
    public void setSituations( List<Situation> situations ) {
        this.situations = situations;
    }

    /**
     * Add a situation.
     * @param situation the situation
     */
    public void addSituation( Situation situation ) {
        this.situations.add( situation );
    }

    /**
     * Remove a situation.
     * @param situation the situation
     */
    public void removeSituation( Situation situation ) {
        this.situations.remove( situation );
    }
}
