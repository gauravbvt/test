package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.model.Conceptualizable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * A part or flow was marked as de facto conceptual but no reason was given.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 8/30/11
 * Time: 11:17 AM
 */
public class ConceptualDeFactoWithoutReason extends AbstractIssueDetector {

    public ConceptualDeFactoWithoutReason() {
    }

    @Override
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Conceptualizable conceptualizable = (Conceptualizable) modelObject;
        if ( conceptualizable.isConceptual() && conceptualizable.getConceptualReason().isEmpty() ) {
            Issue issue = makeIssue( Issue.COMPLETENESS, (ModelObject) conceptualizable, "conceptualReason" );
            issue.setDescription( "Marked de facto conceptual but no reason is given." );
            issue.setRemediation( "Provide a reason\nor unmark as de facto conceptual." );
            issue.setSeverity( Level.Low );
            issues.add( issue );
        }
        return issues;
    }

    @Override
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Conceptualizable;
    }

    @Override
    public String getTestedProperty() {
        return "conceptualReason";
    }

    @Override
    protected String getKindLabel() {
        return "De facto conceptual but no reason is given";
    }
}
