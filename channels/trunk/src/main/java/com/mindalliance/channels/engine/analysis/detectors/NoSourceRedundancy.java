package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.analysis.DetectedIssue;
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
    protected String getKindLabel() {
        return "A task has only one source for critical information it needs";
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
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        final Part part = (Part) modelObject;
        // Find all critical sourced receives of a part
        Iterator<Flow> criticalReceives = new FilterIterator( part.getAllSharingReceives().iterator(),
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
                                    && Matcher.same( otherFlow.getName(), name )
                                    && otherFlow.getTarget().isPart()
                                    && partsMatch( (Part) otherFlow.getTarget(), part )
                                    && otherFlow.getSource().isPart()
                                    && ( (Part) otherFlow.getSource() ).getActor() != sourceActor;
                        }
                    } );
            if ( !alternates.hasNext() ) {
                DetectedIssue issue = makeIssue( queryService, DetectedIssue.ROBUSTNESS, part );
                issue.setDescription( "Has a only one source task for critical \""
                        + name + "\"" );
                issue.setRemediation( "Add alternate source\nor make the information non-critical." );
                issue.setSeverity( queryService.computePartPriority( part ) );
                issues.add( issue );
            }
        }
        return issues;
    }

    // Same actors if given, else same roles
    private boolean partsMatch( Part part, Part otherPart ) {
        if ( part.getActor() != null && otherPart.getActor() != null ) {
            return Matcher.same( part.getActor().getName(), otherPart.getActor().getName() );
        } else if ( part.getRole() != null && otherPart.getRole() != null ) {
            return Matcher.same( part.getRole().getName(), otherPart.getRole().getName() );
        } else return false;
    }
}
