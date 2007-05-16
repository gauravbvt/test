/*
 * Created on Apr 28, 2007
 */
package com.mindalliance.channels.data.system;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mindalliance.channels.data.reference.Environment;
import com.mindalliance.channels.data.reference.Location;
import com.mindalliance.channels.data.reference.Policy;
import com.mindalliance.channels.data.reference.Template;
import com.mindalliance.channels.data.reference.Typology;

/**
 * Access to all reference data: environment, typologies, locations,
 * policies and templates
 * 
 * @author jf
 */
public class Library extends AbstractQueryable {

    private Map<String, Typology> typologies;
    private List<Location> locations;
    private List<Policy> policies;
    private List<Environment> environments;
    private List<Template> templates;

    public Library() {
        typologies = new HashMap<String, Typology>();
        locations = new ArrayList<Location>();
        policies = new ArrayList<Policy>();
        environments = new ArrayList<Environment>();
        templates = new ArrayList<Template>();
    }
    
    /**
     * @return the locations
     */
    public List<Location> getLocations() {
        return locations;
    }

    /**
     * @param locations the locations to set
     */
    public void setLocations( List<Location> locations ) {
        this.locations = locations;
    }

    /**
     * @return the policies
     */
    public List<Policy> getPolicies() {
        return policies;
    }

    /**
     * @param policies the policies to set
     */
    public void setPolicies( List<Policy> policies ) {
        this.policies = policies;
    }

    /**
     * @return the templates
     */
    public List<Template> getTemplates() {
        return templates;
    }

    /**
     * @param templates the templates to set
     */
    public void setTemplates( List<Template> templates ) {
        this.templates = templates;
    }

    /**
     * @return the typologies
     */
    public Collection<Typology> getTypologies() {
        return typologies.values();
    }

    /**
     * @param typologies the typologies to set
     */
    public void setTypologies( Collection<Typology> typologies ) {
        for ( Typology typology : typologies ) {
            addTypology(typology );
        }
    }

    public Typology getTypology( String name ) {
        return typologies.get( name );
    }

    public void addTypology( Typology typology ) {
        typologies.put( typology.getName(), typology );
    }

}
