package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 21, 2010
 * Time: 11:55:44 AM
 */
public class ImportantTaskNotOperational extends AbstractIssueDetector {

    public ImportantTaskNotOperational() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        if ( !part.isOperational() ) {
            Level importance = getTaskFailureSeverity( part );
            if ( importance.compareTo( Level.Low ) >= 1 ) {
                Issue issue = makeIssue( Issue.ROBUSTNESS, part );
                issue.setDescription( "Task \""
                        + part.getTitle()
                        + "\" is important but is not operational." );
                issue.setSeverity( importance );
                issue.setRemediation( "Make the task operational." );
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
    protected String getLabel() {
        return "Important task is not operational";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
