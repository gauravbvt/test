package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.nlp.Matcher;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Detects whether a critical receive from an actor (singleton source) has no alternate source.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 19, 2008
 * Time: 4:35:27 PM
 */
public class NoSourceRedundancy extends AbstractIssueDetector {

    public NoSourceRedundancy() { }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Part;
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected String getLabel() {
        return "Only one source of critical information";
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
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        final Part part = (Part) modelObject;
        // Find all critical sourced receives of a part
        Iterator<Flow> criticalReceives = new FilterIterator( part.receives(),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        Flow receive = (Flow) obj;
                        return receive.isCritical()
                                && !receive.getName().isEmpty()
                                && receive.getSource().isPart()
                                && ( (Part) receive.getSource() ).getActor() != null;
                    }
                } );
        // Find critical receives without alternate sources
        while ( criticalReceives.hasNext() ) {
            final Flow criticalReceive = criticalReceives.next();
            final Actor sourceActor = ( (Part) criticalReceive.getSource() ).getActor();
            final String name = criticalReceive.getName();
            // Get all differently sourced receives for same info
            // by the part or other "matching" parts
            Iterator<Flow> alternates = new FilterIterator( part.getSegment().flows(),
                    new Predicate() {
                        public boolean evaluate( Object obj ) {
                            Flow otherFlow = (Flow) obj;
                            return otherFlow != criticalReceive
                                    && Matcher.getInstance().same( otherFlow.getName(), name )
                                    && otherFlow.getTarget().isPart()
                                    && partsMatch( (Part) otherFlow.getTarget(), part )
                                    && otherFlow.getSource().isPart()
                                    && ( (Part) otherFlow.getSource() ).getActor() != sourceActor;
                        }
                    } );
            if ( !alternates.hasNext() ) {
                DetectedIssue issue = makeIssue( DetectedIssue.ROBUSTNESS, part );
                issue.setDescription( "Has a only one source task for critical \""
                        + name + "\"" );
                issue.setRemediation( "Add alternate source\nor make the information non-critical." );
                issue.setSeverity( getQueryService().computePartPriority( part ) );
                issues.add( issue );
            }
        }
        return issues;
    }

    // Same actors if given, else same roles
    private boolean partsMatch( Part part, Part otherPart ) {
        Matcher matcher = Matcher.getInstance();
        if ( part.getActor() != null && otherPart.getActor() != null ) {
            return matcher.same( part.getActor().getName(),
                                                otherPart.getActor().getName() );
        } else if ( part.getRole() != null && otherPart.getRole() != null ) {
            return matcher.same( part.getRole().getName(),
                                                otherPart.getRole().getName() );
        } else return false;
    }
}
