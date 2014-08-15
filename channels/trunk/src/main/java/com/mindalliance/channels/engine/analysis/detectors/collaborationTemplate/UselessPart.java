package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.analysis.DetectedIssue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A part that has flows but serves no useful purpose.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 12, 2009
 * Time: 7:21:57 PM
 */
public class UselessPart extends AbstractIssueDetector {

    public UselessPart() {
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
        return "Task does not directly or indirectly achieve any goal";
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( Identifiable modelObject ) {
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
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        if ( !part.getAllSharingReceives().isEmpty() || !part.getAllSharingSends().isEmpty() ) {
            if ( !isUseful( part, new HashSet<Part>() ) ) {
                DetectedIssue issue = makeIssue( communityService, DetectedIssue.COMPLETENESS, part );
                issue.setDescription( "The task is not useful: it achieves no goal, "
                        + "and it does not trigger nor send information to a useful task." );
                issue.setRemediation( "Have the task achieve a goal, end the event phase (if it would end a risk)\n" 
                        + "or make sure at least one task that it"
                        + " directly or indirectly triggers does achieve a goal." );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            }
        }
        return issues;
    }

    // A part is useful if it terminates an event, mitigates a risk or sends info to a useful part.
    private boolean isUseful( Part part, Set<Part> visited ) {
        if ( part.isTerminatesEventPhase() || !part.getGoals().isEmpty() ) return true;
        Iterator<Flow> sends = part.getAllSharingSends().iterator();
        boolean useful = false;
        while ( !useful && sends.hasNext() ) {
            Flow send = sends.next();
            Node node = send.getTarget();
            if ( node.isPart() ) {
                Part target = (Part) node;
                if ( !visited.contains( target ) ) {
                    visited.add( target );
                    useful = isUseful( target, visited );
                }
            }
        }
        return useful;
    }
}

