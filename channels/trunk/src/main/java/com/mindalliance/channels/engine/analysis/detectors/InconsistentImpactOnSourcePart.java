package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.model.Flow;
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
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Flow commitment = (Flow) modelObject;
        for ( Flow similar : getSimilarFlows( commitment ) ) {
            if ( similar.isTriggeringToSource() != commitment.isTriggeringToSource() ) {
                DetectedIssue issue = makeIssue( queryService, Issue.VALIDITY, commitment );
                issue.setDescription( "'"
                        + commitment.getReceiveTitle()
                        + "' triggers '"
                        + commitment.getSource().getTitle()
                        + "' but '"
                        + similar.getReceiveTitle()
                        + "' does not." );
                issue.setRemediation( "Have both or neither trigger the task that produces the information." );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            } else if ( similar.isTerminatingToSource() != commitment.isTerminatingToSource() ) {
                DetectedIssue issue = makeIssue( queryService, Issue.VALIDITY, commitment );
                issue.setDescription( "'"
                        + commitment.getReceiveTitle()
                        + "' terminates '"
                        + commitment.getSource().getTitle()
                        + "' but '"
                        + similar.getReceiveTitle()
                        + "' does not." );
                issue.setRemediation( "Have both terminate the task that produces the information\nor have neither do it." );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            }
        }
        return issues;
    }

    @SuppressWarnings( "unchecked" )
    private List<Flow> getSimilarFlows( final Flow commitment ) {
        Part source = (Part) commitment.getSource();
        return (List<Flow>) CollectionUtils.select(
                IteratorUtils.toList( source.sendsNamed( commitment.getName() ) ),
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
    protected String getKindLabel() {
        return "Impacts on a source task of sharing are mutually inconsistent";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }

}
