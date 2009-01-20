package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Part;

import java.util.List;
import java.util.ArrayList;

/**
 * Detects that a part has no requirements and no outcome.
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
    protected List<Issue> doDetectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        if ( !part.requirements().hasNext() && !part.outcomes().hasNext() ) {
            Issue issue = new Issue( Issue.STRUCTURAL, part );
            issue.setDescription( "Does not produce or need information." );
            issue.setRemediation( "Add sent or received information." );
            issues.add( issue );
        }
        return issues;
    }
}
