package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
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
import java.util.List;

/**
 * Need satisfaction, capability creation, caused/observed event, achieved goal not mapped to a step in the task's checklist.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/24/13
 * Time: 9:07 AM
 */
public class UnmatchedToChecklistStep extends AbstractIssueDetector {

    public UnmatchedToChecklistStep() {
    }

    @Override
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Part;
    }

    @Override
    public List<? extends Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        Part part = (Part)modelObject;
        Checklist checklist = part.getEffectiveChecklist();
        List<Issue> issues = new ArrayList<Issue>(  );
        // conditions from context
        List<Condition> contextConditions = new ArrayList<Condition>(  );
        contextConditions.addAll( checklist.listEventTimingConditions() );
        contextConditions.addAll( checklist.listGoalConditions() );
        contextConditions.addAll( checklist.listNeedSatisfiedConditions() );
        for ( final Condition condition : contextConditions ) {
            if ( !CollectionUtils.exists(
                    checklist.getStepGuards(),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return (( StepGuard )object).getConditionRef().equals( condition.getRef() );
                        }
                    }
            )) {
                Issue issue = makeIssue( queryService, Issue.COMPLETENESS, part );
                issue.setDescription( "No checklist step has precondition '" + condition.getLabel() + "'" );
                issue.setSeverity( queryService.computePartPriority( part ) );
                issue.setRemediation( "Make '" + condition.getLabel() + "' a condition for at least one of the steps");
                issues.add( issue );
            }
        }
        // outcomes from context
        List<Outcome> contextOutcomes = new ArrayList<Outcome>(  );
        contextOutcomes.addAll( checklist.listEventTimingOutcomes() );
        contextOutcomes.addAll( checklist.listGoalAchievedOutcomes() );
        contextOutcomes.addAll( checklist.listCapabilityCreatedOutcomes() );
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
                Issue issue = makeIssue( queryService, Issue.COMPLETENESS, part );
                issue.setDescription( "No checklist step has outcome '" + outcome.getLabel() + "'" );
                issue.setSeverity( queryService.computePartPriority( part ) );
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
        return "Checklist not fully connected to task context";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
