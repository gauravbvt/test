package com.mindalliance.channels.core.community.participation.issues;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Organization;
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
            for ( ParticipationIssueDetector detector : issueDetectors ) {
                if ( detector.appliesTo( identifiable ) ) {
                    issues.addAll( detector.detectIssues( identifiable, planCommunity ) );
                }
            }
        }
        return issues;
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

}
