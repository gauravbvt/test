package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.nlp.Matcher;

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
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Flow sharing = (Flow) modelObject;
        if ( !sharing.isReferencesEventPhase() ) {
            Matcher matcher = Matcher.getInstance();
            Place locale = getPlan().getLocale();
            for ( Flow other : getQueryService().findAllFlows() ) {
                if ( other.isSharing()
                        && sharing.isAskedFor() == other.isAskedFor()
                        && matcher.same( sharing.getName(), other.getName() )
                        && !sharing.getSegment().sameAs( other.getSegment() ) ) {
                    Part initiatedPart = (Part) ( sharing.isAskedFor() ? sharing.getSource() : sharing.getTarget() );
                    Part otherInitiatedPart = (Part) ( other.isAskedFor() ? other.getSource() : other.getTarget() );
                    ResourceSpec receiver = initiatedPart.resourceSpec();
                    ResourceSpec otherReceiver = otherInitiatedPart.resourceSpec();
                    if ( receiver.narrowsOrEquals( otherReceiver, locale )
                            || otherReceiver.narrowsOrEquals( receiver, locale ) ) {
                        Issue issue = makeIssue( Issue.ROBUSTNESS, sharing );
                        issue.setDescription( "No reference is made " +
                                "regarding the event context " +
                                "making the communication ambiguous with " +
                                "other same-named communications in other event contexts." );
                        issue.setRemediation( "Require that the information sharing reference the event context" +
                                "\nor rename the sharing flow." );
                        issue.setSeverity( getSharingFailureSeverity( sharing ) );
                        issues.add( issue );
                    }
                }
            }
        }
        return issues;
    }

    @Override
    public boolean appliesTo( ModelObject modelObject ) {
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
