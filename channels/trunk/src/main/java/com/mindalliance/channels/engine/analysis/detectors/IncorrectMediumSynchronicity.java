package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.core.query.QueryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 9/3/11
 * Time: 9:54 AM
 */
public class IncorrectMediumSynchronicity extends AbstractIssueDetector {

    public IncorrectMediumSynchronicity() {
    }

    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        TransmissionMedium medium = (TransmissionMedium) modelObject;
        for ( ModelEntity type : medium.getAllTypes() ) {
            TransmissionMedium mediumType = (TransmissionMedium) type;
            if ( mediumType.isSynchronous() != medium.isSynchronous() ) {
                Issue issue = makeIssue( communityService, Issue.VALIDITY, medium );
                issue.setDescription(
                        "\"" + medium.getName() + "\" is a kind of "
                                + "\"" + mediumType.getName() + "\" but "
                                + "\"" + medium.getName() + "\" "
                                + ( medium.isSynchronous() ? "is " : "is not " )
                                + "synchronous and "
                                + "\"" + mediumType.getName() + "\""
                                + ( mediumType.isSynchronous() ? " is." : " is not." ) );
                issue.setRemediation( "Make both "
                        + "\"" + medium.getName() + "\" and "
                        + "\"" + mediumType.getName() + "\""
                        + " synchronous \nor make them both not synchronous." );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            }
        }
        return issues;
    }

    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof TransmissionMedium;
    }

    public String getTestedProperty() {
        return null;
    }

    protected String getKindLabel() {
        return "Communication medium is inappropriately set as synchronous";
    }
}
