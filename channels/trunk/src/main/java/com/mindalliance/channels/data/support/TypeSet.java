/*
 * Created on Apr 26, 2007
 */
package com.mindalliance.channels.data.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.mindalliance.channels.data.components.Information;
import com.mindalliance.channels.data.reference.Type;
import com.mindalliance.channels.data.reference.Typology;

/**
 * A collection of types from a given typology.
 * 
 * @author jf
 */
public class TypeSet implements Serializable {

    public static final boolean SINGLETON = true;

    private Typology typology;

    private Set<Type> types;

    boolean singleton = false;

    public TypeSet( Typology typology ) {
        this.typology = typology;
        types = new TreeSet<Type>();
    }

    public TypeSet( String typologyName ) {
        this( Typology.named( typologyName ) );
    }

    public TypeSet( String typologyName, boolean singleton ) {
        this( typologyName );
        this.singleton = singleton;
    }

    /**
     * Whether any of the types imply a given type.
     * 
     * @param type
     * @return
     */
    public boolean implies( Type type ) {
        return false; // TODO
    }

    /**
     * @return the types
     */
    public Set<Type> getTypes() {
        return types;
    }

    /**
     * @param types the types to set
     */
    public void setTypes( Set<Type> types ) {
        this.types = types;
    }

    /**
     * @param type
     */
    public void addType( Type type ) {
        types.add( type ); // TODO verify if valid operarion
    }

    /**
     * @param type
     */
    public void removeType( Type type ) {
        types.remove( type );
    }

    /**
     * @return the typology
     */
    public Typology getTypology() {
        return typology;
    }

    /**
     * Return all domains referenced by types in the typology
     * 
     * @return
     */
    public TypeSet getDomains() {
        return null; // TODO
    }

    /**
     * Return an aggregated Information descriptor for all types in
     * the type set
     * 
     * @return
     */
    public Information getDescriptor() {
        return null; // TODO
    }

}
