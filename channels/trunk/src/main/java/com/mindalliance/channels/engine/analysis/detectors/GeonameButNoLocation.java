package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.analysis.DetectedIssue;
import com.mindalliance.channels.core.model.GeoLocation;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Place;

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

    @Override
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Place place = (Place) modelObject;
        String geoname = place.getGeoname();
        List<GeoLocation> geoLocations = place.getGeoLocations();
        if ( geoname != null && !geoname.isEmpty() && ( geoLocations == null || geoLocations.isEmpty() ) ) {
            DetectedIssue issue = makeIssue( Issue.VALIDITY, place, getTestedProperty() );
            issue.setSeverity( Level.Medium );
            issue.setDescription( "The place's geoname is unknown. No geolocation could be found for it." );
            issue.setRemediation( "Change the geoname\nor remove it." );
            issues.add( issue );
        }
        return issues;
    }

    @Override
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Place;
    }

    @Override
    public String getTestedProperty() {
        return "geoname";
    }

    @Override
    protected String getKindLabel() {
        return "Can't put place on a map";
    }
}
