// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.reference;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Set;
import java.util.TreeSet;

import com.mindalliance.channels.DisplayAs;
import com.mindalliance.channels.data.reference.Typology.Category;

import static com.mindalliance.channels.data.reference.Typology.Category.*;

/**
 * A collection of types from a given typology.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class TypeSet implements Serializable {

    private Typology typology;
    private Category category;
    private Set<Type> types;
    private boolean singleton;

    /**
     * Default constructor.
     * @param typology the underlying typology
     * @param category the category for types in this set
     * @param singleton if the typeset is unique
     */
    public TypeSet(
            Typology typology, Category category, boolean singleton ) {

        this.typology = typology;
        this.singleton = singleton;
        this.category = category;
        types = new TreeSet<Type>();
    }

    /**
     * Default constructor.
     * @param typology the underlying typology
     */
    public TypeSet( Typology typology ) {
        this( typology, Any, false );
    }

    /**
     * Whether any of the types imply a given type.
     * @param type the given type
     */
    public boolean implies( Type type ) {
        //TODO
        return false;
    }

    /**
     * Return the types.
     */
    @DisplayAs( direct = "typed as {1}",
                reverse = "type for {1}",
                directMany = "typed as:",
                reverseMany = "type for:" )
    public final Set<Type> getTypes() {
        return types;
    }

    /**
     * Set the types.
     * @param types the types to set
     */
    public void setTypes( Set<Type> types ) {
        this.types = new TreeSet<Type>( types );
    }

    /**
     * Add a type.
     * @param type the type
     */
    public void addType( Type type ) {
        if ( getCategory() != Any && type.getCategory() != getCategory() )
            throw new IllegalArgumentException(
                MessageFormat.format(
                    "Type {0} should be of category {1}",
                    type.getName(), getCategory()
                ) );

        if ( isSingleton() && getTypes().size() == 1 )
            throw new IllegalStateException(
                "Can't add more than one item in this set" );

        getTypes().add( type );
    }

    /**
     * Remove a type.
     * @param type the type
     */
    public void removeType( Type type ) {
        getTypes().remove( type );
    }

    /**
     * Return the typology.
     */
    public Typology getTypology() {
        return typology;
    }

    /**
     * Return all domains referenced by types in the typology.
     */
    public TypeSet getDomains() {
        // TODO
        return new TypeSet( getTypology() );
    }

    /**
     * Return an aggregated Information descriptor for all types in
     * the type set.
     */
    public Information getDescriptor() {
        // TODO
        return null;
    }

    /**
     * Tell if this type set is limited to only one element.
     */
    public boolean isSingleton() {
        return this.singleton;
    }

    /**
     * Return the value of category.
     */
    public Category getCategory() {
        return this.category;
    }
}
