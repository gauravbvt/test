/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Classification;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Incorrect medium delegation.
 */
public class IncorrectMediumDelegation extends AbstractIssueDetector {

    public IncorrectMediumDelegation() {
    }

    @Override
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getModelService();
        List<Issue> issues = new ArrayList<Issue>();
        TransmissionMedium medium = (TransmissionMedium) modelObject;
        CollaborationModel collaborationModel = queryService.getCollaborationModel();
        for ( TransmissionMedium delegate : medium.getEffectiveDelegatedToMedia() ) {
            if ( securityReduction( collaborationModel, medium, delegate ) ) {
                Issue issue = makeIssue( communityService, Issue.ROBUSTNESS, medium );
                issue.setDescription( '\"' + medium.getName() + "\" delegates to \"" + delegate.getName() + '\"'
                                      + " which is less secure." );
                issue.setRemediation( "Do not delegate \"" + medium.getName() + "\" to \"" + delegate.getName() + '\"'
                                      + "\nor raise the security of \"" + delegate.getName() + '\"'
                                      + "\nor lower the security of \"" + medium.getName() + '\"' );
                issue.setSeverity( Level.Medium );
                issues.add( issue );
            }
        }
        for ( TransmissionMedium delegate : medium.getDelegatedToMedia() ) {
            if ( improperMode( medium, delegate ) ) {
                Issue issue = makeIssue( communityService, Issue.VALIDITY, medium );
                issue.setDescription( medium.getEffectiveCast().name() + " \"" + medium.getName() + "\" delegates to "
                                      + delegate.getEffectiveCast().name() + " \"" + delegate.getName() + '\"'
                                      + " which is improper." );
                issue.setRemediation( "Do not delegate \"" + medium.getName() + "\" to \"" + delegate.getName() + '\"'
                                      + "\nor change the transmission mode of \"" + delegate.getName() + '\"'
                                      + "\nor change the transmission mode of \"" + medium.getName() + '\"' );
                issue.setSeverity( Level.Medium );
                issues.add( issue );
            }

            Place locale = queryService.getPlanLocale();
            if ( redundantDelegate( medium, delegate, locale ) ) {
                Issue issue = makeIssue( communityService, Issue.VALIDITY, medium );
                issue.setDescription( medium.getEffectiveCast().name() + " \"" + medium.getName() + "\" delegates to "
                                      + delegate.getEffectiveCast().name() + " \"" + delegate.getName() + '\"'
                                      + " which is redundant." );
                issue.setRemediation(
                        "Do not delegate \"" + medium.getName() + "\" to \"" + delegate.getName() + '\"' );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            }
            issues.addAll( subsumedDelegate( communityService, medium, delegate, locale ) );
        }
        return issues;
    }

    private static boolean securityReduction( CollaborationModel collaborationModel, TransmissionMedium medium, TransmissionMedium delegate ) {
        List<Classification> mediumSec = medium.getEffectiveSecurity( collaborationModel );
        return !mediumSec.isEmpty() && Classification.hasHigherClassification( mediumSec,
                                                                               delegate.getEffectiveSecurity( collaborationModel ),
                collaborationModel );
    }

    private static boolean improperMode( TransmissionMedium medium, TransmissionMedium delegate ) {
        return medium.getEffectiveCast().compareTo( delegate.getEffectiveCast() ) > 0;
    }

    private static boolean redundantDelegate( TransmissionMedium medium, TransmissionMedium delegate, Place locale ) {
        return medium.narrowsOrEquals( delegate, locale );
    }

    private List<Issue> subsumedDelegate( CommunityService communityService, TransmissionMedium medium,
                                          TransmissionMedium delegate, Place locale ) {
        List<Issue> issues = new ArrayList<Issue>();
        List<TransmissionMedium> others = new ArrayList<TransmissionMedium>( medium.getDelegatedToMedia() );
        others.remove( delegate );
        others.addAll( medium.getInheritedDelegates() );
        for ( TransmissionMedium otherDelegate : others ) {
            if ( delegate.narrowsOrEquals( otherDelegate, locale ) ) {
                Issue issue = makeIssue( communityService, Issue.VALIDITY, medium );
                issue.setDescription( "Delegated-to medium \"" + delegate.getName() + "\" is redundant"
                                      + " given other delegated-to medium \"" + otherDelegate.getName() + "\"." );
                issue.setRemediation( "Do not delegate to \"" + delegate.getName() + '\"' + "\nor do not delegate to \""
                                      + otherDelegate.getName() + '\"' );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            }
        }
        return issues;
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof TransmissionMedium;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Communication medium delegates transmissions to an inappropriate medium";
    }
}
