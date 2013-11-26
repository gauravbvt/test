package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Part is terminated by more than one send.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/8/11
 * Time: 8:45 AM
 */
public class PartTerminatedByManySends extends AbstractIssueDetector {

    public PartTerminatedByManySends() {
    }

    @Override
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        List<Issue> issues = new ArrayList<Issue>(  );
        Part part = (Part)modelObject;
        int count = CollectionUtils.countMatches(
                part.getAllSharingSends(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ((Flow)object).isTerminatingToSource();
                    }
                }
        );
        if ( count > 1 ) {
            Issue issue = makeIssue( communityService, Issue.VALIDITY, part );
            issue.setDescription( "This task is ambiguously terminated by " + count + " sends." );
            issue.setRemediation( "Have the task terminated by at most one send." );
            issue.setSeverity( Level.Medium );
            issues.add( issue );
        }
        return issues;
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Part;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "More than one send can terminate the task";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
