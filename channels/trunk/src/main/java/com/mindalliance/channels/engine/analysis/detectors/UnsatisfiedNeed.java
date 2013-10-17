/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.analysis.DetectedIssue;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The information need is not fully satisfied by communication commitments
 */
public class UnsatisfiedNeed extends AbstractIssueDetector {

    public UnsatisfiedNeed() {
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        Flow need = (Flow) modelObject;
        List<Issue> issues = new ArrayList<Issue>();
        if ( !need.getName().isEmpty() && need.isNeed() ) {
            // if empty, then need = "give me anything you've got"
            List<Flow> sharings = queryService.findAllSharingsAddressingNeed( need );
            if ( sharings.isEmpty() ) {
                // Open ended need is satisifed by any synonymous commitment.
                DetectedIssue issue = makeIssue( queryService, Issue.COMPLETENESS, need );
                if ( need.isCritical() )
                    issue.setSeverity( queryService.computePartPriority( (Part) need.getTarget() ) );
                else
                    issue.setSeverity( Level.Low );
                issue.setDescription( "The needed information is not shared." );
                issue.setRemediation(
                        "Add a sharing flow of the same name " + "with matching elements and restriction, if any." );
                issues.add( issue );
            } else {
                final Set<ElementOfInformation> sharedEOIs = new HashSet<ElementOfInformation>();
                for ( Flow sharing : sharings )
                    sharedEOIs.addAll( sharing.getEffectiveEois() );

                if ( !need.getEffectiveEois().isEmpty() ) {
                    List<ElementOfInformation> neededEOIs = need.getEffectiveEois();
                    List<ElementOfInformation> unsatisfiedEOIs =
                            (List<ElementOfInformation>) CollectionUtils.select( neededEOIs, new Predicate() {
                                @Override
                                public boolean evaluate( Object obj ) {
                                    final String neededEOI = ( (ElementOfInformation) obj ).getContent();
                                    return !CollectionUtils.exists( sharedEOIs, new Predicate() {
                                        @Override
                                        public boolean evaluate( Object object ) {
                                            String sharedEOI = ( (ElementOfInformation) object ).getContent();
                                            return Matcher.same( neededEOI, sharedEOI );
                                        }
                                    } );
                                }
                            } );
                    if ( !unsatisfiedEOIs.isEmpty() ) {
                        DetectedIssue issue = makeIssue( queryService, Issue.COMPLETENESS, need );
                        if ( need.isCritical() )
                            issue.setSeverity( queryService.computePartPriority( (Part) need.getTarget() ) );
                        else
                            issue.setSeverity( Level.Low );
                        StringBuffer sb = new StringBuffer();
                        for ( ElementOfInformation eoi : unsatisfiedEOIs ) {
                            sb.append( " -- " );
                            sb.append( StringUtils.abbreviate( eoi.getContent(), 25 ) );
                        }
                        issue.setDescription(
                                "There is apparently no sharing" + " of these needed elements of information: "
                                + sb.toString() + "." );
                        issue.setRemediation( "Add sharing flows\n "
                                              + "or extend current sharing flows to include the missing elements of information." );
                        issues.add( issue );
                    }
                }
            }
        }
        return issues;
    }

    @Override
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Flow;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Information need not fully satisfied";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
