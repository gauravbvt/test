package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An agent has a job where the supervisor is form an entirely different organization.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/9/13
 * Time: 11:03 AM
 */
public class ActorHasSupervisorOutsideOwnOrganization extends AbstractIssueDetector {

    public ActorHasSupervisorOutsideOwnOrganization() {
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Actor && ( (Actor) modelObject ).isActual();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getPlanService();
        List<Issue> issues = new ArrayList<Issue>();
        Actor actor = (Actor) modelObject;
        List<Actor> supervisors = queryService.findAllSupervisorsOf( actor );
        if ( !supervisors.isEmpty() ) {
            final Set<Organization> actorEmployers = findAllEmployersOf( actor, queryService );
            for ( Actor supervisor : supervisors ) {
                Set<Organization> supervisorEmployers = findAllEmployersOf( supervisor, queryService );
                boolean outsideSupervision = !CollectionUtils.exists(
                        supervisorEmployers,
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                Organization supervisorEmployer = (Organization) object;
                                return actorEmployers.contains( supervisorEmployer );
                            }
                        }
                );
                if ( outsideSupervision ) {
                    List<String> actorEmployerNames = (List<String>) CollectionUtils.collect(
                            actorEmployers,
                            new Transformer() {
                                @Override
                                public Object transform( Object input ) {
                                    return ( (Organization) input ).getName();
                                }
                            }
                    );
                    Issue issue = makeIssue( communityService, Issue.VALIDITY, actor );
                    issue.setDescription( "Agent \""
                            + actor.getName()
                            + "\" is supervised by \""
                            + supervisor
                            + "\" who is from an entirely different organization."
                    );
                    issue.setRemediation( "Have the agent \""
                            + actor.getName()
                            + "\" be supervised by another agent employed by an organization also employing the agent, namely one of "
                            + ChannelsUtils.listToString( actorEmployerNames, ", ", " or ")
                            + ","
                            + "\nor have the supervisor \""
                            + supervisor.getName()
                            + "\" employed in one of the organizations also employing the agent." );
                    issue.setSeverity( Level.Medium );
                    issues.add( issue );
                }
            }
        }
        return issues;
    }

    private Set<Organization> findAllEmployersOf( Actor actor, QueryService queryService ) {
        Set<Organization> employers = new HashSet<Organization>();
        for ( Employment employment : queryService.findAllEmploymentsForActor( actor ) ) {
            employers.addAll( employment.getOrganization().selfAndAncestors() );
        }
        return employers;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Agent has a supervisor from outside own organization";
    }
}
