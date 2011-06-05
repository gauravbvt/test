package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.ElementOfInformation;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.InfoStandard;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.nlp.Matcher;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Flow EOIs don't match info standards.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/2/11
 * Time: 6:35 PM
 */
public class FlowEOIsNotStandardCompliant extends AbstractIssueDetector {

    public FlowEOIsNotStandardCompliant() {
    }

    @Override
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Flow flow = (Flow) modelObject;
        List<InfoStandard> infoStandards = flow.getInfoStandards( getPlan() );
        final Matcher matcher = Matcher.getInstance();
        for ( InfoStandard infoStandard : infoStandards ) {
            for ( final String eoiName : infoStandard.getEoiNames() ) {
                boolean exists = CollectionUtils.exists(
                        flow.getEois(),
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return matcher.same( eoiName, ( (ElementOfInformation) object ).getContent() );
                            }
                        } );
                if ( !exists ) {
                    Issue issue = makeIssue( Issue.COMPLETENESS, flow );
                    issue.setDescription( "Flow is tagged with info standard \""
                            + infoStandard.getName()
                            + "\" but is missing expected element of information \""
                            + eoiName
                            + "\"." );
                    issue.setSeverity( Level.Low );
                    issue.setRemediation( "Add element of information \""
                            + eoiName
                            + "\""
                            + "\nor remove info standard tag \""
                            + infoStandard.getName()
                            + "\"." );
                    issues.add( issue );
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
        return "Flow EOIs not standard compliant";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
