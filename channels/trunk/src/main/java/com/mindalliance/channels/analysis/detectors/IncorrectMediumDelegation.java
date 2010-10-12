package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Classification;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.TransmissionMedium;
import com.mindalliance.channels.model.Place;

import java.util.ArrayList;
import java.util.List;

/**
 * Incorrect medium delegation.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 7, 2010
 * Time: 4:04:28 PM
 */
public class IncorrectMediumDelegation extends AbstractIssueDetector {

    public IncorrectMediumDelegation() {
    }

    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        TransmissionMedium medium = (TransmissionMedium) modelObject;
        for ( TransmissionMedium delegate : medium.getEffectiveDelegatedToMedia() ) {
            if ( securityReduction( medium, delegate ) ) {
                Issue issue = makeIssue( Issue.ROBUSTNESS, medium );
                issue.setDescription( "\"" + medium.getName()
                        + "\" delegates to \"" + delegate.getName() + "\""
                        + " which is less secure." );
                issue.setRemediation( "Do not delegate \"" + medium.getName()
                        + "\" to \"" + delegate.getName() + "\""
                        + "\nor raise the security of \"" + delegate.getName() + "\""
                        + "\nor lower the security of \"" + medium.getName() + "\"" );
                issue.setSeverity( Level.Medium );
                issues.add( issue );
            }
        }
        for ( TransmissionMedium delegate : medium.getDelegatedToMedia() ) {
            if ( improperMode( medium, delegate ) ) {
                Issue issue = makeIssue( Issue.VALIDITY, medium );
                issue.setDescription( medium.getEffectiveCast().name() + " \"" + medium.getName()
                        + "\" delegates to " + delegate.getEffectiveCast().name() + " \"" + delegate.getName() + "\""
                        + " which is improper." );
                issue.setRemediation( "Do not delegate \"" + medium.getName()
                        + "\" to \"" + delegate.getName() + "\""
                        + "\nor change the transmission mode of \"" + delegate.getName() + "\""
                        + "\nor change the transmission mode of \"" + medium.getName() + "\"" );
                issue.setSeverity( Level.Medium );
                issues.add( issue );
            }

            if ( redundantDelegate( medium, delegate ) ) {
                Issue issue = makeIssue( Issue.VALIDITY, medium );
                issue.setDescription( medium.getEffectiveCast().name() + " \"" + medium.getName()
                        + "\" delegates to " + delegate.getEffectiveCast().name() + " \"" + delegate.getName() + "\""
                        + " which is redundant." );
                issue.setRemediation( "Do not delegate \"" + medium.getName()
                        + "\" to \"" + delegate.getName() + "\"" );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            }
            issues.addAll( subsumedDelegate( medium, delegate ) );
        }
        return issues;
    }

    private boolean securityReduction( TransmissionMedium medium, TransmissionMedium delegate ) {
        List<Classification> mediumSec = medium.getEffectiveSecurity();
        return !mediumSec.isEmpty()
                &&
                Classification.hasHigherOrEqualClassification(
                        mediumSec,
                        delegate.getEffectiveSecurity()
                );
    }

    private boolean improperMode( TransmissionMedium medium, TransmissionMedium delegate ) {
        return medium.getEffectiveCast().compareTo( delegate.getEffectiveCast() ) > 0;
    }

    private boolean redundantDelegate( TransmissionMedium medium, TransmissionMedium delegate ) {
        return medium.narrowsOrEquals( delegate, getPlan().getLocale() );
    }

    private List<Issue> subsumedDelegate( TransmissionMedium medium, TransmissionMedium delegate ) {
        List<Issue> issues = new ArrayList<Issue>();
        List<TransmissionMedium> others = new ArrayList<TransmissionMedium>( medium.getDelegatedToMedia() );
        others.remove( delegate );
        others.addAll( medium.getInheritedDelegates() );
        Place locale = getPlan().getLocale();
        for ( TransmissionMedium otherDelegate : others ) {
            if ( delegate.narrowsOrEquals( otherDelegate, locale ) ) {
                Issue issue = makeIssue( Issue.VALIDITY, medium );
                issue.setDescription( "Delegated-to medium \"" + delegate.getName() + "\" is redundant"
                        + " given other delegated-to medium \"" + otherDelegate.getName() + "\"." );
                issue.setRemediation( "Do not delegate to \"" + delegate.getName() + "\""
                        + "\nor do not delegate to \"" + otherDelegate.getName() + "\"" );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            }
        }
        return issues;
    }

    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof TransmissionMedium;
    }

    public String getTestedProperty() {
        return null;
    }

    protected String getLabel() {
        return "Communication medium delegates to inappropriate medium";
    }
}
