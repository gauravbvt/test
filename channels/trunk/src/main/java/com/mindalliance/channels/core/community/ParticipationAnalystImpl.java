package com.mindalliance.channels.core.community;

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
    public List<ParticipationIssue> detectAllIssues( CommunityService communityService ) {
        List<ParticipationIssue> issues = new ArrayList<ParticipationIssue>();
        Iterator<Identifiable> scope = issueAnalysisScope( communityService );
        while ( scope.hasNext() ) {
            Identifiable identifiable = scope.next();
            issues.addAll( detectIssues( identifiable, communityService ) );
        }
        return issues;
    }

    @Override
    public List<ParticipationIssue> detectIssues( Identifiable identifiable, CommunityService communityService ) {
        List<ParticipationIssue> issues = new ArrayList<ParticipationIssue>();
        for ( ParticipationIssueDetector detector : issueDetectors ) {
            if ( detector.appliesTo( identifiable ) ) {
                issues.addAll( detector.detectIssues( identifiable, communityService ) );
            }
        }
        return issues;
    }

    @Override
    public boolean hasIssues( final Identifiable identifiable, final CommunityService communityService ) {
        for ( ParticipationIssueDetector detector : issueDetectors ) {
            if ( detector.appliesTo( identifiable ) ) {
                if ( !detector.detectIssues( identifiable, communityService ).isEmpty() )
                    return true;
            }
        }
        return false;
    }

    @Override
    public String getIssuesOverview( Identifiable identifiable, CommunityService communityService ) {
        List<ParticipationIssue> issues = detectIssues( identifiable, communityService );
        if ( issues.isEmpty() )
            return "";
        else {
            int count = issues.size();
            return count + ( count > 1 ? " issues" : " issue" );
        }
    }

    private Iterator<Identifiable> issueAnalysisScope( CommunityService communityService ) {
        List<Identifiable> scope = new ArrayList<Identifiable>();
        scope.add( communityService.getPlanCommunity() );
        scope.addAll( findAllOrganizationPlaceholders( communityService ) );
        scope.addAll( communityService.getParticipationManager().getAllKnownAgencies( communityService ) );
        scope.addAll( communityService.getParticipationManager().getAllKnownAgents( communityService ) );
        scope.addAll( communityService.getPlanService().listActualEntities( Actor.class, true ) );
        return scope.iterator();
    }

    @SuppressWarnings( "unchecked" )
    private List<Organization> findAllOrganizationPlaceholders( CommunityService communityService ) {
        return (List<Organization>) CollectionUtils.select(
                communityService.getPlanService().listActualEntities( Organization.class, true ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Organization) object ).isPlaceHolder();
                    }
                }
        );
    }

    @Override
    public List<RequirementRelationship> findRequirementRelationships(
            Phase.Timing timing,
            Event event,
            CommunityService communityService ) {
        List<RequirementRelationship> rels = new ArrayList<RequirementRelationship>();
        List<Agency> allAgencies = communityService.getParticipationManager().getAllKnownAgencies( communityService );
        for ( Agency fromAgency : allAgencies ) {
            for ( Agency toAgency : allAgencies ) {
                if ( !fromAgency.equals( toAgency ) ) {
                    RequirementRelationship rel = findRequirementRelationship(
                            fromAgency,
                            toAgency,
                            timing,
                            event,
                            communityService );
                    if ( !rel.isEmpty() ) rels.add( rel );
                }
            }
        }
        return rels;
    }

    private List<RequirementRelationship> findRequirementRelationships( Requirement requirement,
                                                                        CommunityService communityService ) {
        List<RequirementRelationship> rels = new ArrayList<RequirementRelationship>();
        List<Agency> allAgencies = communityService.getParticipationManager().getAllKnownAgencies( communityService );
        for ( Agency fromAgency : allAgencies ) {
            for ( Agency toAgency : allAgencies ) {
                if ( !fromAgency.equals( toAgency ) ) {
                    RequirementRelationship rel = makeRequirementRelationship(
                            fromAgency,
                            toAgency,
                            requirement,
                            communityService );
                    if ( rel != null ) rels.add( rel );
                }
            }
        }
        return rels;
    }

    private RequirementRelationship makeRequirementRelationship( Agency fromAgency,
                                                                 Agency toAgency,
                                                                 Requirement requirement,
                                                                 CommunityService communityService ) {
        if ( requirement.getCommitterSpec().appliesToAgency( fromAgency, communityService )
                && requirement.getBeneficiarySpec().appliesToAgency( toAgency, communityService ) ) {
            RequirementRelationship reqRel = new RequirementRelationship( fromAgency, toAgency, null, null );
            Requirement req = requirement.transientCopy();
            req.setCommitterAgency( fromAgency );
            req.setBeneficiaryAgency( toAgency );
            req.initialize( communityService );
            reqRel.addRequirement( req );
            return reqRel;
        } else {
            return null;
        }
    }


    @SuppressWarnings( "unchecked" )
    @Override
    public RequirementRelationship findRequirementRelationship(
            final Agency fromAgency,
            final Agency toAgency,
            final Phase.Timing timing,
            final Event event,
            final CommunityService communityService ) {
        final Place locale = communityService.getPlanCommunity().getLocale( communityService );
        List<Requirement> relationshipRequirements = new ArrayList<Requirement>();
        for ( Requirement req : communityService.list( Requirement.class ) ) {
            req.initialize( communityService );
            if ( !req.isUnknown() && !req.isEmpty()
                    && req.appliesTo( timing )
                    && req.appliesTo( event, locale )
                    && req.getCommitterSpec().appliesToAgency( fromAgency, communityService )
                    && req.getBeneficiarySpec().appliesToAgency( toAgency, communityService ) ) {
                Requirement requirement = req.transientCopy();
                requirement.setCommitterAgency( fromAgency );
                requirement.setBeneficiaryAgency( toAgency );
                requirement.initialize( communityService );
                relationshipRequirements.add( requirement );
            }
        }
        RequirementRelationship rel = new RequirementRelationship( fromAgency, toAgency, timing, event );
        rel.setRequirements( relationshipRequirements );
        return rel;
    }

    @Override
    public Requirement.Satisfaction requirementSatisfaction(
            Requirement requirement,
            Object[] extras,
            CommunityService communityService ) {
        Phase.Timing timing = (Phase.Timing) extras[0];
        Event event = (Event) extras[1];
        return requirement.measureSatisfaction( timing, event, communityService );
    }

    @Override
    public int requiredCommitmentsCount( Requirement requirement, Object[] extras, CommunityService communityService ) {
        Phase.Timing timing = (Phase.Timing) extras[0];
        Event event = (Event) extras[1];
        PlanService planService = communityService.getPlanService();
        return communityService.getAllCommitments( false ).inSituation(
                timing,
                event,
                planService.getPlanLocale() )
                .satisfying( requirement, communityService ).size();
    }

    @Override
    public String satisfactionSummary( Requirement requirement, Object[] extras, CommunityService communityService ) {
        Phase.Timing timing = (Phase.Timing) extras[0];
        Event event = (Event) extras[1];
        return requirement.satisfactionSummary( timing, event, communityService );
    }

    @Override
    public String percentSatisfaction( Requirement requirement, CommunityService communityService ) {
        List<RequirementRelationship> reqRels = findRequirementRelationships( requirement, communityService );
        if ( reqRels.isEmpty() ) return "N/A";
        int satisfiedCount = 0;
        for ( RequirementRelationship reqRel : reqRels ) {
             Requirement req = reqRel.getRequirements().get( 0 );
            assert requirement.getId() == req.getId();
            if ( !req.measureSatisfaction( null, null, communityService ).isFailed() ) {
                satisfiedCount++;
            }
        }
        int percent = satisfiedCount * 100 / reqRels.size();
        return percent + "%";
    }

    @Override
    public String realizability( CommunityCommitment communityCommitment, CommunityService communityService ) {
        return communityService.getAnalyst().realizability( communityCommitment.getCommitment(), communityService );
    }


}
