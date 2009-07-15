package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.nlp.Proximity;
import com.mindalliance.channels.util.Matcher;
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
        List<Flow> commitments = getQueryService().findAllSharingCommitmentsAddressing( need );
        final Set<String> committedEOIs = new HashSet<String>();
        for ( Flow commitment : commitments ) {
            List<String> eois = Matcher.extractEOIs( commitment.getDescription() );
            committedEOIs.addAll( eois );
        }
        if ( !need.getDescription().isEmpty() ) {
            List<String> neededEOIs = Matcher.extractEOIs( need.getDescription() );
            List<String> unsatisfiedEOIs = (List<String>) CollectionUtils.select(
                    neededEOIs,
                    new Predicate() {
                        public boolean evaluate( Object obj ) {
                            final String neededEOI = (String) obj;
                            return !CollectionUtils.exists(
                                    committedEOIs,
                                    new Predicate() {
                                        public boolean evaluate( Object o ) {
                                            String committedEOI = (String) o;
                                            return getQueryService().isSemanticMatch(
                                                    neededEOI,
                                                    committedEOI,
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
                for ( String eoi : unsatisfiedEOIs ) {
                    sb.append( " -- " );
                    sb.append( StringUtils.abbreviate( eoi, 25 ) );
                }
                issue.setDescription( "There is apparently no commitment to share"
                        + " these needed elements of informations: "
                        + sb.toString() );
                issue.setRemediation( "Obtain additional sharing commitments, "
                        + "or extends current commitments to include the missing elements of informations" );
                issues.add( issue );
            }
        } else {
            if ( commitments.isEmpty() ) {
                // Open ended need is satisifed by any synonymous commitment.
                DetectedIssue issue = makeIssue( Issue.COMPLETENESS, need );
                if ( need.isCritical() ) {
                    issue.setSeverity( getQueryService().getPartPriority( (Part) need.getTarget() ) );
                } else {
                    issue.setSeverity( Issue.Level.Minor );
                }
                issue.setDescription( "There is no commitment to share this information." );
                issue.setRemediation( "Add a sharing commitment of the same name." );
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
        return "Information need not fully satisfied.";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
