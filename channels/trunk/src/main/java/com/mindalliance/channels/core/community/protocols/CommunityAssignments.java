package com.mindalliance.channels.core.community.protocols;

import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A iterable collection of community assignments.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/3/13
 * Time: 4:22 PM
 */
public class CommunityAssignments implements Iterable<CommunityAssignment>, Serializable {

    private final Set<CommunityAssignment> assignments = new HashSet<CommunityAssignment>(  );

    private final Place locale;

    public CommunityAssignments( Place locale ) {
        this.locale = locale;
    }

    public Place getLocale() {
        return locale;
    }

    public void add( CommunityAssignment assignment ) {
        assignments.add( assignment );
    }

    public CommunityAssignments with( Agent agent ) {
        CommunityAssignments result = new CommunityAssignments( locale );
        for ( CommunityAssignment assignment : this ) {
            if ( assignment.getEmployment().getAgent().equals( agent ) ) {
                result.add( assignment );
            }
        }
        return result;
    }

    public CommunityAssignments assignedTo( Part part ) {
        if ( part == null ) return this;
        CommunityAssignments result = new CommunityAssignments( locale );
        for ( CommunityAssignment assignment : this ) {
            if ( part.equals( assignment.getPart() ) ) {
                result.add( assignment );
            }
        }
        return result;
    }

    //////////////////////

    public int size() {
        return assignments.size();
    }

    public Iterator<CommunityAssignment> iterator() {
        return assignments.iterator();
    }

    public boolean contains( CommunityAssignment assignment ) {
        return assignments.contains( assignment );
    }

    ///////////////////////

    @Override
    public String toString() {
        return "Assignments(" + size() + ')';
    }

 }
