package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Classification;
import com.mindalliance.channels.model.ElementOfInformation;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Subject;
import com.mindalliance.channels.model.Transformation;

import java.util.ArrayList;
import java.util.List;

/**
 * Flow declassifies information.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 10, 2009
 * Time: 1:41:42 PM
 */
public class FlowDeclassifies extends AbstractIssueDetector {

    public FlowDeclassifies() {
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public List<Issue> detectIssues( ModelObject modelObject ) {
        Flow flow = (Flow) modelObject;
        List<Issue> issues = new ArrayList<Issue>();
        if ( flow.isSharing() ) {
            Part source = (Part) flow.getSource();
            List<Flow> receives = source.getAllSharingReceives();
            for ( Flow receive : receives ) {
                for ( ElementOfInformation inEOI : receive.getEois() ) {
                    if ( inEOI.isClassified() ) {
                        for ( ElementOfInformation outEOI : flow.getEois() ) {
                                if ( declassifies( outEOI, flow, inEOI, receive ) ) {
                                    Issue issue = makeIssue( Issue.ROBUSTNESS, flow );
                                    Subject inSubject = new Subject( receive.getName(), inEOI.getContent() );
                                    Subject outSubject = new Subject( flow.getName(), outEOI.getContent() );
                                    issue.setDescription( "Received element of information "
                                            + inSubject
                                            + " is declassified when sending "
                                            + outSubject
                                            +"." );
                                    issue.setRemediation( "Set the classification of sent "
                                            +  outSubject
                                            + " to be at least be as high"
                                            + " as received "
                                            + inSubject
                                            + "\nor lower the classification of received "
                                            + inSubject
                                            + "\nor do not send "
                                            + outSubject);
                                    issue.setSeverity( Level.Medium );
                                    issues.add( issue );
                                }
                            }
                        }
                    }
                }
        }
        return issues;
    }

    private boolean declassifies( ElementOfInformation outEOI, Flow send, ElementOfInformation inEOI, Flow receive ) {
        Subject subjectSent = new Subject( send.getName(), outEOI.getContent() );
        Subject subjectReceived = new Subject( receive.getName(), inEOI.getContent() );
        boolean isSame;
        Transformation xform = outEOI.getTransformation();
        if ( xform.isNone() ) {
            isSame = subjectSent.equals( subjectReceived );
        } else {
            isSame = xform.renames( subjectReceived );
        }
        return isSame &&
                Classification.hasHigherClassification(
                        inEOI.getClassifications(),
                        outEOI.getClassifications() );
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return Flow.class.isAssignableFrom( modelObject.getClass() );
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
    protected String getKindLabel() {
        return "Information is declassified";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
