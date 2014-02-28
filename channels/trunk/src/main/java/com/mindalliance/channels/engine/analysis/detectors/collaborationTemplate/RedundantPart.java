/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.analysis.DetectedIssue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Detects that a part has a duplicate.
 */
public class RedundantPart extends AbstractIssueDetector {

    public RedundantPart() {
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Part;
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }

    @Override
    protected String getKindLabel() {
        return "Redundant task";
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getModelService();
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        List<Part> equivalentParts = findEquivalentTo( part, queryService.getPlanLocale() );
        int count = equivalentParts.size();
        if ( count > 0 ) {
            DetectedIssue issue = makeIssue( communityService, DetectedIssue.COMPLETENESS, part );
            issue.setDescription( "This task is restated " + ( count == 1 ? "once." : count + " times." ) );
            issue.setRemediation( "Remove redundant task\nor specify one of the tasks differently." );
            issue.setSeverity( Level.Low );
            issues.add( issue );
        }
        return issues;
    }

    private List<Part> findEquivalentTo( Part part, Place locale ) {
        List<Part> equivalentParts = new ArrayList<Part>();
        Iterator<Part> parts = part.getSegment().parts();
        while ( parts.hasNext() ) {
            Part otherPart = parts.next();
            if ( otherPart != part && isEquivalent( part, otherPart, locale ) )
                equivalentParts.add( otherPart );
        }
        return equivalentParts;
    }

    // One narrows or equals the other
    private static boolean isEquivalent( Part part, Part otherPart, Place locale ) {
        return Matcher.same( part.getTask(), otherPart.getTask() )

               && ( part.resourceSpec().narrowsOrEquals( otherPart.resourceSpec(), locale )
                    || otherPart.resourceSpec().narrowsOrEquals( part.resourceSpec(), locale ) )

               && ( ModelEntity.implies( part.getKnownLocation(), otherPart.getKnownLocation(), locale )
                    || ModelEntity.implies( otherPart.getKnownLocation(), part.getKnownLocation(), locale ) );
    }
}
