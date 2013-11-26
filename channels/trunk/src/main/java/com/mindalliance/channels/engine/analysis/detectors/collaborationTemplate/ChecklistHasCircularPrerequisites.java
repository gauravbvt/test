package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
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
 * Checklist has steps with circular prerequisites.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/29/13
 * Time: 10:55 AM
 */
public class ChecklistHasCircularPrerequisites extends AbstractIssueDetector {

    public ChecklistHasCircularPrerequisites() {
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Part;
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getPlanService();
        Part part = (Part) modelObject;
        Checklist checklist = part.getEffectiveChecklist();
        List<Issue> issues = new ArrayList<Issue>();
        if ( !checklist.isEmpty() ) {
            for ( Step step : checklist.listEffectiveSteps() ) {
                if ( checklist.hasCircularPrerequisites( step ) ) {
                    Issue issue = makeIssue( communityService, Issue.VALIDITY, part );
                    issue.setDescription( "The step \""
                            + step.getLabel()
                            + "\" in the checklist has itself, indirectly, as a prerequisite step." );
                    issue.setSeverity( computeTaskFailureSeverity( queryService, part ) );
                    issue.setRemediation( "Change the steps this step must come after"
                            + ( step.isActionStep() ? "\nor remove the step." : "." ) );
                    issues.add( issue );
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
        return Arrays.asList("checklist");
    }

    @Override
    protected String getKindLabel() {
        return "Checklist has steps with invalid prerequisites";
    }
}
