// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.util;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.mindalliance.channels.reference.AreaType;

/**
 * A location of some type that may within and next to other locations.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 *
 * @opt attributes
 * @navassoc * partOf 0..1 Area
 * @navassoc * nextTo * Area
 */
public class Area extends AbstractJavaBean {

    private AreaType kind;
    private String name;
    private Area partOf;
    private Set<Area> nextTo = new TreeSet<Area>();

    /**
     * Default constructor.
     */
    Area() {
        super();
    }

    /**
     * Return the value of kind.
     */
    public AreaType getKind() {
        return this.kind;
    }

    /**
     * Set the value of kind.
     * @param kind The new value of kind
     */
    public void setKind( AreaType kind ) {
        this.kind = kind;
    }

    /**
     * Return the value of nextTo.
     */
    public Set<Area> getNextTo() {
        return this.nextTo;
    }

    /**
     * Set the value of nextTo.
     * @param nextTo The new value of nextTo
     */
    public void setNextTo( SortedSet<Area> nextTo ) {
        this.nextTo = nextTo;
    }

    /**
     * Return the value of partOf.
     */
    public Area getPartOf() {
        return this.partOf;
    }

    /**
     * Set the value of partOf.
     * @param partOf The new value of partOf
     */
    public void setPartOf( Area partOf ) {
        this.partOf = partOf;
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
}
