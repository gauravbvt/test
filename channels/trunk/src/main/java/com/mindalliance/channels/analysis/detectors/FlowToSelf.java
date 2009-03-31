package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Actor;

import java.util.List;
import java.util.ArrayList;

/**
 * A flow that is or can only be from one actor to itself.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 30, 2009
 * Time: 9:37:50 AM
 */
public class FlowToSelf extends AbstractIssueDetector {

    public FlowToSelf() {
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
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Flow flow = (Flow) modelObject;
        if ( flow.getSource().isPart() && flow.getTarget().isPart() ) {
            Part source = (Part)flow.getSource();
            Part target = (Part)flow.getTarget();
            if (source.getActor() == target.getActor() && source.getActor() != null ) {
                Issue issue = new DetectedIssue(Issue.STRUCTURAL, flow);
                issue.setDescription(source.getActor() + " is both the source and target.");
                issue.setRemediation(" Change either the source or target of this flow.");
                issue.setSeverity( Issue.Level.Major );
                issues.add(issue);
            } else {
                List<Actor> possibleSourceActors = getDqo().findAllActors( source.resourceSpec() );
                List<Actor> possibleTargetActors = getDqo().findAllActors( target.resourceSpec() );
                if ( possibleSourceActors.size() == 1
                        && possibleTargetActors.size() == 1
                        && possibleSourceActors.get(0) == possibleTargetActors.get(0)) {
                    Issue issue = new DetectedIssue(Issue.STRUCTURAL, flow);
                    issue.setDescription(possibleSourceActors.get(0)
                            + " is both the only potential source and target.");
                    issue.setRemediation(" If this is not intentional, change either the source or target of this flow, "
                            + "or identify more actors that could be either source or target");
                    issue.setSeverity( Issue.Level.Minor );
                    issues.add(issue);

                }
            }
        }
        return issues;
    }

}
