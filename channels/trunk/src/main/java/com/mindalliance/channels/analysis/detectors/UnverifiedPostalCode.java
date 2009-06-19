package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Place;

import java.util.ArrayList;
import java.util.List;

/**
 * A place's postal code can't be verified.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 16, 2009
 * Time: 8:06:34 PM
 */
public class UnverifiedPostalCode extends AbstractIssueDetector {

    public UnverifiedPostalCode() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Place place = (Place) modelObject;
        String postalCode = place.getPostalCode();
        if ( postalCode != null && !postalCode.isEmpty() ) {
            if ( place.getGeoLocation() == null ) {
                Issue issue = makeIssue( Issue.VALIDITY, place, getTestedProperty() );
                issue.setSeverity( Issue.Level.Minor );
                issue.setDescription( "Can't verify the postal code without a geolocation." );
                issue.setRemediation( "Make sure the geoname is set and valid, and choose a geolocation." );
                issues.add( issue );
            } else {
                if ( !getGeoService().verifyPostalCode( place.getPostalCode(), place.getGeoLocation() ) ) {
                    Issue issue = makeIssue( Issue.VALIDITY, place, getTestedProperty() );
                    issue.setSeverity( Issue.Level.Minor );
                    issue.setDescription( "Can't verify the postal code for the geolocation." );
                    issue.setRemediation( "Make sure the postal code or the geolocation are correct." );
                    issues.add( issue );
                }
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
        return "Postal code can't be verified.";
    }
}
