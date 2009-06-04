package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;

import java.util.ArrayList;
import java.util.List;

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
    public boolean canBeWaived() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected String getLabel() {
        return "Communicating to self";
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
                Issue issue = makeIssue(Issue.STRUCTURAL, flow);
                issue.setDescription(source.getActor() + " is both the source and target.");
                issue.setRemediation(" Change either the source or target of this flow.");
                issue.setSeverity( Issue.Level.Major );
                issues.add(issue);
            } else {
                List<Actor> possibleSourceActors = getQueryService().findAllActors( source.resourceSpec() );
                List<Actor> possibleTargetActors = getQueryService().findAllActors( target.resourceSpec() );
                if ( possibleSourceActors.size() == 1
                        && possibleTargetActors.size() == 1
                        && possibleSourceActors.get(0) == possibleTargetActors.get(0)) {
                    Issue issue = makeIssue(Issue.STRUCTURAL, flow);
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
