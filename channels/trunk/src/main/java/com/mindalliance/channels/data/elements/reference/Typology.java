/*
 * Created on Apr 26, 2007
 */
package com.mindalliance.channels.data.elements.reference;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.Channels;
import com.mindalliance.channels.data.Named;
import com.mindalliance.channels.data.system.System;
import com.mindalliance.channels.services.LibraryService;
import com.mindalliance.channels.util.AbstractJavaBean;

/**
 * A taxonomy of types
 * 
 * @author jf
 */
public class Typology extends AbstractJavaBean implements Named {

    private String name;
    private List<Type> types;
    private Type root; // The type all types imply by default.

    public Typology() {
    }

    public Typology( String name ) {
        this.name = name;
        root = System.elementFactory.newInstance( Type.class );
        root.setName( name );
        root.setDescription( "Root type for typology " + name );
        types = new ArrayList<Type>();
        types.add( root );
    }

    /**
     * Compares this named object with the specified named object for
     * order.
     * 
     * @param o the named object to compare to
     */
    public int compareTo( Named named ) {
        return getName().compareTo( named.getName() );
    }

    public static Typology named( String name ) {
        LibraryService libraryService = Channels.getSystemService().getLibraryService();
        return libraryService.getTypology( name );
    }

    /**
     * @return the root
     */
    public Type getRoot() {
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

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName( String name ) {
        this.name = name;
    }

}
