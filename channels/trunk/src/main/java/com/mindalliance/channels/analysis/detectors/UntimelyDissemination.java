package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Dissemination;
import com.mindalliance.channels.model.ElementOfInformation;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Subject;
import com.mindalliance.channels.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Dissemination does not meet timing constraints
 * set by an information need.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 21, 2009
 * Time: 3:47:25 PM
 */
public class UntimelyDissemination extends AbstractIssueDetector {

    public UntimelyDissemination() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        QueryService queryService = getQueryService();
        List<Issue> issues = new ArrayList<Issue>();
        final Flow flow = (Flow) modelObject;
        if ( flow.isNeed() ) {
            Part target = (Part) flow.getTarget();
            for ( ElementOfInformation eoi : flow.getEois() ) {
                if ( eoi.isTimeSensitive() ) {
                    Subject subject = new Subject( flow.getName(), eoi.getContent() );
                    // dissemination from sources
                    List<Dissemination> disseminations = queryService.findAllDisseminations(
                            target,
                            subject,
                            false );
                    if ( !disseminations.isEmpty() ) {
                        boolean untimely = CollectionUtils.exists(
                                disseminations,
                                new Predicate() {
                                    @Override
                                    public boolean evaluate( Object object ) {
                                        Dissemination d = (Dissemination) object;
                                        return d.getDelay().compareTo( flow.getMaxDelay() ) > 0;
                                    }
                                } );
                        if ( untimely ) {
                            Issue issue = makeIssue( Issue.ROBUSTNESS, flow );
                            issue.setDescription( "The need for element \""
                                    + eoi.getContent()
                                    + "\" might not be satisfied in a timely manner." );
                            issue.setRemediation( "Increase the max delay of the information need"
                                    + "\nor reduce the max delays of flows disseminating the element \""
                                    + eoi.getContent()
                                    + "\"." );
                            issue.setSeverity( getTaskFailureSeverity( target ) );
                            issues.add( issue );
                        }
                    }
                }
            }
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Flow;
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
        return "Untimely dissemination";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
