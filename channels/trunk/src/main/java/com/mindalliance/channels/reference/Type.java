// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.reference;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * A type, eventually a java bean imported from a custom ontology.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 *
 * @opt attributes
 * @assoc * - 0..1 DomainType
 * @assoc * implies * Type
 */
public abstract class Type implements Comparable<Type> {

    private String name;
    private DomainType domain;
    private Set<Type> implies = new TreeSet<Type>();
    private List<URL> urls = new ArrayList<URL>();

    /**
     * Default constructor.
     */
    public Type() {
    }

    /**
     * Default constructor.
     * @param name the name of this type
     */
    public Type( String name ) {
        this();
        setName( name );
    }

    /**
     * Return the value of domain.
     */
    public DomainType getDomain() {
        return this.domain;
    }

    /**
     * Set the value of domain.
     * @param domain The new value of domain
     */
    public void setDomain( DomainType domain ) {
        this.domain = domain;
    }

    /**
     * Return the value of implies.
     */
    public Set<Type> getImplies() {
        return this.implies;
    }

    /**
     * Set the value of implies.
     * @param implies The new value of implies
     */
    public void setImplies( Set<Type> implies ) {
        this.implies = implies;
    }

    /**
     * Return the value of name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the value of name.
     * @param name The new value of name
     */
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * Return the value of urls.
     */
    public List<URL> getUrls() {
        return this.urls;
    }

    /**
     * Set the value of urls.
     * @param urls The new value of urls
     */
    public void setUrls( List<URL> urls ) {
        this.urls = urls;
    }

    /**
     * Add an URL from the list.
     * @param url the url to add
     */
    public void addUrl( URL url ) {
        this.urls.add( url );
    }

    /**
     * Remove an URL from the list.
     * @param url the url to remove
     */
    public void removeUrl( URL url ) {
        this.urls.remove( url );
    }

    /**
     * Compare with another type.
     * @param o the other type
     */
    public int compareTo( Type o ) {
        return getName().compareTo( o.getName() );
    }
}
