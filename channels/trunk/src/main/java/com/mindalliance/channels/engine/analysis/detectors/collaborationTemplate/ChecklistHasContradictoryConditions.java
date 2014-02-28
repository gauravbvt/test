package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.checklist.Checklist;
import com.mindalliance.channels.core.model.checklist.Step;
import com.mindalliance.channels.core.model.checklist.StepGuard;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Checklist has contradictory conditions.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/29/13
 * Time: 11:11 AM
 */
public class ChecklistHasContradictoryConditions extends AbstractIssueDetector {

    public ChecklistHasContradictoryConditions() {
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Part;
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getModelService();
        Part part = (Part) modelObject;
        Checklist checklist = part.getEffectiveChecklist();
        List<Issue> issues = new ArrayList<Issue>();
        if ( !checklist.isEmpty() ) {
            for ( Step step : checklist.listEffectiveSteps() ) {
                if ( hasMutuallyExclusiveConditions( checklist, step ) ) {
                    Issue issue = makeIssue( communityService, Issue.VALIDITY, part );
                    issue.setDescription( "The step \""
                            + step.getLabel()
                            + "\" in the checklist has an identical \"if\" and \"unless\" condition." );
                    issue.setSeverity( computeTaskFailureSeverity( queryService, part ) );
                    issue.setRemediation( "Remove the \"if\" condition that is repeated as an \"unless\" condition"
                            + "\nor remove the \"unless\" condition that is repeated as an \"if\" condition." );
                    issues.add( issue );
                }
            }
        }

        return issues;
    }

    @SuppressWarnings( "unchecked" )
    private boolean hasMutuallyExclusiveConditions( Checklist checklist, Step step ) {
        List<String> ifConditionRefs = (List<String>) CollectionUtils.collect(
                checklist.listEffectiveStepGuardsFor( step, true ),
                new Transformer() {
                    @Override
                    public Object transform( Object input ) {
                        return ( (StepGuard) input ).getConditionRef();
                    }
                } );
        List<String> unlessConditionrefs = (List<String>) CollectionUtils.collect(
                checklist.listEffectiveStepGuardsFor( step, false ),
                new Transformer() {
                    @Override
                    public Object transform( Object input ) {
                        return ( (StepGuard) input ).getConditionRef();
                    }
                } );
        return !Collections.disjoint( ifConditionRefs, unlessConditionrefs );
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
        return "Checklist step has contradictory conditions";
    }
}
