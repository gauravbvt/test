/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.core.model.Classification;
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Subject;
import com.mindalliance.channels.core.model.Transformation;
import com.mindalliance.channels.engine.query.QueryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Flow declassifies information.
 */
public class FlowDeclassifies extends AbstractIssueDetector {

    public FlowDeclassifies() {
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        Flow flow = (Flow) modelObject;
        List<Issue> issues = new ArrayList<Issue>();
        if ( flow.isSharing() ) {
            Part source = (Part) flow.getSource();
            List<Flow> receives = source.getAllSharingReceives();
            for ( Flow receive : receives ) {
                for ( ElementOfInformation inEOI : receive.getEois() ) {
                    if ( inEOI.isClassified() ) {
                        for ( ElementOfInformation outEOI : flow.getEois() ) {
                            if ( declassifies( outEOI, flow, inEOI, receive, queryService ) ) {
                                Issue issue = makeIssue( queryService, Issue.ROBUSTNESS, flow );
                                Subject inSubject = new Subject( receive.getName(), inEOI.getContent() );
                                Subject outSubject = new Subject( flow.getName(), outEOI.getContent() );
                                issue.setDescription( "Received element of information " + inSubject
                                                      + " is declassified when sending " + outSubject + "." );
                                issue.setRemediation(
                                        "Set the classification of sent " + outSubject + " to be at least be as high"
                                        + " as received " + inSubject + "\nor lower the classification of received "
                                        + inSubject + "\nor do not send " + outSubject );
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

    private boolean declassifies( ElementOfInformation outEOI, Flow send, ElementOfInformation inEOI, Flow receive,
                                  QueryService queryService ) {
        Subject subjectSent = new Subject( send.getName(), outEOI.getContent() );
        Subject subjectReceived = new Subject( receive.getName(), inEOI.getContent() );
        Transformation xform = outEOI.getTransformation();
        boolean isSame = xform.isNone() ? subjectSent.equals( subjectReceived ) : xform.renames( subjectReceived );
        return isSame && Classification.hasHigherClassification( inEOI.getClassifications(),
                                                                 outEOI.getClassifications(),
                                                                 queryService.getPlan() );
    }

    @Override
    public boolean appliesTo( ModelObject modelObject ) {
        return Flow.class.isAssignableFrom( modelObject.getClass() );
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Information is declassified";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
