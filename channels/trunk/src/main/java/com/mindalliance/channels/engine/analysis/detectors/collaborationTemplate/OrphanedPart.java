package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.analysis.DetectedIssue;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects that a part has no receives and no sends.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 20, 2008
 * Time: 9:47:55 AM
 */
public class OrphanedPart extends AbstractIssueDetector {

    public OrphanedPart() {
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
        return "Task neither consumes nor produces information";
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getModelService();
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        if ( part.getAllSharingReceives().isEmpty() && part.getAllSharingSends().isEmpty() ) {
            DetectedIssue issue = makeIssue( communityService, DetectedIssue.COMPLETENESS, part );
            issue.setDescription( "Does not produce nor need information." );
            issue.setRemediation( "Add information received\nor add information sent." );
            issue.setSeverity( queryService.computePartPriority( part ) );
            issues.add( issue );
        }
        return issues;
    }
}
