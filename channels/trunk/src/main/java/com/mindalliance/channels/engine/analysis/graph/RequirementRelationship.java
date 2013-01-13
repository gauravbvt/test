package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.participation.Agency;
import com.mindalliance.channels.core.community.participation.ParticipationAnalyst;
import com.mindalliance.channels.core.community.protocols.CommunityCommitment;
import com.mindalliance.channels.core.community.protocols.CommunityCommitments;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Requirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class RequirementRelationship implements Identifiable {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( RequirementRelationship.class );


    private static final String SEPARATOR = "=>";
    /**
     * Requirements for an agency to share information with another.
     */

    private long id;
    private String relationshipId;
    private List<Requirement> requirements = new ArrayList<Requirement>();
    private Phase.Timing timing = null;
    private Event event = null;

    public RequirementRelationship() {
    }

    public RequirementRelationship(
            Agency fromAgency,
            Agency toAgency,
            Phase.Timing timing,
            Event event ) {
        id = Math.min( fromAgency.getId(), Long.MAX_VALUE / 2 )
                + Math.min( toAgency.getId(), Long.MAX_VALUE / 2 ) ;
        relationshipId = fromAgency.getName() + SEPARATOR + toAgency.getName();
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

    public String getRelationshipId() {
        return relationshipId;
    }

    public void setId( String id, PlanCommunity planCommunity ) {
        this.relationshipId = id;
        ParticipationAnalyst analyst = planCommunity.getParticipationAnalyst();
        RequirementRelationship reqRel =
                analyst.findRequirementRelationship(
                        getFromAgency( planCommunity ),
                        getToAgency( planCommunity ),
                        timing,
                        event,
                        planCommunity );
        if ( reqRel != null ) {
            requirements = reqRel.getRequirements();
        }
    }

    private String[] parseId( String id ) throws Exception {
        String[] names = new String[2];
        int index = id.indexOf( SEPARATOR );
        if ( index < 1
                || index == id.length() - SEPARATOR.length() )
            throw new Exception( "Bad requirement relationship id " + id );
        names[0] = id.substring( 0, index );
        names[1] = id.substring( index + SEPARATOR.length() );
        return names;
    }

    public Agency getFromAgency( PlanCommunity planCommunity ) {
        try {
            String[] names = parseId( relationshipId );
            return getAgency( names[0], planCommunity );
        } catch ( Exception e ) {
            LOG.warn( "Failed to get from agency from relationship " + relationshipId );
            return Agency.UNKNOWN;
        }
    }

    public Agency getToAgency( PlanCommunity planCommunity ) {
        try {
            String[] names = parseId( relationshipId );
            return getAgency( names[1], planCommunity );
        } catch ( Exception e ) {
            LOG.warn( "Failed to get to agency from relationship " + relationshipId );
            return Agency.UNKNOWN;
        }
    }

    private Agency getAgency( String name, PlanCommunity planCommunity ) {
        return planCommunity.getParticipationManager().findAgencyNamed( name, planCommunity );
    }

    public boolean hasUnfulfilledRequirements(
            Phase.Timing timing,
            Event event,
            PlanCommunity planCommunity ) {
        /*String summary = queryService.getRequirementNonFulfillmentSummary( this, timing, event, analyst );
        return !summary.isEmpty();*/
        return !getNonFulfillmentSummary( timing, event, planCommunity ).isEmpty();
    }

    public String getNonFulfillmentSummary(
            Phase.Timing timing,
            Event event,
            PlanCommunity planCommunity ) {
        StringBuilder sb = new StringBuilder();
        CommunityCommitments allCommitments = planCommunity.getAllCommitments( false );
        List<Requirement> unfulfilled = new ArrayList<Requirement>();
        Place locale = planCommunity.getCommunityLocale();
        for ( Requirement requirement : getRequirements() ) {
            Requirement req = requirement.transientCopy();
            req.setCommitterAgency( getFromAgency( planCommunity ) );
            req.setBeneficiaryAgency( getFromAgency( planCommunity ) );
            req.initialize( planCommunity );
            Iterator<CommunityCommitment> commitmentIterator = allCommitments.iterator();
            boolean fulfilled = false;
            while ( !fulfilled && commitmentIterator.hasNext() ) {
                CommunityCommitment communityCommitment = commitmentIterator.next();
                fulfilled = communityCommitment.isInSituation( timing, event, locale )
                        && req.satisfiedBy( communityCommitment, planCommunity )
                        && planCommunity.getAnalyst().canBeRealized(
                        communityCommitment.getCommitment(),
                        planCommunity.getPlan(),
                        planCommunity.getPlanService() );
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

    //////


    @Override
    public String getClassLabel() {
        return getTypeName();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getTypeName() {
        return "requirement relationship";
    }

    @Override
    public boolean isModifiableInProduction() {
        return true;
    }

    @Override
    public String getName() {
        return "Requirement " + relationshipId;
    }
}
