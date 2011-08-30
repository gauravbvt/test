package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Classification;
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Information sharing flow contradicts related capability.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 7, 2010
 * Time: 1:00:07 PM
 */
public class SharingContradictsCapability extends AbstractIssueDetector {

    public SharingContradictsCapability() {
    }

    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Flow flow = (Flow) modelObject;
        if ( flow.isSharing() ) {
            Node source = flow.getSource();
            Iterator<Flow> sends = source.sends();
            while ( sends.hasNext() ) {
                Flow send = sends.next();
                if ( send.isCapability() && Matcher.same( send.getName(), flow.getName() ) ) {
                    List<String> mismatches = new ArrayList<String>();
                    findEOIMismatch( flow, send, mismatches );
                    findIntentMismatch( flow, send, mismatches );
                    findChannelsMismatch( flow, send, mismatches );
                    findDelayMismatch( flow, send, mismatches );
                    if ( !mismatches.isEmpty() ) {
                        Issue issue = makeIssue( Issue.VALIDITY, flow );
                        issue.setDescription( "The sharing flow contradicts an explicit capability as follows: "
                                + mismatchesToString( mismatches ) );
                        issue.setRemediation( "Modify the definition " +
                                "of the contradicted capability" +
                                "\nor modify the definition of this sharing flow." );
                        issue.setSeverity( computeSharingFailureSeverity( flow ));
                        issues.add( issue );
                    }
                }
            }
        }
        return issues;
    }

    private void findDelayMismatch( Flow sharing, Flow capability, List<String> mismatches ) {
        if ( sharing.getMaxDelay().compareTo( capability.getMaxDelay() ) > 0) {
            mismatches.add( "The maximum delay is more than expected.");
        }
    }

    private void findEOIMismatch( Flow sharing, Flow capability, List<String> mismatches ) {
        Plan plan = getPlan();
        for ( ElementOfInformation sharedEoi : sharing.getEois() ) {
            boolean matched = false;
            for ( ElementOfInformation offeredEoi : capability.getEois() ) {
                if ( Matcher.same( sharedEoi.getContent(), offeredEoi.getContent() ) ) {
                    matched = true;
                    if ( Classification.hasHigherClassification(
                            offeredEoi.getClassifications(),
                            sharedEoi.getClassifications(), plan ) ||
                            Classification.hasHigherClassification(
                                    sharedEoi.getClassifications(),
                                    offeredEoi.getClassifications(), plan ) ) {
                        mismatches.add( "\""
                                + sharedEoi.getContent()
                                + "\" has different secrecy classifications." );
                    }
                }
            }
            if ( !matched ) {
                mismatches.add( "\"" + sharedEoi.getContent() + "\" is unexpectedly shared." );
            }
        }
    }

    private void findIntentMismatch( Flow sharing, Flow capability, List<String> mismatches ) {
        if ( capability.getIntent() != null ) {
            if ( sharing.getIntent() != null && sharing.getIntent() != capability.getIntent() ) {
                mismatches.add( "The intent is unexpected." );
            }
        }
    }

    private void findChannelsMismatch( Flow sharing, Flow capability, List<String> mismatches ) {
        final Place locale = getPlan().getLocale();
        for ( final Channel sharingChannel : sharing.getEffectiveChannels() ) {
            boolean matched = CollectionUtils.exists(
                    capability.getEffectiveChannels(),
                    new Predicate() {
                        public boolean evaluate( Object object ) {
                            return sharingChannel.getMedium().narrowsOrEquals(
                                    ( (Channel) object ).getMedium(),
                                    locale );
                        }
                    }
            );
            if ( !matched ) {
                mismatches.add( "Sharing over unexpected channel \"" + sharingChannel + "\"." );
            }
        }
    }

    private String mismatchesToString( List<String> mismatches ) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> iter = mismatches.iterator();
        while ( iter.hasNext() ) {
            sb.append( iter.next() );
            if ( iter.hasNext() ) sb.append( ' ' );
        }
        return sb.toString();
    }

    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Flow;
    }

    public String getTestedProperty() {
        return null;
    }

    protected String getKindLabel() {
        return "Sharing contradicts explicit capability";
    }
}
