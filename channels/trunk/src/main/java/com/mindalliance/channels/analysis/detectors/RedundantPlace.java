package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Severity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Redundant place.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 24, 2009
 * Time: 11:56:44 AM
 */
public class RedundantPlace extends AbstractIssueDetector {
    public RedundantPlace() {
    }

    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Place place = (Place) modelObject;
        List<Place> equivalentPlaces = findPlacesEquivalentTo( place );
        if ( !equivalentPlaces.isEmpty() ) {
            Issue issue = makeIssue( Issue.VALIDITY, place );
            issue.setSeverity( Severity.Minor );
            StringBuilder sb = new StringBuilder();
            Iterator<Place> iter = equivalentPlaces.iterator();
            while ( iter.hasNext() ) {
                sb.append( iter.next().getName() );
                if ( iter.hasNext() ) sb.append( ", " );
            }
            issue.setDescription( "This place has the same location as: " + sb.toString() );
            issue.setRemediation( "Remove references to this place\n"
                    +"or change its address\n"
                    +"or change its geolocation." );
            issues.add( issue );
        }
        return issues;
    }

    @SuppressWarnings( "unchecked" )
    private List<Place> findPlacesEquivalentTo( final Place place ) {
        return (List<Place>) CollectionUtils.select(
                getQueryService().list( Place.class ),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        Place other = (Place) obj;
                        return !other.equals( place )
                                && ( place.getWithin() == null && other.getWithin() == null )
                                && !place.getFullAddress().isEmpty()
                                && !other.getFullAddress().isEmpty()
                                && place.getFullAddress().equals( other.getFullAddress() );
                    }
                } );
    }

    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Place;
    }

    public String getTestedProperty() {
        return null;
    }

    protected String getLabel() {
        return "Redundant place";
    }

    public boolean canBeWaived() {
        return true;
    }
}
