/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Classification;
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Information sharing flow contradicts related capability.
 */
public class SharingContradictsCapability extends AbstractIssueDetector {

    public SharingContradictsCapability() {
    }

    @Override
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getPlanService();
        List<Issue> issues = new ArrayList<Issue>();
        Flow flow = (Flow) modelObject;
        if ( flow.isSharing() ) {
            Node source = flow.getSource();
            Iterator<Flow> sends = source.sends();
            while ( sends.hasNext() ) {
                Flow send = sends.next();
                if ( send.isCapability() && Matcher.same( send.getName(), flow.getName() ) ) {
                    List<String> mismatches = new ArrayList<String>();
                    Plan plan = queryService.getPlan();
                    findEOIMismatch( plan, flow, send, mismatches );
                    findIntentMismatch( flow, send, mismatches );
                    findChannelsMismatch( flow, send, mismatches, queryService.getPlanLocale() );
                    findDelayMismatch( flow, send, mismatches );
                    if ( !mismatches.isEmpty() ) {
                        Issue issue = makeIssue( communityService, Issue.VALIDITY, flow );
                        issue.setDescription(
                                "The sharing flow contradicts an explicit capability as follows: " + mismatchesToString(
                                        mismatches ) );
                        issue.setRemediation( "Modify the definition " + "of the contradicted capability"
                                + "\nor modify the definition of this sharing flow." );
                        issue.setSeverity( computeSharingFailureSeverity( queryService, flow ) );
                        issues.add( issue );
                    }
                }
            }
        }
        return issues;
    }

    private static void findDelayMismatch( Flow sharing, Flow capability, List<String> mismatches ) {
        if ( sharing.getMaxDelay().compareTo( capability.getMaxDelay() ) > 0 ) {
            mismatches.add( "The maximum delay is more than expected." );
        }
    }

    private static void findEOIMismatch( Plan plan, Flow sharing, Flow capability, List<String> mismatches ) {
        for ( ElementOfInformation sharedEoi : sharing.getEffectiveEois() ) {
            boolean matched = false;
            for ( ElementOfInformation offeredEoi : capability.getEffectiveEois() ) {
                if ( Matcher.same( sharedEoi.getContent(), offeredEoi.getContent() ) ) {
                    matched = true;
                    if ( Classification.hasHigherClassification( offeredEoi.getClassifications(),
                            sharedEoi.getClassifications(), plan )
                            || Classification.hasHigherClassification( sharedEoi.getClassifications(),
                            offeredEoi.getClassifications(), plan ) ) {
                        mismatches.add( '\"' + sharedEoi.getContent() + "\" has different secrecy classifications." );
                    }
                }
            }
            if ( !matched ) {
                mismatches.add( '\"' + sharedEoi.getContent() + "\" is unexpectedly shared." );
            }
        }
    }

    private static void findIntentMismatch( Flow sharing, Flow capability, List<String> mismatches ) {
        if ( capability.getIntent() != null ) {
            if ( sharing.getIntent() != null && sharing.getIntent() != capability.getIntent() ) {
                mismatches.add( "The intent is unexpected." );
            }
        }
    }

    private static void findChannelsMismatch( Flow sharing, Flow capability, List<String> mismatches,
                                              final Place locale ) {
        List<Channel> capabilityChannels = capability.getEffectiveChannels();
        if ( !capabilityChannels.isEmpty() ) { // capability specifies channels, else any channel is fine
            for ( final Channel sharingChannel : sharing.getEffectiveChannels() ) {
                boolean matched = CollectionUtils.exists( capabilityChannels, new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return sharingChannel.getMedium().narrowsOrEquals( ( (Channel) object ).getMedium(), locale );
                    }
                } );
                if ( !matched )
                    mismatches.add( "Sharing over unexpected channel \"" + sharingChannel + "\"." );
            }
        }
    }

    private static String mismatchesToString( List<String> mismatches ) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> iter = mismatches.iterator();
        while ( iter.hasNext() ) {
            sb.append( iter.next() );
            if ( iter.hasNext() )
                sb.append( ' ' );
        }
        return sb.toString();
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Flow;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Information sharing inconsistent with definition of related capability";
    }
}
