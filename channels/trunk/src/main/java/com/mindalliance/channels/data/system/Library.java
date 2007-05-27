// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.system;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mindalliance.channels.data.elements.scenario.Environment;
import com.mindalliance.channels.data.reference.Location;
import com.mindalliance.channels.data.reference.Policy;
import com.mindalliance.channels.data.reference.Template;
import com.mindalliance.channels.data.reference.Typology;
import com.mindalliance.channels.services.LibraryService;

/**
 * Access to all reference data: environment, typologies, locations,
 * policies and templates.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Library extends AbstractQueryable implements LibraryService {

    private Map<String,Typology> typologies = new HashMap<String,Typology>();
    private List<Location> locations = new ArrayList<Location>();
    private List<Policy> policies = new ArrayList<Policy>();
    private List<Environment> environments = new ArrayList<Environment>();
    private List<Template> templates = new ArrayList<Template>();

    /**
     * Default constructor.
     */
    public Library() {
        super();
    }

    /**
     * Default constructor.
     * @param system the system
     */
    protected Library( System system ) {
        super( system );
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
     * Return the templates.
     */
    public List<Template> getTemplates() {
        return templates;
    }

    /**
     * Set the templates.
     * @param templates the templates to set
     */
    public void setTemplates( List<Template> templates ) {
        this.templates = templates;
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
            typology = new Typology( name );
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
     * Return the value of environments.
     */
    public List<Environment> getEnvironments() {
        return this.environments;
    }

    /**
     * Set the value of environments.
     * @param environments The new value of environments
     */
    public void setEnvironments( List<Environment> environments ) {
        this.environments = environments;
    }

    /**
     * Add an environment.
     * @param environment the environment
     */
    public void addEnvironment( Environment environment ) {
        this.environments.add( environment );
    }

    /**
     * Remove an environment.
     * @param environment the environment
     */
    public void removeEnvironment( Environment environment ) {
        this.environments.remove( environment );
    }
}
