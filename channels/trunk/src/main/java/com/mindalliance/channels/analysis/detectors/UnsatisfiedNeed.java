package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.ElementOfInformation;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.nlp.Proximity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The information need is not fully satisfied by sharing commitments
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 14, 2009
 * Time: 11:57:43 AM
 */
public class UnsatisfiedNeed extends AbstractIssueDetector {

    public UnsatisfiedNeed() {
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public List<Issue> detectIssues( ModelObject modelObject ) {
        Flow need = (Flow) modelObject;
        List<Issue> issues = new ArrayList<Issue>();
        // if empty, then need = "give me anything you've got"
        List<Flow> sharings = getQueryService().findAllSharingsAddressing( need );
        final Set<ElementOfInformation> sharedEOIs = new HashSet<ElementOfInformation>();
        for ( Flow sharing : sharings ) {
            List<ElementOfInformation> eois = sharing.getEois();
            sharedEOIs.addAll( eois );
        }
        if ( !need.getEois().isEmpty() ) {
            List<ElementOfInformation> neededEOIs = need.getEois();
            List<ElementOfInformation> unsatisfiedEOIs = (List<ElementOfInformation>) CollectionUtils.select(
                    neededEOIs,
                    new Predicate() {
                        public boolean evaluate( Object obj ) {
                            final String neededEOI = ( (ElementOfInformation) obj ).getContent();
                            return !CollectionUtils.exists(
                                    sharedEOIs,
                                    new Predicate() {
                                        public boolean evaluate( Object o ) {
                                            String sharedEOI = ( (ElementOfInformation) o ).getContent();
                                            return getQueryService().isSemanticMatch(
                                                    neededEOI,
                                                    sharedEOI,
                                                    Proximity.HIGH );
                                        }
                                    }
                            );
                        }
                    } );
            if ( !unsatisfiedEOIs.isEmpty() ) {
                DetectedIssue issue = this.makeIssue( Issue.COMPLETENESS, need );
                if ( need.isCritical() ) {
                    issue.setSeverity( getQueryService().getPartPriority( (Part) need.getTarget() ) );
                } else {
                    issue.setSeverity( Issue.Level.Minor );
                }
                StringBuffer sb = new StringBuffer();
                for ( ElementOfInformation eoi : unsatisfiedEOIs ) {
                    sb.append( " -- " );
                    sb.append( StringUtils.abbreviate( eoi.getContent(), 25 ) );
                }
                issue.setDescription( "There is apparently no sharing"
                        + " of these needed elements of information: "
                        + sb.toString() );
                issue.setRemediation( "Add sharing flows\n "
                        + "or extend current sharing flows to include the missing elements of information." );
                issues.add( issue );
            }
        } else {
            if ( sharings.isEmpty() ) {
                // Open ended need is satisifed by any synonymous commitment.
                DetectedIssue issue = makeIssue( Issue.COMPLETENESS, need );
                if ( need.isCritical() ) {
                    issue.setSeverity( getQueryService().getPartPriority( (Part) need.getTarget() ) );
                } else {
                    issue.setSeverity( Issue.Level.Minor );
                }
                issue.setDescription( "The neded information is not shared." );
                issue.setRemediation( "Add a sharing flow of the same name." );
                issues.add( issue );
            }
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Flow
                && !modelObject.getName().trim().isEmpty()
                && ( (Flow) modelObject ).getSource().isConnector();
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
        return "Information need not fully satisfied";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
