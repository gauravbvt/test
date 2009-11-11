package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.Delay;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.util.Matcher;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.collections.Transformer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A triggering sharing commitment does not meet timing requirements
 * set by a matching triggering notification need.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 21, 2009
 * Time: 3:47:25 PM
 */
public class UntimelyTriggeringSharing extends AbstractIssueDetector {

    public UntimelyTriggeringSharing() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Flow commitment = (Flow) modelObject;
        Part target = (Part) commitment.getTarget();
        List<Flow> triggeredNeeds = getMatchingTriggeredNeeds( target, commitment );
        if ( !triggeredNeeds.isEmpty() ) {
            Delay cumulativeDelay = getCumulativeDelay( commitment );
            for ( Flow triggeredNeed : triggeredNeeds ) {
                if ( cumulativeDelay.compareTo( triggeredNeed.getMaxDelay() ) > 0 ) {
                    DetectedIssue issue = makeIssue( Issue.ROBUSTNESS, commitment );
                    issue.setDescription(
                            "The needed information \""
                                    + triggeredNeed.getName()
                                    + "\" is communicated at least "
                                    + cumulativeDelay
                                    + " after it first becomes known, which exceeds the required "
                                    + triggeredNeed.getMaxDelay()
                    );
                    issue.setRemediation(
                            "Ease the timeliness constraint for \""
                                    + triggeredNeed.getRequirementTitle()
                                    + "\",\nor obtain more timely sharing commitments for "
                                    + "\"" + triggeredNeed.getName() + "\""
                    );
                    issue.setSeverity( getQueryService().getPartPriority( target ) );
                    issues.add( issue );
                }
            }
        }
        return issues;
    }

    private Delay getCumulativeDelay( Flow commitment ) {
        return getCumulativeDelay( commitment, new HashSet<Flow>() );
    }

    @SuppressWarnings( "unchecked" )
    private Delay getCumulativeDelay( Flow commitment, final Set<Flow> visited ) {
        if ( visited.contains( commitment ) ) return new Delay();
        visited.add( commitment );
        Delay cumulativeDelay = new Delay( commitment.getMaxDelay() );
        List<Flow> priorCommitments = (List<Flow>) CollectionUtils.select(
                IteratorUtils.toList( ( (Part) commitment.getSource() ).requirementsNamed( commitment.getName() ) ),
                PredicateUtils.invokerPredicate( "isSharingCommitment" )
        );
        List<Delay> alternateDelays = (List<Delay>) CollectionUtils.collect(
                priorCommitments,
                new Transformer() {
                    public Object transform( Object obj ) {
                        return getCumulativeDelay( (Flow) obj, visited );
                    }
                }
        );
        if ( !alternateDelays.isEmpty() ) {
            // Get smallest alternative delay.
            Collections.sort( alternateDelays );
            cumulativeDelay = cumulativeDelay.add( alternateDelays.get( 0 ) );
        }
        return cumulativeDelay;
    }

    @SuppressWarnings( "unchecked" )
    private List<Flow> getMatchingTriggeredNeeds( Part target, final Flow commitment ) {
        return (List<Flow>) CollectionUtils.select(
                IteratorUtils.toList( target.requirementsNamed( commitment.getName() ) ),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        Flow receive = (Flow) obj;
                        String eois = receive.getDescription();
                        return receive.isNeed()
                                && receive.isTriggeringToTarget()
                                && ( eois.isEmpty()
                                || Matcher.hasCommonEOIs(
                                receive,
                                commitment,
                                getQueryService() ) );
                    }
                }
        );
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Flow
                && ( (Flow) modelObject ).isSharing()
                && ( (Flow) modelObject ).isTriggeringToTarget();
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
        return "Untimely task-triggering notification";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
