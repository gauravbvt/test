package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;

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
    protected String getLabel() {
        return "Useless task";
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
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        if ( part.requirements().hasNext() || part.outcomes().hasNext() ) {
            if ( !isUseful( part, new HashSet<Part>() ) ) {
                DetectedIssue issue = makeIssue( DetectedIssue.STRUCTURAL, part );
                issue.setDescription( "Not useful: it neither ends or mitigates a risk, "
                        + "nor does it trigger or send information to a useful task." );
                issue.setRemediation( "Have the task mitigate a risk, or make sure at least one task that it"
                        + " directly or indirectly impacts does address a risk." );
                issue.setSeverity( Issue.Level.Minor );
                issues.add( issue );
            }
        }
        return issues;
    }

    // A part is useful if it terminates an event, mitigates a risk or sends info to a useful part.
    private boolean isUseful( Part part, Set<Part> visited ) {
        if ( part.isTerminatesEvent() || !part.getMitigations().isEmpty() ) return true;
        Iterator<Flow> outcomes = part.outcomes();
        boolean useful = false;
        while ( !useful && outcomes.hasNext() ) {
            Flow send = outcomes.next();
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

