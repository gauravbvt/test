package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.QueryService;

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
    public boolean appliesTo( Identifiable modelObject ) {
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
    protected String getKindLabel() {
        return "Sharing with self";
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
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getModelService();
        List<Issue> issues = new ArrayList<Issue>();
        Flow flow = (Flow) modelObject;
        if ( flow.getSource().isPart() && flow.getTarget().isPart() ) {
            Part source = (Part) flow.getSource();
            Part target = (Part) flow.getTarget();
            if ( ModelObject.areIdentical( source.getActor(), target.getActor() ) ) {
                Issue issue = makeIssue( communityService, Issue.COMPLETENESS, flow );
                issue.setDescription( source.getActor() + " is both the source and target." );
                issue.setRemediation( " Change either the source\n or change target of this flow." );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            } else {
                List<Actor> possibleSourceActors = queryService.findAllActualActors( source.resourceSpec() );
                List<Actor> possibleTargetActors = queryService.findAllActualActors( target.resourceSpec() );
                if ( possibleSourceActors.size() == 1
                        && possibleTargetActors.size() == 1
                        && possibleSourceActors.get( 0 ) == possibleTargetActors.get( 0 ) ) {
                    Issue issue = makeIssue( communityService, Issue.VALIDITY, flow );
                    issue.setDescription( possibleSourceActors.get( 0 )
                            + " is both the only potential source and target." );
                    issue.setRemediation( "Change the source"
                            + "\nor change the target of this flow"
                            + "\nor generalize task specifications to assign multiple agents to either source or target task" );
                    issue.setSeverity( Level.Low );
                    issues.add( issue );

                }
            }
        }
        return issues;
    }

}
