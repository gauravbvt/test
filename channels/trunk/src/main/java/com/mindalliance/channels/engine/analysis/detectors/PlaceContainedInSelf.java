package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.query.QueryService;

import java.util.ArrayList;
import java.util.List;

/**
 * A place is within itself.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 23, 2009
 * Time: 3:50:18 PM
 */
public class PlaceContainedInSelf extends AbstractIssueDetector {

    public PlaceContainedInSelf() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Place place = (Place) modelObject;
        Place loopy = place.getLoopyContainingPlace();
        if ( loopy != null ) {
            Issue issue = makeIssue( communityService, Issue.VALIDITY, place );
            issue.setSeverity( Level.Medium );
            issue.setDescription( place.getName()
                    + " is within "
                    + loopy.getName()
                    + " which is within "
                    + place.getName() + "." );
            issue.setRemediation( "Change the place " + loopy.getName() + " is said to be within" +
                    "\nor define the place to be within none." );
            issues.add( issue );
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Place;
    }

    /**
     * {@inheritDoc}
     */
    public String getTestedProperty() {
        return "within";
    }

    /**
     * {@inheritDoc}
     */
    protected String getKindLabel() {
        return "Place contained in itself";
    }
}
