/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Redundant place.
 */
public class RedundantPlace extends AbstractIssueDetector {

    public RedundantPlace() {
    }

    @Override
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getModelService();
        List<Issue> issues = new ArrayList<Issue>();
        Place place = (Place) modelObject;
        List<Place> equivalentPlaces = findPlacesEquivalentTo( queryService, place );
        if ( !equivalentPlaces.isEmpty() ) {
            Issue issue = makeIssue( communityService, Issue.COMPLETENESS, place );
            issue.setSeverity( Level.Low );
            StringBuilder sb = new StringBuilder();
            Iterator<Place> iter = equivalentPlaces.iterator();
            while ( iter.hasNext() ) {
                sb.append( iter.next().getName() );
                if ( iter.hasNext() )
                    sb.append( ", " );
            }
            issue.setDescription( "This place has the same location as: " + sb.toString() );
            issue.setRemediation(
                    "Remove references to this place\n" + "or change its address\n" + "or change its geolocation." );
            issues.add( issue );
        }
        return issues;
    }

    @SuppressWarnings( "unchecked" )
    private static List<Place> findPlacesEquivalentTo( QueryService queryService, final Place place ) {
        return (List<Place>) CollectionUtils.select( queryService.list( Place.class ), new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                Place other = (Place) object;
                return !other.equals( place ) && ( place.getWithin() == null && other.getWithin() == null )
                       && !place.getFullAddress().isEmpty() && !other.getFullAddress().isEmpty()
                       && place.getFullAddress().equals( other.getFullAddress() );
            }
        } );
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Place;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Redundant place";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
