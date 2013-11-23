package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Function;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Information;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Function has info need or capability without EOIs.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/23/13
 * Time: 4:27 PM
 */
public class FunctionMissingEois extends AbstractIssueDetector {

    public FunctionMissingEois() {
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Function;
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        Function function = (Function)modelObject;
        List<Issue> issues = new ArrayList<Issue>(  );
        for ( Information info : function.getEffectiveInfoNeeded() ) {
            if ( info.getEffectiveEois().isEmpty() ) {
                Issue issue = makeIssue( communityService, Issue.COMPLETENESS, function );
                issue.setDescription( "Function \""
                        + function.getName()
                        + "\" defines an information need \""
                        + info.getName()
                        + "\" that has no element." );
                issue.setRemediation( "Add one or more element to needed information \""
                        + info.getName()
                        + "\"." );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            }
        }
        for ( Information info : function.getEffectiveInfoAcquired() ) {
            if ( info.getEffectiveEois().isEmpty() ) {
                Issue issue = makeIssue( communityService, Issue.COMPLETENESS, function );
                issue.setDescription( "Function \""
                        + function.getName()
                        + "\" defines to-be-shared information \""
                        + info.getName()
                        + "\" that has no element." );
                issue.setRemediation( "Add one or more element to to-be-shared information \""
                        + info.getName()
                        + "\"." );
                issue.setSeverity( Level.Low );
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
    public boolean canBeWaived() {
        return true;
    }

    @Override
    protected String getKindLabel() {
        return "Function is missing elements of information";
    }
}
