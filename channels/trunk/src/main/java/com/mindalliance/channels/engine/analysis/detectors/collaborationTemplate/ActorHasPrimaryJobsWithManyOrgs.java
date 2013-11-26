package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An actor can have at most one primary job in one organization
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 9/30/13
 * Time: 9:26 AM
 */
public class ActorHasPrimaryJobsWithManyOrgs extends AbstractIssueDetector {

    public ActorHasPrimaryJobsWithManyOrgs() {
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Actor && ( (Actor) modelObject ).isActual();
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getPlanService();
        Actor actor = (Actor) modelObject;
        List<Issue> issues = new ArrayList<Issue>();
        List<Employment> allEmployments = queryService.findAllEmploymentsForActor( actor );
        Set<String> orgs = new HashSet<String>();
        for ( Employment employment : allEmployments ) {
            if ( employment.getJob().isPrimary() )
                orgs.add( employment.getOrganization().getName() );
        }
        if ( orgs.size() > 1 ) {
            Issue issue = makeIssue( communityService, Issue.VALIDITY, actor );
            StringBuilder sb = new StringBuilder();
            sb.append( "Agent " )
                    .append( actor.getName() )
                    .append( " has primary (hiring) jobs in " );
            sb.append( ChannelsUtils.listToString( new ArrayList<String>( orgs ), " and " ) );
            issue.setDescription( sb.toString() );
            issue.setRemediation( "Make all jobs but one linked jobs\nor assign all but one job to other agents." );
            issue.setSeverity( Level.Medium );
            issues.add( issue );
        }
        return issues;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Agent has multiple primary jobs spread across many organizations";
    }
}
