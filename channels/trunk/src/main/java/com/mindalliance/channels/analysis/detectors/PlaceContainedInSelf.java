package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Place;

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
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Place place = (Place) modelObject;
        Place loopy = place.getLoopyContainingPlace();
        if ( loopy != null ) {
            Issue issue = makeIssue( Issue.VALIDITY, place );
            issue.setSeverity( Level.Medium );
            issue.setDescription( place.getName()
                    + " is within "
                    + loopy.getName()
                    + " which is within "
                    + place.getName() + "." );
            issue.setRemediation( "Change the place " + loopy.getName() + " is said to be within." );
            issues.add( issue );
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
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
    protected String getLabel() {
        return "The place is contained in itself.";
    }
}
