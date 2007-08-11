// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.definitions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.mindalliance.channels.support.GUID;

/**
 * A taxonomy of categories.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision$
 *
 * @opt attributes
 * @composed - - * Discipline
 */
public class Typology extends NamedObject {

    private Map<String,Discipline> disciplines =
                                        new HashMap<String,Discipline>();
    private Discipline defaultDiscipline;

    /**
     * Default constructor.
     */
    public Typology() {
        this( null, null );
    }

    /**
     * Default constructor.
     * @param guid the guid
     * @param name the name
     */
    public Typology( GUID guid, String name ) {
        super( guid, name );

        Discipline d = new Discipline();
        d.setName( "Common usage" );
        d.setDescription( "Common vocabulary across all disciplines" );

        setDefaultDiscipline( d );
        addDiscipline( d );
    }

    /**
     * Return categories of given name across all disciplines.
     * @param name the name
     */
    public Set<Category> getCategories( final String name ) {
        TreeSet<Category> result = new TreeSet<Category>();
        for ( Discipline d : this.disciplines.values() ) {
            Category c = d.getCategory( name );
            if ( c != null )
                result.add( c );
        }

        return result;
    }

    /**
     * Return the category of given name in a specific discipline.
     * @param discipline the discipline
     * @param name the name
     * @return null when no category of that name exists
     */
    public NamedObject getCategory( Discipline discipline, String name ) {
        return discipline.getCategory( name );
    }

    /**
     * Return the default discipline.
     */
    public Discipline getDefaultDiscipline() {
        return this.defaultDiscipline;
    }

    /**
     * Set the default discipline.
     * @param defaultDiscipline the defaultDiscipline
     */
    public void setDefaultDiscipline( Discipline defaultDiscipline ) {
        this.defaultDiscipline = defaultDiscipline;
    }

    /**
     * Return the disciplines.
     */
    public Set<Discipline> getDisciplines() {
        return Collections.unmodifiableSet(
                new TreeSet<Discipline>(
                        this.disciplines.values() ) );
    }

    /**
     * Set the disciplines.
     * @param disciplines the disciplines
     */
    public void setDisciplines( Set<Discipline> disciplines ) {
        this.disciplines = new HashMap<String,Discipline>();
        for ( Discipline d : disciplines )
            addDiscipline( d );

        Discipline def = getDefaultDiscipline();
        if ( !disciplines.contains( def ) )
            this.disciplines.put( def.getName(), def );
    }

    /**
     * Return the subdiscipline of a discipline, i.e. disciplines
     * having the given discipline as their parent.
     * @param discipline the given discipline
     */
    public Set<Discipline> getSubdisciplines( Discipline discipline ) {
        Set<Discipline> result = new TreeSet<Discipline>();
        for ( Discipline d : this.disciplines.values() )
            if ( d.getParents().contains( discipline ) )
                result.add( d );

        return result;
    }

    /**
     * Add a discipline.
     * @param discipline the discipline
     */
    public void addDiscipline( Discipline discipline ) {
        this.disciplines.put( discipline.getName(), discipline );
        if ( discipline != getDefaultDiscipline()
                && discipline.getParents().size() == 0 )
            discipline.addParent( getDefaultDiscipline() );
    }

    /**
     * Remove a discipline.
     * @param discipline the discipline
     */
    public void removeDiscipline( Discipline discipline ) {
        this.disciplines.remove( discipline.getName() );
    }

    /**
     * Get a discipline of given name.
     * @param name the name of the discipline
     * @return null if none is found
     */
    public Discipline getDiscipline( String name ) {
        return this.disciplines.get( name );
    }
}
