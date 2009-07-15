package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.util.Matcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    public boolean canBeWaived() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected String getLabel() {
        return "Redundant task";
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
            DetectedIssue issue = makeIssue( DetectedIssue.VALIDITY, part );
            issue.setDescription(
                    "This task is restated "
                            + ( ( count == 1 ) ? "once." : count + " times." ) );
            issue.setRemediation( "Remove redundant task, or make the repeated tasks different." );
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
    
    // One narrows or equls the other
    private boolean isEquivalent( Part part, Part otherPart ) {
        return Matcher.same( part.getTask(), otherPart.getTask() )
                && (
                part.resourceSpec().narrowsOrEquals( otherPart.resourceSpec() )
                        || otherPart.resourceSpec().narrowsOrEquals( part.resourceSpec() )
        )
                && ( Matcher.samePlace( part.getLocation(), otherPart.getLocation() )
                || Matcher.within( part.getLocation(), otherPart.getLocation() )
                || Matcher.within( otherPart.getLocation(), part.getLocation() ) );
    }
}
