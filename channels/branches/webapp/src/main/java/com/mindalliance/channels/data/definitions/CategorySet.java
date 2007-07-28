// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.definitions;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Set;
import java.util.TreeSet;

import com.mindalliance.channels.DisplayAs;
import com.mindalliance.channels.data.definitions.Category.Taxonomy;

/**
 * A collection of categories from a given typology.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 *
 * @navassoc - - * Category
 * @navassoc - - 1 Taxonomy
 */
public class CategorySet implements Serializable {

    private Taxonomy taxonomy = Taxonomy.Any;
    private Set<Category> categories = new TreeSet<Category>();
    private boolean singleton;

    /**
     * Default constructor.
     */
    public CategorySet() {
        super();
    }

    /**
     * Convenience constructor.
     * @param taxonomy the taxonomy
     * @param categories initial categories
     */
    public CategorySet( Taxonomy taxonomy, Category... categories ) {
        this();
        setTaxonomy( taxonomy );
        for ( Category c : categories )
            addCategory( c );
    }

    /**
     * Whether any of the categories in this set implies a given category.
     * @param category the given category
     */
    public boolean implies( Category category ) {
        for ( Category c : this.categories ) {
            if ( c.implies( category ) )
                return true;
        }

        return false;
    }

    /**
     * Whether any of the categories in this set
     * implies some category in given set.
     * @param set a set of categories
     */
    public boolean implies( CategorySet set ) {
        for ( Category c : set.getCategories() ) {
            if ( this.implies( c ) )
                return true;
        }

        return false;
    }

    /**
     * Return the categories.
     */
    @DisplayAs( direct = "categorized as {1}",
                reverse = "category for {1}",
                directMany = "categorized as:",
                reverseMany = "category for:" )
    public final Set<Category> getCategories() {
        return categories;
    }

    /**
     * Set the categories.
     * @param categories the categories to set
     */
    public void setCategories( Set<Category> categories ) {
        this.categories = new TreeSet<Category>( categories );
    }

    /**
     * Add a category.
     * @param category the taxonomy
     */
    public void addCategory( Category category ) {
        if ( getTaxonomy() != Taxonomy.Any
                && category.getTaxonomy() != getTaxonomy() )
            throw new IllegalArgumentException(
                MessageFormat.format(
                    "Category {0} should be of taxonomy {1}",
                    category.getName(), getTaxonomy()
                ) );

        if ( isSingleton() && getCategories().size() == 1 )
            throw new IllegalStateException(
                "Can't add more than one item in this set" );

        this.categories.add( category );
    }

    /**
     * Remove a taxonomy.
     * @param category the taxonomy
     */
    public void removeCategory( NamedObject category ) {
        this.categories.remove( category );
    }

    /**
     * Return an aggregated Information descriptor for all categories in
     * the taxonomy set.
     */
    public Information getInformationTemplate() {
        // TODO
        return null;
    }

    /**
     * Tell if this taxonomy set is limited to only one element.
     */
    public boolean isSingleton() {
        return this.singleton;
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
}
