/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.core.model.Classification;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.engine.query.QueryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Incorrect medium delegation.
 */
public class IncorrectMediumDelegation extends AbstractIssueDetector {

    public IncorrectMediumDelegation() {
    }

    @Override
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        TransmissionMedium medium = (TransmissionMedium) modelObject;
        Plan plan = queryService.getPlan();
        for ( TransmissionMedium delegate : medium.getEffectiveDelegatedToMedia() ) {
            if ( securityReduction( plan, medium, delegate ) ) {
                Issue issue = makeIssue( queryService, Issue.ROBUSTNESS, medium );
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
                Issue issue = makeIssue( queryService, Issue.VALIDITY, medium );
                issue.setDescription( medium.getEffectiveCast().name() + " \"" + medium.getName() + "\" delegates to "
                                      + delegate.getEffectiveCast().name() + " \"" + delegate.getName() + '\"'
                                      + " which is improper." );
                issue.setRemediation( "Do not delegate \"" + medium.getName() + "\" to \"" + delegate.getName() + '\"'
                                      + "\nor change the transmission mode of \"" + delegate.getName() + '\"'
                                      + "\nor change the transmission mode of \"" + medium.getName() + '\"' );
                issue.setSeverity( Level.Medium );
                issues.add( issue );
            }

            Place locale = plan.getLocale();
            if ( redundantDelegate( medium, delegate, locale ) ) {
                Issue issue = makeIssue( queryService, Issue.VALIDITY, medium );
                issue.setDescription( medium.getEffectiveCast().name() + " \"" + medium.getName() + "\" delegates to "
                                      + delegate.getEffectiveCast().name() + " \"" + delegate.getName() + '\"'
                                      + " which is redundant." );
                issue.setRemediation(
                        "Do not delegate \"" + medium.getName() + "\" to \"" + delegate.getName() + '\"' );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            }
            issues.addAll( subsumedDelegate( queryService, medium, delegate, locale ) );
        }
        return issues;
    }

    private static boolean securityReduction( Plan plan, TransmissionMedium medium, TransmissionMedium delegate ) {
        List<Classification> mediumSec = medium.getEffectiveSecurity( plan );
        return !mediumSec.isEmpty() && Classification.hasHigherClassification( mediumSec,
                                                                               delegate.getEffectiveSecurity( plan ),
                                                                               plan );
    }

    private static boolean improperMode( TransmissionMedium medium, TransmissionMedium delegate ) {
        return medium.getEffectiveCast().compareTo( delegate.getEffectiveCast() ) > 0;
    }

    private static boolean redundantDelegate( TransmissionMedium medium, TransmissionMedium delegate, Place locale ) {
        return medium.narrowsOrEquals( delegate, locale );
    }

    private List<Issue> subsumedDelegate( QueryService queryService, TransmissionMedium medium,
                                          TransmissionMedium delegate, Place locale ) {
        List<Issue> issues = new ArrayList<Issue>();
        List<TransmissionMedium> others = new ArrayList<TransmissionMedium>( medium.getDelegatedToMedia() );
        others.remove( delegate );
        others.addAll( medium.getInheritedDelegates() );
        for ( TransmissionMedium otherDelegate : others ) {
            if ( delegate.narrowsOrEquals( otherDelegate, locale ) ) {
                Issue issue = makeIssue( queryService, Issue.VALIDITY, medium );
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
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof TransmissionMedium;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Communication medium delegates to inappropriate medium";
    }
}
