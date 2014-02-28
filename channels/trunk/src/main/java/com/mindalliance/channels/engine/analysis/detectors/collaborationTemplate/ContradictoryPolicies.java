package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Prohibitable;
import com.mindalliance.channels.core.query.QueryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Attached policies contradict one another.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 4, 2010
 * Time: 1:25:46 PM
 */
public class ContradictoryPolicies extends AbstractIssueDetector {

    public ContradictoryPolicies() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( CommunityService communityService, Identifiable identifiable ) {
        QueryService queryService = communityService.getModelService();
        ModelObject modelObject = (ModelObject)identifiable;
        List<Issue> issues = new ArrayList<Issue>();
        if ( ((Prohibitable)modelObject).isProhibited() && modelObject.hasMandatingPolicy() ) {
            Issue issue = makeIssue( communityService, Issue.VALIDITY, modelObject );
            issue.setDescription( "Mandating policy attached to prohibited "
                    + modelObject.getKindLabel()
                    + "." );
            issue.setSeverity( Level.Medium );
            issue.setRemediation( "Remove prohibition"
                    + "\nor remove all mandating policies" );
            issues.add( issue );
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Prohibitable;
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
        return "Contradictory policies";
    }
}
