package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * A sharing flow is synonymous with another in a different context and the context is not referenced.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/14/11
 * Time: 10:49 AM
 */
public class AmbiguousSharingFlow extends AbstractIssueDetector {

    public AmbiguousSharingFlow() {
    }

    @Override
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getPlanService();
        List<Issue> issues = new ArrayList<Issue>();
        Flow sharing = (Flow) modelObject;
        if ( !sharing.isReferencesEventPhase() ) {
            Place locale = queryService.getPlanLocale();
            for ( Flow other : queryService.findAllFlows() ) {
                if ( other.isSharing()
                        && sharing.isAskedFor() == other.isAskedFor()
                        && Matcher.same( sharing.getName(), other.getName() )
                        && !sharing.getSegment().sameAs( other.getSegment() ) ) {
                    Part initiatedPart = (Part) ( sharing.isAskedFor() ? sharing.getSource() : sharing.getTarget() );
                    Part otherInitiatedPart = (Part) ( other.isAskedFor() ? other.getSource() : other.getTarget() );
                    ResourceSpec receiver = initiatedPart.resourceSpec();
                    ResourceSpec otherReceiver = otherInitiatedPart.resourceSpec();
                    if ( receiver.narrowsOrEquals( otherReceiver, locale )
                            || otherReceiver.narrowsOrEquals( receiver, locale ) ) {
                        Issue issue = makeIssue( communityService, Issue.ROBUSTNESS, sharing );
                        issue.setDescription( "No reference is made " +
                                "regarding the event context " +
                                "making the communication ambiguous with " +
                                "other same-named communications in other event contexts." );
                        issue.setRemediation( "Require that the information sharing reference the event context" +
                                "\nor rename the sharing flow." );
                        issue.setSeverity( computeSharingFailureSeverity( queryService, sharing ) );
                        issues.add( issue );
                    }
                }
            }
        }
        return issues;
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Flow && ( (Flow) modelObject ).isSharing();
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Ambiguous information sharing";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
