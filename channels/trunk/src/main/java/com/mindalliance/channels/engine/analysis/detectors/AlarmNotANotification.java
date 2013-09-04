package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * A flow with intent Alarm is not a notification.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Sep 9, 2010
 * Time: 2:18:28 PM
 */
public class AlarmNotANotification extends AbstractIssueDetector {

    public AlarmNotANotification() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Flow flow = (Flow) modelObject;
        Flow.Intent intent = flow.getIntent();
        if ( intent != null && intent.equals( Flow.Intent.Alarm ) ) {
            if ( flow.isAskedFor() ) {
                Issue issue = makeIssue( queryService, Issue.ROBUSTNESS, flow );
                issue.setSeverity( flow.isSharing() ? computeSharingFailureSeverity( queryService, flow ) : Level.Low );
                issue.setDescription( "Information \""
                        + flow.getName()
                        + "\" is intended as an alarm and yet it must be requested." );
                issue.setRemediation( "Make this a notification"
                + "\nor have the information be intended as something other than an alarm.");
                issues.add( issue );
            }
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Flow;
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
        return "An alarm flow is not a notification";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
