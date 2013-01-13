package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.participation.issues.ParticipationIssue;
import com.mindalliance.channels.core.community.participation.issues.ParticipationIssueDetector;
import com.mindalliance.channels.core.community.protocols.CommunityCommitment;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.engine.analysis.graph.RequirementRelationship;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of participation analyst.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/8/13
 * Time: 9:18 AM
 */
public class ParticipationAnalystImpl implements ParticipationAnalyst {

    private List<ParticipationIssueDetector> issueDetectors;

    public ParticipationAnalystImpl() {
    }

    public List<ParticipationIssueDetector> getIssueDetectors() {
        return issueDetectors;
    }

    public void setIssueDetectors( List<ParticipationIssueDetector> issueDetectors ) {
        this.issueDetectors = issueDetectors;
    }

    @Override
    public List<ParticipationIssue> detectAllIssues( PlanCommunity planCommunity ) {
        List<ParticipationIssue> issues = new ArrayList<ParticipationIssue>(  );
        Iterator<Identifiable> scope = issueAnalysisScope( planCommunity );
        while ( scope.hasNext() ) {
            Identifiable identifiable = scope.next();
            issues.addAll( detectIssues( identifiable, planCommunity ) );
        }
        return issues;
    }

    @Override
    public List<ParticipationIssue> detectIssues( Identifiable identifiable, PlanCommunity planCommunity ) {
        List<ParticipationIssue> issues = new ArrayList<ParticipationIssue>(  );
        for ( ParticipationIssueDetector detector : issueDetectors ) {
            if ( detector.appliesTo( identifiable ) ) {
                issues.addAll( detector.detectIssues( identifiable, planCommunity ) );
            }
        }
        return issues;
    }

    @Override
    public boolean hasIssues( final Identifiable identifiable, final PlanCommunity planCommunity ) {
        for ( ParticipationIssueDetector detector : issueDetectors ) {
            if ( detector.appliesTo( identifiable ) ) {
                if( !detector.detectIssues( identifiable, planCommunity ).isEmpty() )
                    return true;
            }
        }
        return false;
    }

    @Override
    public String getIssuesOverview( Identifiable identifiable, PlanCommunity planCommunity ) {
        List<ParticipationIssue> issues = detectIssues( identifiable, planCommunity );
        if ( issues.isEmpty() )
            return "";
        else {
            int count = issues.size();
            return count + ( count > 1 ? " issues" : " issue" );
        }
    }

    private Iterator<Identifiable> issueAnalysisScope( PlanCommunity planCommunity ) {
        List<Identifiable> scope = new ArrayList<Identifiable>();
        scope.add( planCommunity );
        scope.addAll( findAllOrganizationPlaceholders( planCommunity ) );
        scope.addAll( planCommunity.getParticipationManager().getAllKnownAgencies( planCommunity ) );
        scope.addAll( planCommunity.getParticipationManager().getAllKnownAgents( planCommunity ) );
        scope.addAll( planCommunity.getPlanService().listActualEntities( Actor.class,  true ) );
        return scope.iterator();
    }

    @SuppressWarnings( "unchecked" )
    private List<Organization> findAllOrganizationPlaceholders( PlanCommunity planCommunity ) {
        return (List<Organization>) CollectionUtils.select(
                planCommunity.getPlanService().listActualEntities( Organization.class, true ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ((Organization)object).isPlaceHolder();
                    }
                }
        );
    }

    @Override
    public List<RequirementRelationship> findRequirementRelationships(
            Phase.Timing timing,
            Event event,
            PlanCommunity planCommunity ) {
        List<RequirementRelationship> rels = new ArrayList<RequirementRelationship>();
        List<Agency> allAgencies = planCommunity.getParticipationManager().getAllKnownAgencies( planCommunity );
        for ( Agency fromAgency : allAgencies ) {
            for ( Agency toAgency : allAgencies ) {
                if ( !fromAgency.equals( toAgency ) ) {
                    RequirementRelationship rel = findRequirementRelationship(
                            fromAgency,
                            toAgency,
                            timing,
                            event,
                            planCommunity );
                    if ( !rel.isEmpty() ) rels.add( rel );
                }
            }
        }
        return rels;
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public RequirementRelationship findRequirementRelationship(
            final Agency fromAgency,
            final Agency toAgency,
            final Phase.Timing timing,
            final Event event,
            final PlanCommunity planCommunity ) {
        final Place locale = planCommunity.getCommunityLocale();
        List<Requirement> requirements = (List<Requirement>) CollectionUtils.select(
                planCommunity.getPlanService().list( Requirement.class ), // todo get requirements from the community, not the plan
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Requirement req = (Requirement) object;
                        req.initialize( planCommunity );
                        return !req.isUnknown() && !req.isEmpty()
                                && req.appliesTo( timing )
                                && req.appliesTo( event, locale )
                                && req.getCommitterSpec().appliesToAgency( fromAgency, planCommunity )
                                && req.getBeneficiarySpec().appliesToAgency( toAgency, planCommunity );
                    }
                }
        );
        RequirementRelationship rel = new RequirementRelationship( fromAgency, toAgency, timing, event );
        rel.setRequirements( requirements );
        return rel;
    }

    @Override
    public Requirement.Satisfaction committerSatisfaction(
            Requirement requirement,
            Object[] extras,
            PlanCommunity planCommunity ) {
        Phase.Timing timing = (Phase.Timing) extras[0];
        Event event = (Event) extras[1];
        Agency agency = requirement.getCommitterSpec().getAgency();
        return requirement.satisfaction( agency, false, timing, event, planCommunity );
    }

    @Override
    public Requirement.Satisfaction beneficiarySatisfaction(
            Requirement requirement,
            Object[] extras,
            PlanCommunity planCommunity ) {
        Phase.Timing timing = (Phase.Timing) extras[0];
        Event event = (Event) extras[1];
        Agency agency = requirement.getBeneficiarySpec().getAgency();
        return requirement.satisfaction( agency, true, timing, event, planCommunity );
    }

    @Override
    public int commitmentsCount( Requirement requirement, Object[] extras, PlanCommunity planCommunity ) {
        Phase.Timing timing = (Phase.Timing) extras[0];
        Event event = (Event) extras[1];
        PlanService planService = planCommunity.getPlanService();
        return planCommunity.getAllCommitments( false ).inSituation(
                timing,
                event,
                planService.getPlanLocale() )
                .satisfying( requirement, planCommunity ).size();
    }

    @Override
    public String realizability( CommunityCommitment communityCommitment, PlanCommunity planCommunity ) {
        return planCommunity.getAnalyst().realizability( communityCommitment.getCommitment(), planCommunity );
    }



}
