package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/4/13
 * Time: 3:14 PM
 */
public class OrganizationGivesActorMultipleTitles extends AbstractIssueDetector {

    public OrganizationGivesActorMultipleTitles() {
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Organization && ( (Organization) modelObject ).isActual();
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Organization org = (Organization) modelObject;
        Map<Actor, Set<String>> actorTitles = new HashMap<Actor, Set<String>>();
        for ( Job job : org.getJobs() ) {
            String title = job.getRawTitle();
            Actor actor = job.getActor();
            if ( !title.isEmpty() ) {
                Set<String> titles = actorTitles.get( actor );
                if ( titles == null ) {
                    titles = new HashSet<String>();
                    actorTitles.put( actor, titles );
                }
                titles.add( title );
            }
        }
        for ( Actor actor : actorTitles.keySet() ) {
            Set<String> titles = actorTitles.get( actor );
            if ( titles.size() > 1 ) {
                Issue issue = makeIssue( communityService, Issue.VALIDITY, org );
                issue.setDescription( "The organization \""
                        + org.getName()
                        + "\" gives agent \"" + actor.getName() + "\" more than one title ("
                        + ChannelsUtils.listToString( new ArrayList<String>( titles ), ", ", " and " )
                        + ")." );
                issue.setRemediation( "Use the same  title for all the agent's jobs" +
                        "\nor give only one of its jobs a title." );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            }
        }
        return issues;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Organization gives agent multiple titles";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
