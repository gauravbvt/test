package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Classification;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

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

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Flow;
    }

    @Override
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getModelService();
        Flow flow = (Flow) modelObject;
        List<Issue> issues = new ArrayList<Issue>();
        List<Classification> eoiClassifications = flow.getClassifications();
        if ( !eoiClassifications.isEmpty() ) {
            for ( Channel channel : flow.getEffectiveChannels() ) {
                TransmissionMedium medium = channel.getMedium();
                CollaborationModel collaborationModel = queryService.getCollaborationModel();
                if ( !medium.isDirect() ) {
                    // under-secured immediate medium
                    List<Classification> mediumClassifications = medium.getEffectiveSecurity( collaborationModel );
                    if ( !Classification.encompass(
                            mediumClassifications,
                            eoiClassifications, collaborationModel ) ) {
                        Issue issue = makeIssue( communityService, Issue.ROBUSTNESS, flow );
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
                }
                // under-secured delegated medium
                List<TransmissionMedium> delegates = medium.getEffectiveDelegates( queryService.getPlanLocale() );
                for ( TransmissionMedium delegate : delegates ) {
                    if ( !medium.isDirect() ) {
                        List<Classification> delegateClassifications = medium.getEffectiveSecurity( collaborationModel );
                        if ( !Classification.encompass(
                                delegateClassifications,
                                eoiClassifications, collaborationModel ) ) {
                            Issue issue = makeIssue( communityService, Issue.ROBUSTNESS, flow );
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
        }
        return issues;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Information shared over an insufficiently secure medium";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
