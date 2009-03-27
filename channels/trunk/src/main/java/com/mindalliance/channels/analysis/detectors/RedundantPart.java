package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.util.SemMatch;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Detects that a part has a duplicate.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 13, 2009
 * Time: 10:56:44 AM
 */
public class RedundantPart extends AbstractIssueDetector {

    public RedundantPart() {
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
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        List<Part> equivalentParts = findEquivalentTo( part );
        int count = equivalentParts.size();
        if ( count > 0 ) {
            DetectedIssue issue = new DetectedIssue( DetectedIssue.DEFINITION, part );
            issue.setDescription(
                    "This part is restated "
                            + ( ( count == 1 ) ? "once." : count + " times." ) );
            issue.setRemediation( "Remove redundant parts, or make them different." );
            issue.setSeverity( Issue.Level.Minor );
            issues.add( issue );
        }
        return issues;
    }

    private List<Part> findEquivalentTo( Part part ) {
        List<Part> equivalentParts = new ArrayList<Part>();
        Iterator<Part> parts = part.getScenario().parts();
        while ( parts.hasNext() ) {
            Part otherPart = parts.next();
            if ( otherPart != part && isEquivalent( part, otherPart ) ) {
                equivalentParts.add( otherPart );
            }
        }
        return equivalentParts;
    }

    private boolean isEquivalent( Part part, Part otherPart ) {
        return SemMatch.same( part.getTask(), otherPart.getTask() )
                && (
                part.resourceSpec().narrowsOrEquals( otherPart.resourceSpec() )
                        || otherPart.resourceSpec().narrowsOrEquals( part.resourceSpec() )
        )
                && (
                part.getLocation() == null
                        || otherPart.getLocation() == null
                        || SemMatch.samePlace( part.getLocation(), otherPart.getLocation() ) );
    }
}
