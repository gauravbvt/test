package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Severity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * A sharing commitment's impact (triggering, terminating, critical)
 * on a source part is different from that of a similar flow.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 22, 2009
 * Time: 3:30:46 PM
 */
public class InconsistentImpactOnSourcePart extends AbstractIssueDetector {

    public InconsistentImpactOnSourcePart() {
    }


    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Flow commitment = (Flow) modelObject;
        for ( Flow similar : getSimilarFlows( commitment ) ) {
            if ( similar.isTriggeringToSource() != commitment.isTriggeringToSource() ) {
                DetectedIssue issue = makeIssue( Issue.VALIDITY, commitment );
                issue.setDescription( "'"
                        + commitment.getRequirementTitle()
                        + "' triggers '"
                        + commitment.getSource().getTitle()
                        + "' but '"
                        + similar.getRequirementTitle()
                        + "' does not." );
                issue.setRemediation( "Have both or neither trigger the task that produces the information." );
                issue.setSeverity( Severity.Minor );
                issues.add( issue );
            } else if ( similar.isTerminatingToSource() != commitment.isTerminatingToSource() ) {
                DetectedIssue issue = makeIssue( Issue.VALIDITY, commitment );
                issue.setDescription( "'"
                        + commitment.getRequirementTitle()
                        + "' terminates '"
                        + commitment.getSource().getTitle()
                        + "' but '"
                        + similar.getRequirementTitle()
                        + "' does not." );
                issue.setRemediation( "Have both terminate the task that produces the information\nor have neither do it." );
                issue.setSeverity( Severity.Minor );
                issues.add( issue );
            }
        }
        return issues;
    }

    @SuppressWarnings( "unchecked" )
    private List<Flow> getSimilarFlows( final Flow commitment ) {
        Part source = (Part) commitment.getSource();
        return (List<Flow>) CollectionUtils.select(
                IteratorUtils.toList( source.outcomesNamed( commitment.getName() ) ),
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
        return "Inconsistent impact on source task";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }

}
