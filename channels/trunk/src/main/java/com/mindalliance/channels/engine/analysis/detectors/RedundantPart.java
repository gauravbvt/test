package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.analysis.DetectedIssue;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.engine.nlp.Matcher;

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
    protected String getKindLabel() {
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
            DetectedIssue issue = makeIssue( DetectedIssue.COMPLETENESS, part );
            issue.setDescription(
                    "This task is restated "
                            + ( ( count == 1 ) ? "once." : count + " times." ) );
            issue.setRemediation( "Remove redundant task\nor specify one of the tasks differently." );
            issue.setSeverity( Level.Low );
            issues.add( issue );
        }
        return issues;
    }

    private List<Part> findEquivalentTo( Part part ) {
        List<Part> equivalentParts = new ArrayList<Part>();
        Iterator<Part> parts = part.getSegment().parts();
        while ( parts.hasNext() ) {
            Part otherPart = parts.next();
            if ( otherPart != part && isEquivalent( part, otherPart ) ) {
                equivalentParts.add( otherPart );
            }
        }
        return equivalentParts;
    }

    // One narrows or equals the other
    private boolean isEquivalent( Part part, Part otherPart ) {
        Place locale = getPlan().getLocale();
        return Matcher.getInstance().same( part.getTask(), otherPart.getTask() )

            && ( part.resourceSpec().narrowsOrEquals( otherPart.resourceSpec(), locale )
                 || otherPart.resourceSpec().narrowsOrEquals( part.resourceSpec(), locale ) )

            && ( ModelEntity.implies( part.getLocation(), otherPart.getLocation(), locale )
                 || ModelEntity.implies( otherPart.getLocation(), part.getLocation(), locale ) );
    }
}
