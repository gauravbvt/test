package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

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
            Level importance = computeTaskFailureSeverity( part );
            if ( importance.compareTo( Level.Low ) >= 1 ) {
                Issue issue = makeIssue( Issue.ROBUSTNESS, part );
                issue.setDescription( "Task \""
                        + part.getTitle()
                        + "\" is important but is conceptual." );
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
    protected String getKindLabel() {
        return "Important task is not operational";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
