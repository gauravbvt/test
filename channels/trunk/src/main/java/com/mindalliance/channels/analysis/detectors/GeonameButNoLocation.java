package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Place;

import java.util.ArrayList;
import java.util.List;

/**
 * A place has a non-empty geoname for which no geolocation can be found.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 16, 2009
 * Time: 7:51:29 PM
 */
public class GeonameButNoLocation extends AbstractIssueDetector {

    public GeonameButNoLocation() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Place place = (Place) modelObject;
        String geoname = place.getGeoname();
        if ( geoname != null && !place.getGeoname().isEmpty() && place.getGeoLocations().isEmpty() ) {
            DetectedIssue issue = makeIssue( Issue.VALIDITY, place, getTestedProperty() );
            issue.setSeverity( Issue.Level.Major );
            issue.setDescription( "No geolocation could be found that might correspond to the place's geoname." );
            issue.setRemediation( "Change the geoname or remove it." );
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
        return "geoname";
    }

    /**
     * {@inheritDoc}
     */
    protected String getLabel() {
        return "Geoname corresponds to no geolocation.";
    }
}
