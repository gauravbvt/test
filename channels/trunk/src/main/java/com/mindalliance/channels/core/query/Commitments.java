/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.query;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Specable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Filterable sharing commitments.
 */
public class Commitments implements Serializable, Iterable<Commitment> {

    private final Set<Commitment> commitments = new HashSet<Commitment>();

    public Commitments() {
    }

    public Commitments( QueryService queryService, Specable profile, Assignments assignments ) {
        List<Flow> sharingFlows = queryService.findAllFlows(); //assignments.getSharingFlows();
        commitments.addAll( queryService.findAllCommitmentsOf( profile, assignments, sharingFlows ) );
        commitments.addAll( queryService.findAllCommitmentsTo( profile, assignments, sharingFlows ) );
    }

    public Commitments of( Assignment assignment ) {
        Commitments result = new Commitments();
        for ( Commitment commitment : commitments )
            if ( assignment.equals( commitment.getCommitter() ) )
                result.add( commitment );

        return result;
    }

    public Commitments to( Assignment assignment ) {
        Commitments result = new Commitments();
        for ( Commitment commitment : commitments )
            if ( assignment.equals( commitment.getBeneficiary() ) )
                result.add( commitment );

        return result;
    }

    public Commitments with( Flow flow ) {
        Commitments result = new Commitments();
        for ( Commitment commitment : commitments )
            if ( flow.equals( commitment.getSharing() ) )
                result.add( commitment );

        return result;
    }

    public Commitments withEoi( final String eoi ) {
        Commitments result = new Commitments();
        for ( Commitment commitment : commitments )
            if ( CollectionUtils.exists( commitment.getSharing().getEois(), new Predicate() {
                @Override
                public boolean evaluate( Object object ) {
                    return Matcher.same( ( (ElementOfInformation) object ).getContent(), eoi );
                }
            } ) )
                result.add( commitment );

        return result;
    }

    public Commitments withInfo( String info ) {
        Commitments result = new Commitments();
        for ( Commitment commitment : commitments )
            if ( Matcher.same( commitment.getSharing().getName(), info ) )
                result.add( commitment );

        return result;
    }

    public void add( Commitment commitment ) {
        commitments.add( commitment );
    }

    public void addAll( Commitments others ) {
        for ( Commitment commitment : others ) {
            commitments.add( commitment );
        }
    }

    @Override
    public Iterator<Commitment> iterator() {
        return commitments.iterator();
    }
}
