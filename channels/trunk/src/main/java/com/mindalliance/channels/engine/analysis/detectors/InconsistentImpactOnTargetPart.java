package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.analysis.DetectedIssue;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * A communication commitment's impact (triggering, terminating, critical)
 * on a target part is different from that of a similar need.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 22, 2009
 * Time: 3:00:40 PM
 */
public class InconsistentImpactOnTargetPart extends AbstractIssueDetector {

    public InconsistentImpactOnTargetPart() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Flow flow = (Flow) modelObject;
        for ( Flow similar : getSimilarFlows( flow ) ) {
            if ( similar.isCritical() != flow.isCritical() ) {
                DetectedIssue issue = makeIssue( communityService, Issue.VALIDITY, flow );
                issue.setDescription( "Inconsistent impacts: '"
                        + flow.getReceiveTitle()
                        + "' is critical but '"
                        + similar.getReceiveTitle()
                        + "' is not." );
                issue.setRemediation( "Make both or neither critical." );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            } else if ( similar.isTriggeringToTarget() != flow.isTriggeringToTarget() ) {
                DetectedIssue issue = makeIssue( communityService, Issue.VALIDITY, flow );
                issue.setDescription( "Inconsistent impacts: '"
                        + flow.getReceiveTitle()
                        + "' triggers '"
                        + flow.getTarget().getTitle()
                        + "' but '"
                        + similar.getReceiveTitle()
                        + "' does not." );
                issue.setRemediation( "Have both trigger the task that consumes the information\nor have neither do it." );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            } else if ( similar.isTerminatingToTarget() != flow.isTerminatingToTarget() ) {
                DetectedIssue issue = makeIssue( communityService, Issue.VALIDITY, flow );
                issue.setDescription( "Inconsistent impacts: '"
                        + flow.getReceiveTitle()
                        + "' terminates '"
                        + flow.getTarget().getTitle()
                        + "' but '"
                        + similar.getReceiveTitle()
                        + "' does not." );
                issue.setRemediation( "Have both terminate the task that consumes the information\nor have neither." );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            }
        }
        return issues;
    }

    @SuppressWarnings( "unchecked" )
    private List<Flow> getSimilarFlows( final Flow flow ) {
        Part target = (Part) flow.getTarget();
        return (List<Flow>) CollectionUtils.select(
                IteratorUtils.toList( target.receivesNamed( flow.getName() ) ),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return !obj.equals( flow )
                                && ((Flow)obj).isAskedFor() == flow.isAskedFor();
                    }
                }
        );
    }


    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Flow && ( (Flow) modelObject ).isSharing();
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
        return "Impacts on a target task of sharing are mutually inconsistent";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
