// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.definitions;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.mindalliance.channels.data.definitions.Category.Taxonomy;
import com.mindalliance.channels.data.support.GUID;

/**
 * A discipline, possibly a sub-discipline of another.
 * Categories are uniquely named within this discipline (aka namespace).
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 *
 * @composed - - * Category
 * @navassoc - parents * Discipline
 */
public class Discipline extends NamedObject {

    private Map<String,Category> categories = new HashMap<String,Category>();
    private Set<Discipline> parents = new TreeSet<Discipline>();
    /**
     * Default constructor.
     */
    public Discipline() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Discipline( GUID guid ) {
        super( guid );
    }

    /**
     * Default constructor.
     * @param guid the guid
     * @param name the name
     */
    public Discipline( GUID guid, String name ) {
        super( guid, name );
    }

    /**
     * Return the root categories of a given type for this discipline.
     * Includes the roots from parent disciplines.
     * @param taxonomy the type. Specify "Taxonomy.Any" to get all categories.
     */
    public Set<Category> getRoots( Taxonomy taxonomy ) {
        TreeSet<Category> result = new TreeSet<Category>();
        for ( Category c : getAllCategories( taxonomy ) )
            if ( c.getImplications().size() == 0 )
                result.add( c );

        return result;
    }

    /**
     * Return the direct children of a category in this discipline.
     * @param category the category
     */
    public Set<Category> getChildren( Category category ) {
        TreeSet<Category> result = new TreeSet<Category>();
        for ( Category c : getAllCategories( category.getTaxonomy() ) )
            if ( c.getImplications().contains( category ) )
                result.add( c );

        return result;
    }

    /**
     * Get the category of a given name.
     * This will also look in parent disciplines if no category
     * is found within this one.
     *
     * @param name the name
     * @return null if none is found.
     */
    public Category getCategory( String name ) {
        Category result = categories.get( name );
        if ( result == null )
            for ( Discipline parent : parents ) {
                result = parent.getCategory( name );
                if ( result != null )
                    break;
            }

        return result;
    }

    /**
     * Return all categories in this discipline, including those
     * defined in parent disciplines.
     * @param taxonomy the type
     */
    public Set<Category> getAllCategories( Taxonomy taxonomy ) {
        TreeSet<Category> result = new TreeSet<Category>();
        addAllCategories( taxonomy, result, new HashSet<Discipline>() );

        return result;
    }

    private void addAllCategories(
            Taxonomy taxonomy,
            TreeSet<Category> result, Set<Discipline> seen ) {

        if ( !seen.contains( this ) ) {
            seen.add( this );
            result.addAll( getCategories( taxonomy ) );
            for ( Discipline p : this.parents )
                p.addAllCategories( taxonomy, result, seen );
        }
    }

    /**
     * Return the categories of a given type directly defined in this
     * discipline.
     * @param taxonomy the type
     * @see getAllCategories()
     */
    public Set<Category> getCategories( Taxonomy taxonomy ) {
        Set<Category> result = new TreeSet<Category>();
        for ( Category c : this.categories.values() )
            if ( taxonomy == Taxonomy.Any || c.getTaxonomy() == taxonomy )
                result.add( c );

        return result;
    }

    /**
     * Return the categories directly defined in this discipline.
     * @see getAllCategories()
     */
    public Set<Category> getCategories() {
        return Collections.unmodifiableSet(
                new TreeSet<Category>(
                        this.categories.values() ) );
    }

    /**
     * Set the categories.
     * @param categories the categories
     */
    public void setCategories( Set<Category> categories ) {
        this.categories = new HashMap<String,Category>();
        for ( Category c : categories )
            addCategory( c );
    }

    /**
     * Add a category.
     * @param category the category
     */
    public void addCategory( Category category ) {
        this.categories.put( category.getName(), category );
    }

    /**
     * Remove a category.
     * @param category the category
     */
    public void removeCategory( NamedObject category ) {
        this.categories.remove( category.getName() );
    }

    /**
     * Return the parent disciplines.
     */
    public Set<Discipline> getParents() {
        return Collections.unmodifiableSet( this.parents );
    }

    /**
     * Set the parent disciplines.
     * @param parents the parents
     */
    public void setParents( Set<Discipline> parents ) {
        this.parents = new TreeSet<Discipline>();
        for ( Discipline p : parents )
            addParent( p );
    }

    /**
     * Add a parent.
     * @param parent the parent
     */
    public void addParent( Discipline parent ) {
        this.parents.add( parent );
    }

    /**
     * Remove a parent.
     * @param parent the parent
     */
    public void removeParent( Discipline parent ) {
        this.parents.remove( parent );
    }
}
