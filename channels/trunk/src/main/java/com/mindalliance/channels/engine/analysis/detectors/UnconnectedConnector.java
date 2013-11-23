package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.analysis.DetectedIssue;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects whether the plan segment has unsatisifed needs or unused capabilities.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 19, 2008
 * Time: 2:52:42 PM
 */
public class UnconnectedConnector extends AbstractIssueDetector {

    public UnconnectedConnector() {
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Part;
    }

    /**
     * {@inheritDoc}
     */
    public String getTestedProperty() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    protected String getKindLabel() {
        return "Information need or capability not taken into account";
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getPlanService();
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        for ( Flow capability : queryService.findUnusedCapabilities( part ) ) {
            DetectedIssue issue = makeIssue( communityService, DetectedIssue.COMPLETENESS, part );
            issue.setDescription( "'" + capability.getName() + "' is produced but never sent." );
            issue.setRemediation( "Share \"" + capability.getName() + "\" with a task that needs it." );
            issue.setSeverity( Level.Low );
            issues.add( issue );
        }
        for ( Flow need : queryService.findUnconnectedNeeds( part ) ) {
            DetectedIssue issue = makeIssue( communityService, DetectedIssue.COMPLETENESS, part );
            issue.setDescription(
                    ( need.isRequired() ? "Required " : "" )
                            + "'"
                            + need.getName()
                            + "' is needed but never received." );
            issue.setRemediation( "Have a task producing \"" + need.getName() + "\" share it with the needing task." );
            issue.setSeverity( need.isRequired() ? Level.Medium : Level.Low );
            issues.add( issue );
        }
        return issues;
    }
}
