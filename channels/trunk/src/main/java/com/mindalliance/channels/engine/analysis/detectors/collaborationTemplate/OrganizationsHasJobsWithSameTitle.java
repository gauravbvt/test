package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Some jobs have the same title.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 8/13/14
 * Time: 4:25 PM
 */
public class OrganizationsHasJobsWithSameTitle extends AbstractIssueDetector {

    public OrganizationsHasJobsWithSameTitle() {
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable identifiable ) {
        List<Issue> issues = new ArrayList<Issue>(  );
        Organization org = (Organization)identifiable;
        List<Job> jobs = org.getJobs();
        if ( jobs.size() > 1 ) {
            Map<String, List<Job>> titleCounts = new HashMap<String, List<Job>>();
            for ( Job job : jobs ) {
                List<Job> titleJobs = titleCounts.get( job.getTitle() );
                if ( titleJobs == null ) titleJobs = new ArrayList<Job>(  );
                titleJobs.add( job );
                titleCounts.put( job.getTitle(),titleJobs );
            }
            for ( String title : titleCounts.keySet() ) {
                List<Job> sameTitleJobs = titleCounts.get( title );
                if ( sameTitleJobs.size() > 1 ) {
                    Issue issue = makeIssue( communityService, Issue.VALIDITY, org );
                    issue.setDescription( "Organization \"" + org.getLabel() + "\" has more than one job with title \"" + title + "\"" );
                    issue.setRemediation( "Rename jobs so that only one has title \"" + title + "\""
                            + "\nor remove all but one job with title \"" + title + "\"." );
                    issue.setSeverity( Level.Medium );
                    issues.add( issue );
                }
            }
        }
        return issues;
    }

    @Override
    public boolean appliesTo( Identifiable identifiable ) {
        return identifiable instanceof Organization && ((Organization)identifiable).isActual();
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Jobs with identical titles";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }

}
