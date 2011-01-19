package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.Delay;
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
 * Untimely sharing of required info.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 22, 2009
 * Time: 2:32:23 PM
 */
public class UntimelyNecessarySharing extends AbstractIssueDetector {

    public UntimelyNecessarySharing() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Flow flow = (Flow) modelObject;
        if ( flow.isSharing()
                && ( flow.isCritical() || flow.isTriggeringToTarget() || flow.isTerminatingToTarget() ) ) {
            Part target = (Part) flow.getTarget();
            List<Flow> criticalNeeds = getMatchingCriticalNeeds( target, flow );
            if ( !criticalNeeds.isEmpty() ) {
                Delay commitmentDelay = flow.getMaxDelay();
                for ( Flow criticalNeed : criticalNeeds ) {
                    if ( commitmentDelay.compareTo( criticalNeed.getMaxDelay() ) > 0 ) {
                        DetectedIssue issue = makeIssue( Issue.ROBUSTNESS, flow );
                        issue.setDescription(
                                "The needed information \""
                                        + criticalNeed.getName()
                                        + "\" is communicated at least "
                                        + commitmentDelay
                                        + " after it can be, which exceeds the required "
                                        + criticalNeed.getMaxDelay()
                        );
                        issue.setRemediation(
                                "Ease the time constraint for \""
                                        + criticalNeed.getReceiveTitle()
                                        + "\"\nor obtain a more timely sharing commitment for "
                                        + "\"" + criticalNeed.getName() + "\""
                        );
                        issue.setSeverity( getQueryService().computePartPriority( target ) );
                        issues.add( issue );
                    }
                }
            }
        }
        return issues;
    }

    @SuppressWarnings( "unchecked" )
    private List<Flow> getMatchingCriticalNeeds( Part target, final Flow sharing ) {
        return (List<Flow>) CollectionUtils.select(
                IteratorUtils.toList( target.receivesNamed( sharing.getName() ) ),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        Flow receive = (Flow) obj;
                        return receive.isNeed()
                                && receive.isAskedFor() == sharing.isAskedFor()
                                && receive.isCritical() == sharing.isCritical()
                                && receive.isTriggeringToTarget() == sharing.isTriggeringToTarget()
                                && receive.isTerminatingToTarget() == sharing.isTerminatingToTarget()
                                && ( receive.getEois().isEmpty()
                                || getQueryService().hasCommonEOIs( receive, sharing ) );
                    }
                }
        );
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
    public String getTestedProperty() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    protected String getLabel() {
        return "Untimely necessary sharing flow";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }


}
