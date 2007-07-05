// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.reference;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mindalliance.channels.data.reference.Typology.Category.Any;

/**
 * A taxonomy of types.
 * All type names are unique in this typology.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision$
 */
public class Typology extends ReferenceData {

    /** The name of the root element. */
    public static final String ROOT_NAME = "" ;

    /**
     * Generic type categories.
     * These are the standard "supertypes" below the root.
     * All other types in the typology should be under one
     * (or more) of these categories.
     */
    public enum Category {
        /** The catch all category. */
        Any, Standard, Mission, Objective,

        /** Events. */
        Event, Domain, Issue, Clearance, Format, Location
    }

    private List<Type> types = new ArrayList<Type>();
    private Map<String,Type> typeIndex = new HashMap<String,Type>();
    private Type root;

    /**
     * Default constructor.
     */
    public Typology() {
        super();
        setRoot( new Type( this, Any ) );
    }

    /**
     * Default constructor.
     * @param name the name of the typology
     */
    public Typology( String name ) {
        this();
        setName( name );
    }

    /**
     * Set the root of this typology.
     * Also create the categories.
     * @param root the new root
     */
    public void setRoot( Type root ) {
        this.root = root;

        root.setName( ROOT_NAME );
        root.setDescription( "Root type for typology " + getName() );
        addType( root );

        for ( Category c : Category.values() )
            if ( c != Any )
                addType( new Type( c.toString(), root, c ) );
    }

    /**
     * Return the type all types imply by default.
     */
    public Type getRoot() {
        return root;
    }

    /**
     * Return the types.
     */
    public List<Type> getTypes() {
        return types;
    }

    /**
     * Set the types.
     * @param types the types to set
     */
    public void setTypes( List<Type> types ) {
        this.types = new ArrayList<Type>();
        this.typeIndex = new HashMap<String,Type>();
        setRoot( new Type( this, Any ) );

        for ( Type t : types )
            if ( !typeIndex.containsKey( t.getName() ) ) {
                t.setTypology( this );
                addType( t );
            }
    }

    /**
     * Add a type.
     * @param type the type
     */
    public void addType( Type type ) {
        final String name = type.getName();
        if ( typeIndex.containsKey( name ) )
            throw new IllegalArgumentException(
                MessageFormat.format( "Type {0} already exists", name ) );

        types.add( type );
        typeIndex.put( name, type );

        if ( type.getCategory() != Any ) {
            // Add to category type
            Type c = getType( type.getCategory().toString() );
            c.getImplied().addType( type );
        }
    }

    /**
     * Remove a type.
     * @param type the type
     */
    public void removeType( Type type ) {
        types.remove( type );
        typeIndex.remove( type.getName() );

        // TODO remove or fix subtypes?
    }

    /**
     * Return the type of the given name.
     * @param name the name
     */
    public Type getType( String name ) {
        return typeIndex.get( name );
    }
}
