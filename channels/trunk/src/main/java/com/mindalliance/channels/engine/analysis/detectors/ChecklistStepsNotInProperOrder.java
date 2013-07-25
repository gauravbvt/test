package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.model.Information;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.checklist.CapabilityCreatedOutcome;
import com.mindalliance.channels.core.model.checklist.Checklist;
import com.mindalliance.channels.core.model.checklist.CommunicationStep;
import com.mindalliance.channels.core.model.checklist.Condition;
import com.mindalliance.channels.core.model.checklist.Outcome;
import com.mindalliance.channels.core.model.checklist.Step;
import com.mindalliance.channels.core.model.checklist.StepGuard;
import com.mindalliance.channels.core.model.checklist.StepOutcome;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * A step should be pre-requisite to another but is not.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/24/13
 * Time: 12:35 PM
 */
public class ChecklistStepsNotInProperOrder extends AbstractIssueDetector {

    public ChecklistStepsNotInProperOrder() {
    }

    @Override
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Part;
    }

    @Override
    public List<? extends Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        Checklist checklist = part.getEffectiveChecklist();
        // Steps with matching outcome and condition not in expected pre-requisite relationship
        for ( StepOutcome stepOutcome : checklist.getStepOutcomes() ) {
            for ( StepGuard stepGuard : checklist.getStepGuards() ) {
                if ( stepGuard.isPositive() ) {
                    Condition condition = checklist.deRefCondition( stepGuard.getConditionRef() );
                    Outcome outcome = checklist.deRefOutcome( stepOutcome.getOutcomeRef() );
                    if ( condition != null && outcome != null ) {
                        if ( condition.matches( outcome ) ) {
                            Step outcomeStep = checklist.derefStep( stepOutcome.getStepRef() );
                            Step conditionStep = checklist.derefStep( stepGuard.getStepRef() );
                            if ( !outcomeStep.equals( conditionStep )
                                    && !checklist.listPrerequisiteStepsFor( conditionStep ).contains( outcomeStep ) ) {
                                Issue issue = makeIssue( queryService, Issue.VALIDITY, part );
                                issue.setDescription( "Step \""
                                        + conditionStep.getLabel()
                                        + "\" has condition '"
                                        + condition.getLabel()
                                        + "' but does not have as a direct or indirect pre-requisite step \""
                                        + outcomeStep.getLabel()
                                        + "\" with outcome '"
                                        + outcome.getLabel()
                                );
                                issue.setSeverity( Level.Medium );
                                issue.setRemediation( "Make step \""
                                        + outcomeStep.getLabel()
                                        + "\" a direct or indirect pre-requisite of step \""
                                        + conditionStep
                                        + "\"\nor remove outcome \'"
                                        + outcome.getLabel()
                                        + "'\nor remove condition '"
                                        + condition.getLabel()
                                        + "'"
                                );
                            }
                        }
                    }
                }
            }
        }
        // Step creating capability not a pre-requisite of communication step with matching notification
        for ( Step step : checklist.listEffectiveSteps() ) {
            if ( step.isCommunicationStep() ) {
                CommunicationStep communicationStep = (CommunicationStep) step;
                if ( ( communicationStep ).isNotification() ) {
                    Information sharedInfo = new Information( communicationStep.getSharing() );
                    for ( StepOutcome stepOutcome : checklist.getStepOutcomes() ) {
                        Outcome outcome = checklist.deRefOutcome( stepOutcome.getOutcomeRef() );
                        if ( outcome != null && outcome.isCapabilityCreatedOutcome() ) {
                            CapabilityCreatedOutcome capabilityCreatedOutcome = (CapabilityCreatedOutcome) outcome;
                            Information capabilityInfo = capabilityCreatedOutcome.getCapability();
                            if ( capabilityInfo.narrowsOrEquals( sharedInfo ) ) {
                                Step outcomeStep = checklist.derefStep( stepOutcome.getStepRef() );
                                if ( outcomeStep != null
                                        && !communicationStep.equals( outcomeStep )
                                        && !checklist.listPrerequisiteStepsFor( communicationStep ).contains( outcomeStep ) ) {
                                    Issue issue = makeIssue( queryService, Issue.VALIDITY, part );
                                    issue.setDescription( "Step \""
                                            + communicationStep.getLabel()
                                            + "\" shares information '"
                                            + sharedInfo.toString()
                                            + "' but does not have as a direct or indirect pre-requisite step \""
                                            + outcomeStep.getLabel()
                                            + "\" with outcome '"
                                            + outcome.getLabel()
                                    );
                                    issue.setSeverity( Level.Medium );
                                    issue.setRemediation( "Make step \""
                                            + outcomeStep.getLabel()
                                            + "\" a direct or indirect pre-requisite of step \""
                                            + communicationStep
                                            + "\"\nor remove outcome \'"
                                            + outcome.getLabel()
                                    );
                                }
                            }
                        }
                    }
                }
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
        return "Step in checklist should be pre-requisite to another but is not";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
