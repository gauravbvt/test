package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.TransmissionMedium;

import java.util.ArrayList;
import java.util.List;

/**
 * Medium's mode in neither set nor inherited.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 8, 2010
 * Time: 9:32:07 AM
 */
public class MediumWithoutMode extends AbstractIssueDetector {

    public MediumWithoutMode() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        TransmissionMedium medium = (TransmissionMedium)modelObject;
        if ( !medium.isUnknown() && medium.getCast() == null && medium.getInheritedCast() == null ) {
            Issue issue = makeIssue(Issue.VALIDITY, medium);
            issue.setDescription( "The transmission mode of communication medium \"" + medium.getName()
                    + "\" is undetermined (unicast is assumed).");
            issue.setRemediation( "Set the transmission mode to either Unicast, Multicast or Broadcast"
            + "\nor categorize \"" + medium.getName() + "\" with another medium for which the transmission mode is known");
            issue.setSeverity( Level.Medium );
            issues.add(issue);
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof TransmissionMedium;
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
    protected String getLabel() {
        return "Medium transmission mode is unknown";
    }
}
