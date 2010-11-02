package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.ElementOfInformation;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.nlp.Matcher;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Redundant EOI.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 28, 2010
 * Time: 5:04:46 PM
 */
public class RedundantEOI extends AbstractIssueDetector {

    public RedundantEOI() {
    }

    /**
     * {inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        Flow flow = (Flow) modelObject;
        List<Issue> issues = new ArrayList<Issue>();
        final Matcher matcher = Matcher.getInstance();
        for ( final ElementOfInformation eoi : flow.getEois() ) {
            boolean redundant = CollectionUtils.exists(
                    flow.getEois(),
                    new Predicate() {
                        public boolean evaluate( Object object ) {
                            ElementOfInformation other = (ElementOfInformation) object;
                            return other != eoi
                                    && matcher.same( eoi.getContent(), other.getContent() );
                        }
                    }
            );
            if ( redundant ) {
                Issue issue = makeIssue( Issue.VALIDITY, flow );
                issue.setDescription( "Element \"" + eoi.getContent() + "\" is repeated." );
                issue.setSeverity( Level.Low );
                issue.setRemediation( "Remove repeated element \"" + eoi.getContent() + "\"" );
                issues.add( issue );
            }
        }
        return issues;
    }

    /**
     * {inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return Flow.class.isAssignableFrom( modelObject.getClass() );
    }

    /**
     * {inheritDoc}
     */
    public String getTestedProperty() {
        return null;
    }

    /**
     * {inheritDoc}
     */
    protected String getLabel() {
        return "Redundant element of information";
    }
}
