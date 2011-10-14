package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.Commitments;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.Analyst;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Required networking relationship between organizations.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/6/11
 * Time: 10:03 AM
 */
public class RequirementRelationship extends Relationship<Organization> {
    /**
     * Requirements for an organization to share information with another.
     */
    private List<Requirement> requirements = new ArrayList<Requirement>();
    private Phase.Timing timing = null;
    private Event event = null;

    public RequirementRelationship() {
    }

    public RequirementRelationship(
            Organization fromOrg,
            Organization toOrg,
            Phase.Timing timing,
            Event event ) {
        super( fromOrg, toOrg );
        this.timing = timing;
        this.event = event;
    }

    public List<Requirement> getRequirements() {
        return requirements;
    }

    public void setRequirements( List<Requirement> requirements ) {
        this.requirements = requirements;
    }

    public boolean isEmpty() {
        return requirements.isEmpty();
    }

    public void setId( long id, Segment segment, QueryService queryService, Analyst analyst ) {
        setId( id, queryService );
        RequirementRelationship reqRel =
                analyst.findRequirementRelationship( queryService,
                        getFromIdentifiable( queryService ),
                        getToIdentifiable( queryService ),
                        timing,
                        event );
        if ( reqRel != null ) {
            requirements = reqRel.getRequirements();
        }
    }

    public boolean hasUnfulfilledRequirements(
            Phase.Timing timing,
            Event event,
            QueryService queryService,
            Analyst analyst ) {
        /*String summary = queryService.getRequirementNonFulfillmentSummary( this, timing, event, analyst );
        return !summary.isEmpty();*/
        return !getNonFulfillmentSummary( timing, event, queryService, analyst ).isEmpty();
    }

    public String getNonFulfillmentSummary(
            Phase.Timing timing,
            Event event,
            QueryService queryService,
            Analyst analyst ) {
        StringBuilder sb = new StringBuilder();
        Commitments allCommitments = queryService.getAllCommitments();
        List<Requirement> unfulfilled = new ArrayList<Requirement>();
        Plan plan = queryService.getPlan();
        for ( Requirement requirement : getRequirements() ) {
            Requirement req = requirement.transientCopy();
            req.setCommitterOrganization( (Organization) getFromIdentifiable( queryService ) );
            req.setBeneficiaryOrganization( (Organization) getToIdentifiable( queryService ) );
            Iterator<Commitment> commitmentIterator = allCommitments.iterator();
            Place planLocale = plan.getLocale();
            boolean fulfilled = false;
            while ( !fulfilled && commitmentIterator.hasNext() ) {
                Commitment commitment = commitmentIterator.next();
                fulfilled = commitment.isInSituation( timing, event, planLocale )
                        && req.satisfiedBy( commitment, planLocale )
                        && analyst.canBeRealized( commitment, plan );
            }
            if ( !fulfilled )
                unfulfilled.add( req );
        }
        if ( !unfulfilled.isEmpty() ) {
            sb.append( "Unfulfilled " );
            sb.append( unfulfilled.size() == 1 ? "requirement: " : "requirements: " );
            Iterator<Requirement> iter = unfulfilled.iterator();
            while ( iter.hasNext() ) {
                sb.append( '"' );
                sb.append( iter.next().getName() );
                sb.append( '"' );
                if ( iter.hasNext() ) sb.append( ", " );
            }
        }
        return sb.toString();
    }

}
