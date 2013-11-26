package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 9/30/13
 * Time: 9:26 AM
 */
public class ActorOnlyHasLinkedJobs extends AbstractIssueDetector {

    public ActorOnlyHasLinkedJobs() {
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Actor && ((Actor)modelObject).isActual();
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getPlanService();
        Actor actor = (Actor)modelObject;
        List<Issue> issues = new ArrayList<Issue>(  );
        List<Employment> allEmployments = queryService.findAllEmploymentsForActor( actor );
        boolean hasPrimaryJob = CollectionUtils.exists(
                allEmployments,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ((Employment)object).getJob().isPrimary();
                    }
                }
        );
        if ( !allEmployments.isEmpty() && !hasPrimaryJob ) {
            Issue issue = makeIssue( communityService, Issue.VALIDITY, actor );
            issue.setRemediation( "Agent " + actor.getName() + " only has linked jobs." );
            issue.setRemediation( "Make one of the agent's jobs a non-linked job." );
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
        return "Agent only has linked jobs";
    }
}
