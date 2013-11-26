package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.analysis.DetectedIssue;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.query.QueryService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Detects redundant flows.
 * Synonymous needs or capabilities of a part are redundant.
 * Synonymous sharings between same parts are redundant.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 13, 2009
 * Time: 11:23:29 AM
 */
public class RedundantFlow extends AbstractIssueDetector {

    public RedundantFlow() {
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
        return "Redundant sharing flow";
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
        Flow flow = (Flow) modelObject;
        Iterator<Flow> otherFlows;
        if ( flow.isNeed() ) {
            otherFlows = flow.getTarget().receives();
        } else {
            otherFlows = flow.getSource().sends();
        }
        boolean redundant = false;
        while ( !redundant && otherFlows.hasNext() ) {
            Flow otherFlow = otherFlows.next();
            redundant = ( otherFlow != flow ) && equivalent( flow, otherFlow );
        }
        if ( redundant ) {
            DetectedIssue issue = makeIssue( communityService, DetectedIssue.COMPLETENESS, flow );
            issue.setDescription( "This " + flowKind( flow ) + " is redundant." );
            issue.setRemediation( "Change the name of information transmitted\nor "
                    + ( flow.isSharing() ? "break up" : "remove" )
                    + " the " + flowKind( flow ) +  "." );
            issue.setSeverity( Level.Low );
            issues.add( issue );
        }
        return issues;
    }

    private String flowKind( Flow flow ) {
        if ( flow.isCapability() ) return "information capability";
        if ( flow.isNeed() ) return "information need";
        else return "sharing flow";
    }

    private boolean equivalent( Flow flow, Flow otherFlow ) {
        if ( flowKind( flow ).equals( flowKind( otherFlow ) ) ) {
            if ( Matcher.same( flow.getName(), otherFlow.getName() ) ) {
                if ( flow.isNeed() && otherFlow.isNeed() ) return true;
                if ( flow.isCapability() && otherFlow.isCapability() ) return true;
                if ( flow.getTarget().equals( otherFlow.getTarget() )
                        && flow.isAskedFor() == otherFlow.isAskedFor() ) return true;
            }
        }
        return false;
    }
}
