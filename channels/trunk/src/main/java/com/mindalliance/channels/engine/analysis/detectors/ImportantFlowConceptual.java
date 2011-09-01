package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 21, 2010
 * Time: 12:03:56 PM
 */
public class ImportantFlowConceptual extends AbstractIssueDetector {

    public ImportantFlowConceptual() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Flow flow = (Flow) modelObject;
        if ( flow.isSharing() ) {
            List<String> causes = getAnalyst().findConceptualCauses( flow );
            if ( !causes.isEmpty() ) {
            Level importance =  computeSharingFailureSeverity( flow );
            if ( importance.compareTo( Level.Low ) >= 1 ) {
                Issue issue = makeIssue( Issue.ROBUSTNESS, flow );
                issue.setDescription( "The flow is important but is conceptual: "
                        + ChannelsUtils.listToString( causes, ", and " )
                        + ".");
                issue.setSeverity( importance );
                List<String> remediations = getAnalyst().findConceptualRemediations( flow );
                issue.setRemediation( StringUtils.capitalize( ChannelsUtils.listToString( remediations, "\nor ", "\nor " )
                        + "." ) );
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
        return "Important sharing flow is conceptual";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}

