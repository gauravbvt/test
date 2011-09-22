package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.engine.query.QueryService;

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
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Place place = (Place) modelObject;
        if ( place.getWithin() != null ) {
            String withinPostalCode = place.getWithin().getActualPostalCode();
            String postalCode = place.getPostalCode();
            if ( !postalCode.isEmpty()
                    && !withinPostalCode.isEmpty()
                    && !postalCode.equals( withinPostalCode ) ) {
                Issue issue = makeIssue( queryService, Issue.VALIDITY, place );
                issue.setSeverity( Level.Low );
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
    protected String getKindLabel() {
        return "Postal code different from containing place";
    }

}
