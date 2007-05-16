/*
 * Created on Apr 26, 2007
 */
package com.mindalliance.channels.data.reference;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.Channels;
import com.mindalliance.channels.services.LibraryService;

/**
 * A taxonomy of types
 * 
 * @author jf
 */
public class Typology extends ReferenceData {

    private List<Type> types;
    private Type root; // The type all types imply by default.

    public Typology() {
    }

    public Typology( String name ) {
        setName(name);
        types = new ArrayList<Type>();
    }

    public static Typology named( String name ) {
        LibraryService libraryService = Channels.getSystemService().getLibraryService();
        return libraryService.getTypology( name );
    }

    /**
     * @return the root
     */
    public Type getRoot() {
        if (root == null) {
            root = new Type();
            root.setName( getName() );
            root.setDescription( "Root type for typology " + getName() );
            root.setTypology( this );
            types.add( root );            
        }
        return root;
    }

    /**
     * @param root the root to set
     */
    public void setRoot( Type root ) {
        this.root = root;
    }

    /**
     * @return the types
     */
    public List<Type> getTypes() {
        return types;
    }

    /**
     * @param types the types to set
     */
    public void setTypes( List<Type> types ) {
        this.types = types;
    }

    /**
     * @param type
     */
    public void addType( Type type ) {
        types.add( type );
    }

    /**
     * @param type
     */
    public void removeType( Type type ) {
        types.remove( type );
    }


}
