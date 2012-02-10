package com.mindalliance.channels.api.plan;

import com.mindalliance.channels.api.entities.AgentData;
import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Participation;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.PlanService;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Web service data element for a plan summary.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/12/11
 * Time: 1:36 PM
 */
@XmlType( propOrder = {"planIdentifier", "description", "planners", "participation", "supervised", "documentation"} )

public class PlanSummaryData {

    private PlanService planService;
    private ChannelsUserDao userDao;

    public PlanSummaryData() {
        // required
    }

    public PlanSummaryData( PlanService planService, ChannelsUserDao userDao  ) {
        this.planService = planService;
        this.userDao = userDao;
    }

    @XmlElement
    public PlanIdentifierData getPlanIdentifier() {
        return new PlanIdentifierData( getPlan() );
    }

    @XmlElement
    public String getDescription() {
        return getPlan().getDescription();
    }

    @XmlElement
    public List<UserData> getPlanners() {
        List<UserData> planners = new ArrayList<UserData>(  );
        for ( ChannelsUser planner : planService.getUserDao().getPlanners( getPlan().getUri() ) ) {
              planners.add( new UserData( planner ) );
        }
        return planners;
    }

    @XmlElement( name = "participatingAs" )
    public ParticipationData getParticipation() {
        ChannelsUser user = ChannelsUser.current( userDao );
        Participation participation = planService.findParticipation( user.getUsername() );
        return participation == null || participation.getActor() == null
                ? null
                : new ParticipationData( participation, user, getPlan() );
    }

    @XmlElement( name = "supervised" )
    public List<AgentData> getSupervised() {
        List<AgentData> underlings = new ArrayList<AgentData>(  );
        for ( Actor underling : findSupervised() ) {
            underlings.add(  new AgentData( underling, getPlan() ) );
        }
        return underlings;
    }

    @XmlElement
    public DocumentationData getDocumentation() {
        return new DocumentationData( getPlan() );
    }

    private Actor getParticipant() {
        Participation participation = planService.findParticipation( ChannelsUser.current( userDao ).getUsername() );
        if ( participation != null ) {
            return participation.getActor();
        } else {
            return null;
        }
    }

    private List<Actor> findSupervised() {
        Actor actor = getParticipant();
        if ( actor == null ) {
            return new ArrayList<Actor>(  );
        } else {
            return planService.findSupervised( actor );
        }
    }

    private Plan getPlan() {
        return planService.getPlan();
    }
}
