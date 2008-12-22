package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Actor;
import com.mindalliance.channels.util.SemMatch;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.collections.iterators.FilterIterator;
import org.apache.commons.collections.Predicate;

/**
 * Detects whether a critical requirement from an actor (singleton source) has no alternate source.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 19, 2008
 * Time: 4:35:27 PM
 */
public class NoRedundancy extends AbstractIssueDetector {

    public NoRedundancy() { }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Part;
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
    @SuppressWarnings( "unchecked" )
    protected List<Issue> doDetectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        final Part part = (Part) modelObject;
        // Find all critical sourced requirements of a part
        Iterator<Flow> criticalRequirements = new FilterIterator( part.requirements(),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        Flow requirement = (Flow) obj;
                        return requirement.isCritical()
                                && !requirement.getName().isEmpty()
                                && requirement.getSource().isPart()
                                && ( (Part) requirement.getSource() ).getActor() != null;
                    }
                } );
        // Find critical requirements without alternate sources
        while ( criticalRequirements.hasNext() ) {
            final Flow criticalRequirement = criticalRequirements.next();
            final Actor sourceActor = ( (Part) criticalRequirement.getSource() ).getActor();
            final String name = criticalRequirement.getName();
            // Get all differently sourced requirements for same info
            // by the part or other "matching" parts
            Iterator<Flow> alternates = new FilterIterator( part.getScenario().flows(),
                    new Predicate() {
                        public boolean evaluate( Object obj ) {
                            Flow otherFlow = (Flow) obj;
                            return otherFlow != criticalRequirement
                                    && SemMatch.same( otherFlow.getName(), name )
                                    && otherFlow.getTarget().isPart()
                                    && partsMatch( (Part) otherFlow.getTarget(), part )
                                    && otherFlow.getSource().isPart()
                                    && ( (Part) otherFlow.getSource() ).getActor() != sourceActor;
                        }
                    } );
            if ( !alternates.hasNext() ) {
                Issue issue = new Issue( Issue.STRUCTURAL, part );
                issue.setDescription( "Has a only one source of critical information -"
                        + name + "-" );
                issue.setRemediation( "Add alternate source." );
                issues.add( issue );
            }
        }
        return issues;
    }

    // Same actors if given, else same roles
    private boolean partsMatch( Part part, Part otherPart ) {
        if ( part.getActor() != null && otherPart.getActor() != null ) {
            return SemMatch.same( part.getActor().getName(), otherPart.getActor().getName() );
        } else if ( part.getRole() != null && otherPart.getRole() != null ) {
            return SemMatch.same( part.getRole().getName(), otherPart.getActor().getName() );
        } else return false;
    }
}
