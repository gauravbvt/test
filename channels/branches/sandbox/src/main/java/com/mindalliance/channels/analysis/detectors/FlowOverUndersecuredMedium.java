package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Classification;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.TransmissionMedium;
import com.mindalliance.channels.dao.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Fow over under-secured medium.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 7, 2010
 * Time: 2:20:48 PM
 */
public class FlowOverUndersecuredMedium extends AbstractIssueDetector {

    public FlowOverUndersecuredMedium() {
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
    public List<Issue> detectIssues( ModelObject modelObject ) {
        Flow flow = (Flow) modelObject;
        List<Issue> issues = new ArrayList<Issue>();
        List<Classification> eoiClassifications = flow.getClassifications();
        if ( !eoiClassifications.isEmpty() ) {
            for ( Channel channel : flow.getEffectiveChannels() ) {
                TransmissionMedium medium = channel.getMedium();
                // under-secured immediate medium
                List<Classification> mediumClassifications = medium.getEffectiveSecurity();
                if ( !Classification.hasHigherOrEqualClassification(
                        mediumClassifications,
                        eoiClassifications ) ) {
                    Issue issue = makeIssue( Issue.ROBUSTNESS, flow );
                    issue.setDescription( "Classified information would be communicated "
                            + "over unsecured or insufficiently secured channel \""
                            + channel + "\"." );
                    issue.setRemediation( "Remove \"" + channel + "\" as an alternative"
                            + "\nor change the security profile of medium \"" + channel.getMedium().getName() + "\""
                            + "\nor change the secrecy classification of the problematic elements of information"
                            + "\nor remove the classified elements(s) of information that exceed the channel's security" );
                    issue.setSeverity( Level.Medium );
                    issues.add( issue );
                }
                // under-secured delegated medium
                List<TransmissionMedium> delegates = medium.getEffectiveDelegates( User.current().getPlan() );
                for ( TransmissionMedium delegate : delegates ) {
                    List<Classification> delegateClassifications = medium.getEffectiveSecurity();
                    if ( !Classification.hasHigherOrEqualClassification(
                            delegateClassifications,
                            eoiClassifications ) ) {
                        Issue issue = makeIssue( Issue.ROBUSTNESS, flow );
                        issue.setDescription( "Classified information could be communicated "
                                + "over unsecured or insufficiently secured delegated channel \""
                                + delegate + "\"" );
                        issue.setRemediation( "Remove \"" + channel + "\" as an alternative"
                                + "\nor change the security profile of delegated medium \"" + delegate.getName() + "\""
                                + "\nor change the secrecy classification of the problematic elements of information"
                                + "\nor remove the classified elements(s) of information that exceed the delegated medium's security" );
                        issue.setSeverity( Level.Medium );
                        issues.add( issue );
                    }
                }
            }
        }
        return issues;
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
        return "Flow over unsecure medium";
    }
}
