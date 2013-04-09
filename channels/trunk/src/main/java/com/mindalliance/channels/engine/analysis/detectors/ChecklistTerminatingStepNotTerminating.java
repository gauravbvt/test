package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.checklist.Checklist;
import com.mindalliance.channels.core.model.checklist.Step;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A terminating step in a checklist is a prerequisite to another.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/30/13
 * Time: 5:28 PM
 */
public class ChecklistTerminatingStepNotTerminating extends AbstractIssueDetector {

    public ChecklistTerminatingStepNotTerminating() {
    }

    @Override
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Part;
    }

    @Override
    public List<? extends Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        Part part = (Part) modelObject;
        Checklist checklist = part.getChecklist();
        List<Issue> issues = new ArrayList<Issue>();
        if ( !checklist.isEmpty() ) {
            for ( Step step : checklist.listEffectiveSteps() ) {
                if ( step.isTerminating() ) {
                    for ( Step otherStep : checklist.listEffectiveSteps() ) {
                        if ( !otherStep.equals( step ) ) {
                            List prerequisites = checklist.listPrerequisiteStepsFor( otherStep );
                            if ( prerequisites.contains( step ) ) {
                                Issue issue = makeIssue( queryService, Issue.VALIDITY, part );
                                issue.setDescription( "The step \""
                                        + step.getLabel()
                                        + "\" in the checklist is a terminating step yet step \""
                                        + otherStep.getLabel()
                                        + "\" must come after it." );
                                issue.setSeverity( computeTaskFailureSeverity( queryService, part ) );
                                issue.setRemediation( "Remove the terminating step \""
                                        + step.getLabel()
                                        + "\" as a prerequisite for \""
                                        + otherStep.getLabel() + "\"." );
                                issues.add( issue );
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
    protected List<String> getTags() {
        return Arrays.asList( "checklist" );
    }

    @Override
    protected String getKindLabel() {
        return "Terminating step in checklist is prerequisite to another";
    }
}
