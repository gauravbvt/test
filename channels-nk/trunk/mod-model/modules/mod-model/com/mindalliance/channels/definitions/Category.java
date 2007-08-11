// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.definitions;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.mindalliance.channels.support.GUID;

/**
 * A categorization of elements.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 *
 * @navassoc - - 1 Taxonomy
 * @navassoc - implies * Category
 */
public class Category extends NamedObject implements Describable {

    private Taxonomy taxonomy = Taxonomy.Any;
    private Information informationTemplate;
    private Set<Category> implications = new TreeSet<Category>();

    /**
     * Default constructor.
     */
    public Category() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Category( GUID guid ) {
        super( guid );
    }

    /**
     * Convenience constructor.
     * @param guid the guid
     * @param name the name of the category
     * @param taxonomy the general category taxonomy
     * @param implied implied categories
     */
    public Category( GUID guid, String name,
            Taxonomy taxonomy, Category... implied ) {

        super( guid, name );
        setTaxonomy( taxonomy );
        for ( Category c : implied )
            addImplication( c );
    }

    /**
     * Convenience constructor.
     * @param name the name of the category
     * @param taxonomy the general category taxonomy
     * @param implied implied categories
     */
    public Category( String name, Taxonomy taxonomy, Category... implied ) {
        this( null, name, taxonomy, implied );
    }

    /**
     * Test if this category implies another.
     * @param category the other category
     */
    public boolean implies( NamedObject category ) {
        return implies( category, new HashSet<Category>() );
    }

    private boolean implies( NamedObject category, Set<Category> trail ) {
        boolean result = false;
        if ( this == category )
            result = true;
        else {
            trail.add( this );
            for ( Category i : this.implications ) {
                if ( !trail.contains( i ) && i.implies( category, trail ) )
                        return true;
            }
        }

        return result;
    }

    /**
     * Return the informationTemplate.
     */
    public Information getInformationTemplate() {
        return informationTemplate;
    }

    /**
     * Set the informationTemplate.
     * @param informationTemplate the information template
     */
    public void setInformationTemplate( Information informationTemplate ) {
        this.informationTemplate = informationTemplate;
    }

    /**
     * Return the implications.
     */
    public Set<Category> getImplications() {
        return Collections.unmodifiableSet( this.implications );
    }

    /**
     * Set the implications.
     * @param implications the implications
     */
    public void setImplications( Set<Category> implications ) {
        this.implications = new TreeSet<Category>();
        for ( Category i : implications )
            addImplication( i );
    }

    /**
     * Add an implied category.
     * @param category the category
     */
    public void addImplication( Category category ) {
        if ( getTaxonomy() != Taxonomy.Any
                && category.getTaxonomy() != getTaxonomy() )
            throw new IllegalArgumentException(
                MessageFormat.format(
                        "Category {0} should be of taxonomy {1}",
                        category, getTaxonomy() ) );

        this.implications.add( category );
    }

    /**
     * Remove an implied category.
     * @param category the category
     */
    public void removeImplication( NamedObject category ) {
        this.implications.remove( category );
    }

    /**
     * Return the taxonomy.
     */
    public Taxonomy getTaxonomy() {
        return this.taxonomy;
    }

    /**
     * Set the taxonomy.
     * @param taxonomy the taxonomy
     */
    public void setTaxonomy( Taxonomy taxonomy ) {
        this.taxonomy = taxonomy;
    }

    /**
     * Generic taxonomy category taxonomies.
     * There is a taxonomy for each kind of element that can be typed.
     * For example, there is an event taxonomy, a task taxonomy, a
     * role taxonomy etc.
     */
    public enum Taxonomy {

        /** Catch all for any category. */
        Any,

        /** Root for mission categories. */
        Mission,

        /** Root for objective categories. */
        Objective,

        /** Root for organization categories. */
        Organization,

        /** Root for situation categories. */
        Situation,

        /** Root for policy categories. */
        Policy,

        /** Root for role categories. */
        Role,

        /** Root for task categories. */
        Task,

        /** Root for event categories. */
        Event,

        /** Root for domain categories. */
        Domain,

        /** Root for issue categories. */
        Issue,

        /** Root for clearance categories. */
        Clearance,

        /** Root for format categories. */
        Format,

        /** Root for location categories. */
        Location
    }
}
