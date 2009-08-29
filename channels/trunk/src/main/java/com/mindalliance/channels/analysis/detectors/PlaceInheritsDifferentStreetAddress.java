package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Place;

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
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Place place = (Place) modelObject;
        if ( place.getWithin() != null ) {
            String withinAddress = place.getWithin().getActualStreetAddress();
            String streetAddress = place.getStreetAddress();
            if ( !streetAddress.isEmpty()
                    && !withinAddress.isEmpty()
                    && !streetAddress.equals( withinAddress ) ) {
                Issue issue = makeIssue( Issue.VALIDITY, place );
                issue.setSeverity( Issue.Level.Minor );
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
    public boolean appliesTo( ModelObject modelObject ) {
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
    protected String getLabel() {
        return "Street address different from containing place.";
    }

}
