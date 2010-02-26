package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Severity;

import java.util.ArrayList;
import java.util.List;

/**
 * Place inherits different postal code.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 23, 2009
 * Time: 4:11:30 PM
 */
public class PlaceInheritsDifferentPostalCode extends AbstractIssueDetector {
    public PlaceInheritsDifferentPostalCode() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Place place = (Place) modelObject;
        if ( place.getWithin() != null ) {
            String withinPostalCode = place.getWithin().getActualPostalCode();
            String postalCode = place.getPostalCode();
            if ( !postalCode.isEmpty()
                    && !withinPostalCode.isEmpty()
                    && !postalCode.equals( withinPostalCode ) ) {
                Issue issue = makeIssue( Issue.VALIDITY, place );
                issue.setSeverity( Severity.Minor );
                issue.setDescription( place.getName()
                        + " is within a place with a different postal code." );
                issue.setRemediation( "Change the postal code\nor remove it." );
                issues.add( issue );
            }
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
        return "postalCode";
    }

    /**
     * {@inheritDoc}
     */
    protected String getLabel() {
        return "Postal code different from containing place.";
    }

}
