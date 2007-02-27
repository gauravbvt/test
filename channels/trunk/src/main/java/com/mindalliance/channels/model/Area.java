// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import java.util.SortedSet;
import java.util.TreeSet;

import com.mindalliance.channels.util.GUID;

/**
 * A location of some type that may within and next to other locations.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class Area extends AbstractNamedObject {

    private String kind;
    private Area partOf;
    private SortedSet<Area> nextTo = new TreeSet<Area>();

    /**
     * Default constructor.
     * @param guid the unique ID for this object
     */
    Area( GUID guid ) {
        super( guid );
    }

    /**
     * Return the value of kind.
     */
    public String getKind() {
        return this.kind;
    }

    /**
     * Set the value of kind.
     * @param kind The new value of kind
     */
    public void setKind( String kind ) {
        this.kind = kind;
    }

    /**
     * Return the value of nextTo.
     */
    public SortedSet<Area> getNextTo() {
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
}
