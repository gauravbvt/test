package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.analysis.DetectedIssue;

import java.util.ArrayList;
import java.util.List;

/**
 * An information need with an empty description (i.e. no elements of information)
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 14, 2009
 * Time: 2:46:31 PM
 */
public class EmptyNeedOrCapability extends AbstractIssueDetector {

    public EmptyNeedOrCapability() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getModelService();
        Flow flow = (Flow) modelObject;
        List<Issue> issues = new ArrayList<Issue>();
        if ( !flow.getName().trim().isEmpty() && flow.getEffectiveEois().isEmpty() ) {
            DetectedIssue issue = makeIssue( communityService, Issue.COMPLETENESS, flow );
            issue.setSeverity( Level.Low );
            String needOrCapability = flow.getTarget().isConnector() ? "capability" : "need";
            issue.setDescription( "Information "
                    + needOrCapability
                    + " has no element of information." );
            issue.setRemediation( "Add elements of information" );
            issues.add( issue );
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Flow
                && ( ( (Flow) modelObject ).getSource().isConnector()
                || ( (Flow) modelObject ).getTarget().isConnector() );
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
        return "Undefined information need";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
