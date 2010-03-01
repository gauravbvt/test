package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;

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
    public boolean appliesTo( ModelObject modelObject ) {
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
    protected String getLabel() {
        return "Task without flows";
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        if ( !part.receives().hasNext() && !part.sends().hasNext() ) {
            DetectedIssue issue = makeIssue( DetectedIssue.COMPLETENESS, part );
            issue.setDescription( "Does not produce or need information." );
            issue.setRemediation( "Add sent information\nor add received information." );
            issue.setSeverity( getQueryService().getPartPriority( part ) );
            issues.add( issue );
        }
        return issues;
    }
}
