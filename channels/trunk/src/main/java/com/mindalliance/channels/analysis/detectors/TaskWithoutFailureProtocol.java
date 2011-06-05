package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Task with major (or greater) failure impact has no failure notification protocol.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 3, 2010
 * Time: 1:06:11 PM
 */
public class TaskWithoutFailureProtocol extends AbstractIssueDetector {
    
    public TaskWithoutFailureProtocol() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        Part part = (Part) modelObject;
        List<Issue> issues = new ArrayList<Issue>();
        Level failureSeverity = getTaskFailureSeverity( part );
        if ( failureSeverity.compareTo( Level.Low ) >= 1 ) {
            boolean notifiesOfFailure = CollectionUtils.exists(
                    part.getAllSharingSends(),
                    new Predicate() {
                        public boolean evaluate( Object object ) {
                            Flow send = (Flow) object;
                            return send.isNotification() && send.isIfTaskFails();
                        }
                    }
            );
            if ( !notifiesOfFailure ) {
                Issue issue = makeIssue( Issue.ROBUSTNESS, part );
                issue.setDescription( "Failure of this task would have "
                        + failureSeverity.getNegativeLabel().toLowerCase()
                        + " consequences, yet no one is to be notified should it fail." );
                issue.setRemediation( "Add a notification conditional on task failure." );
                issue.setSeverity( failureSeverity );
                issues.add( issue );
            }
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Part;
    }

    /**
     * {@inheritDoc}
     */
    public String getTestedProperty() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    protected String getKindLabel() {
        return "Important task without failure notification protocol";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
