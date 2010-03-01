package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Classification;
import com.mindalliance.channels.model.ElementOfInformation;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Severity;

import java.util.ArrayList;
import java.util.Iterator;
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
        Part source = (Part) flow.getSource();
        List<ElementOfInformation> classifiedEOIs = new ArrayList<ElementOfInformation>();
        Iterator<Flow> receives = source.receives();
        while ( receives.hasNext() ) {
            Flow receive = receives.next();
            if ( receive.isClassified() )
                for ( ElementOfInformation eoi : receive.getEois() ) {
                    if ( eoi.isClassified() ) classifiedEOIs.add( eoi );
                }
        }
        for ( ElementOfInformation eoi : flow.getEois() ) {
            for ( ElementOfInformation classifiedEOI : classifiedEOIs ) {
                if ( eoi.equals( classifiedEOI )
                        && Classification.hasHigherOrEqualClassification(
                        classifiedEOI.getClassifications(),
                        eoi.getClassifications() ) ) {
                    Issue issue = makeIssue( Issue.COMPLETENESS, flow );
                    issue.setDescription( "Received element of information \""
                            + classifiedEOI
                            + "\" is declassified to sent element \""
                            + eoi + "\"" );
                    issue.setRemediation( "Set the classification of the element sent to be as strong or"
                            + " greater as when it is received"
                            + "\nor lower the classification of the received element" +
                            "\nor do not send the classified element" );
                    issue.setSeverity( Severity.Major );
                    issues.add( issue );
                }
            }
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return Flow.class.isAssignableFrom( modelObject.getClass() )
                && !( (Flow) modelObject ).isNeed();
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
        return "Information is declassified";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
