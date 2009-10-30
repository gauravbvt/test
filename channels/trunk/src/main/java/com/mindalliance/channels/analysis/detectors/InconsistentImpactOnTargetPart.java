package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * A sharing commitment's impact (triggering, terminating, critical)
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
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Flow commitment = (Flow) modelObject;
        for ( Flow similar : getSimilarFlows( commitment ) ) {
            if ( similar.isCritical() != commitment.isCritical() ) {
                DetectedIssue issue = makeIssue( Issue.VALIDITY, commitment );
                issue.setDescription( "'"
                        + commitment.getRequirementTitle()
                        + "' is critical but '"
                        + similar.getRequirementTitle()
                        + "' is not." );
                issue.setRemediation( "Make both or neither critical." );
                issue.setSeverity( Issue.Level.Minor );
                issues.add( issue );
            } else if ( similar.isTriggeringToTarget() != commitment.isTriggeringToTarget() ) {
                DetectedIssue issue = makeIssue( Issue.VALIDITY, commitment );
                issue.setDescription( "'"
                        + commitment.getRequirementTitle()
                        + "' triggers '"
                        + commitment.getTarget().getTitle()
                        + "' but '"
                        + similar.getRequirementTitle()
                        + "' does not." );
                issue.setRemediation( "Have both trigger the task that consumes the information\nor have neither do it." );
                issue.setSeverity( Issue.Level.Minor );
                issues.add( issue );
            } else if ( similar.isTerminatingToTarget() != commitment.isTerminatingToTarget() ) {
                DetectedIssue issue = makeIssue( Issue.VALIDITY, commitment );
                issue.setDescription( "'"
                        + commitment.getRequirementTitle()
                        + "' terminates '"
                        + commitment.getTarget().getTitle()
                        + "' but '"
                        + similar.getRequirementTitle()
                        + "' does not." );
                issue.setRemediation( "Have both terminate the task that consumes the information\nor have neither." );
                issue.setSeverity( Issue.Level.Minor );
                issues.add( issue );
            }
        }
        return issues;
    }

    @SuppressWarnings( "unchecked" )
    private List<Flow> getSimilarFlows( final Flow commitment ) {
        Part target = (Part) commitment.getTarget();
        return (List<Flow>) CollectionUtils.select(
                IteratorUtils.toList( target.requirementsNamed( commitment.getName() ) ),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return !obj.equals( commitment )
                                && ((Flow)obj).isAskedFor() == commitment.isAskedFor();
                    }
                }
        );
    }


    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
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
    protected String getLabel() {
        return "Inconsistent impact on target task";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
