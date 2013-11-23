/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Dissemination;
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Subject;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Dissemination does not meet timing constraints set by an information need.
 */
public class UntimelyDissemination extends AbstractIssueDetector {

    public UntimelyDissemination() {
    }

    @Override
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getPlanService();
        List<Issue> issues = new ArrayList<Issue>();
        final Flow flow = (Flow) modelObject;
        if ( flow.isNeed() ) {
            Part target = (Part) flow.getTarget();
            for ( ElementOfInformation eoi : flow.getEffectiveEois() ) {
                if ( eoi.isTimeSensitive() ) {
                    Subject subject = new Subject( flow.getName(), eoi.getContent() );
                    // dissemination from sources
                    List<Dissemination> disseminations = queryService.findAllDisseminations( target, subject, false );
                    if ( !disseminations.isEmpty() ) {
                        boolean untimely = CollectionUtils.exists( disseminations, new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                Dissemination d = (Dissemination) object;
                                return d.getDelay().compareTo( flow.getMaxDelay() ) > 0;
                            }
                        } );
                        if ( untimely ) {
                            Issue issue = makeIssue( communityService, Issue.ROBUSTNESS, flow );
                            issue.setDescription( "The need for element \"" + eoi.getContent()
                                                  + "\" might not be satisfied in a timely manner." );
                            issue.setRemediation( "Increase the max delay of the information need"
                                                  + "\nor reduce the max delays of flows disseminating the element \""
                                                  + eoi.getContent() + "\"." );
                            issue.setSeverity( computeTaskFailureSeverity( queryService, target ) );
                            issues.add( issue );
                        }
                    }
                }
            }
        }
        return issues;
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Flow;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Untimely dissemination of information";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
