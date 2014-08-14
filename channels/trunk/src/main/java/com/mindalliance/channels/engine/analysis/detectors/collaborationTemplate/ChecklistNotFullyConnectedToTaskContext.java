package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.checklist.Checklist;
import com.mindalliance.channels.core.model.checklist.Condition;
import com.mindalliance.channels.core.model.checklist.Outcome;
import com.mindalliance.channels.core.model.checklist.StepGuard;
import com.mindalliance.channels.core.model.checklist.StepOutcome;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Need satisfaction, capability creation, caused event, achieved goal, asset-related conditions and outcomes not mapped to a step in the task's checklist.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/24/13
 * Time: 9:07 AM
 */
public class ChecklistNotFullyConnectedToTaskContext extends AbstractIssueDetector {

    public ChecklistNotFullyConnectedToTaskContext() {
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Part;
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getModelService();
        Part part = (Part)modelObject;
        Checklist checklist = part.getEffectiveChecklist();
        List<Issue> issues = new ArrayList<Issue>(  );
        // conditions from context
        List<Condition> contextConditions = new ArrayList<Condition>(  );
        contextConditions.addAll( checklist.listNeedSatisfiedConditions() );
        contextConditions.addAll( checklist.listAssetAvailableConditions() );
        for ( final Condition condition : contextConditions ) {
            if ( !CollectionUtils.exists(
                    checklist.listAllEffectiveStepGuards(),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return (( StepGuard )object).getConditionRef().equals( condition.getRef() );
                        }
                    }
            )) {
                Issue issue = makeIssue( communityService, Issue.COMPLETENESS, part );
                issue.setDescription( "No checklist step has precondition '" + condition.getLabel() + "'" );
                issue.setSeverity( Level.Medium );
                issue.setRemediation( "Make '" + condition.getLabel() + "' a condition for at least one of the steps");
                issues.add( issue );
            }
        }
        // outcomes from context
        List<Outcome> contextOutcomes = new ArrayList<Outcome>(  );
        contextOutcomes.addAll( checklist.listEventTimingOutcomes() );
        contextOutcomes.addAll( checklist.listGoalAchievedOutcomes() );
        contextOutcomes.addAll( checklist.listCapabilityCreatedOutcomes() );
        contextOutcomes.addAll( checklist.listAssetProducedOutcomes() );
        contextOutcomes.addAll( checklist.listAssetProvisionedOutcomes() );
        for ( final Outcome outcome : contextOutcomes ) {
            if ( !CollectionUtils.exists(
                    checklist.getStepOutcomes(),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return ((StepOutcome)object).getOutcomeRef().equals( outcome.getRef() );
                        }
                    }
            )) {
                Issue issue = makeIssue( communityService, Issue.COMPLETENESS, part );
                issue.setDescription( "No checklist step has outcome '" + outcome.getLabel() + "'" );
                issue.setSeverity( Level.Medium );
                issue.setRemediation( "Make '" + outcome.getLabel() + "' an outcome for at least one of the steps");
                issues.add( issue );
            }
        }
        return issues;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Checklist never references one known condition or outcome of the task";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }

    @Override
    protected List<String> getTags() {
        return Arrays.asList( "checklist" );
    }


}
