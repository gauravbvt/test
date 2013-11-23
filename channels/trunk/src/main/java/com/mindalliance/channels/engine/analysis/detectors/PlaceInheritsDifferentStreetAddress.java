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
 * Place inherits different street address.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 23, 2009
 * Time: 4:02:16 PM
 */
public class PlaceInheritsDifferentStreetAddress extends AbstractIssueDetector {

    public PlaceInheritsDifferentStreetAddress() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Place place = (Place) modelObject;
        if ( place.getWithin() != null ) {
            String withinAddress = place.getWithin().getActualStreetAddress();
            String streetAddress = place.getStreetAddress();
            if ( streetAddress != null && !streetAddress.isEmpty()
                    && withinAddress != null && !withinAddress.isEmpty()
                    && !streetAddress.equals( withinAddress ) ) {
                Issue issue = makeIssue( communityService, Issue.VALIDITY, place );
                issue.setSeverity( Level.Low );
                issue.setDescription( place.getName()
                        + " is within a place with a different address." );
                issue.setRemediation( "Change the address\nor remove it." );
                issues.add( issue );
            }
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
        return "streetAddress";
    }

    /**
     * {@inheritDoc}
     */
    protected String getKindLabel() {
        return "Street address different from containing place";
    }

}
