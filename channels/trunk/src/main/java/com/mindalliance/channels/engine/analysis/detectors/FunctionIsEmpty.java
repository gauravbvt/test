package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Function;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Function is empty.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/23/13
 * Time: 4:13 PM
 */
public class FunctionIsEmpty extends AbstractIssueDetector {

    public FunctionIsEmpty() {
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Function;
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        List<Issue> issues = new ArrayList<Issue>(  );
        Function function = (Function)modelObject;
        if ( function.isEmpty() ) {
            Issue issue = makeIssue( communityService, Issue.COMPLETENESS, function );
            issue.setDescription( "The function \""
                    + function.getName()
                    + "\" defines no goals, information needs or information to be shared.");
            issue.setRemediation( "Set a goal to be achieved" +
                    "\nor an information need" +
                    "\n or an information to be shared." );
            issue.setSeverity( Level.Low );
            issues.add( issue );
        }
        return issues;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Function is undefined";
    }
}
