package com.mindalliance.channels.api.plan;

import com.mindalliance.channels.api.entities.AgentData;
import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.dao.user.PlanParticipation;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.PlanService;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Web service data element for a plan summary.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/12/11
 * Time: 1:36 PM
 */
@XmlType( propOrder = {"planIdentifier", "dateVersioned", "description", "planners", "participations", "supervised", "documentation"} )

public class PlanSummaryData {

    private PlanService planService;
    private ChannelsUserDao userDao;

    public PlanSummaryData() {
        // required
    }

    public PlanSummaryData( PlanService planService, ChannelsUserDao userDao ) {
        this.planService = planService;
        this.userDao = userDao;
    }

    @XmlElement
    public PlanIdentifierData getPlanIdentifier() {
        return new PlanIdentifierData( getPlan() );
    }

    @XmlElement
    public String getDateVersioned() {
        return new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" ).format( getPlan().getWhenVersioned() );
    }
    @XmlElement
    public String getDescription() {
        return getPlan().getDescription();
    }

    @XmlElement( name = "planner" )
    public List<UserData> getPlanners() {
        List<UserData> planners = new ArrayList<UserData>();
        for ( ChannelsUser planner : planService.getUserDao().getPlanners( getPlan().getUri() ) ) {
            planners.add( new UserData( planner ) );
        }
        return planners;
    }

    @XmlElement( name = "participatingAs" )
    public List<ParticipationData> getParticipations() {
        List<ParticipationData> participationDataList = new ArrayList<ParticipationData>();
        ChannelsUser user = ChannelsUser.current( userDao );
        List<PlanParticipation> participations = planService.findParticipations( user.getUsername() );
        for ( PlanParticipation participation : participations ) {
            participationDataList.add( new ParticipationData( participation, user, planService ) );
        }
        return participationDataList;
    }

    @XmlElement( name = "supervised" )
    public List<AgentData> getSupervised() {
        List<AgentData> underlings = new ArrayList<AgentData>();
        for ( Actor underling : findSupervised() ) {
            underlings.add( new AgentData( underling, getPlan() ) );
        }
        return underlings;
    }

    @XmlElement
    public DocumentationData getDocumentation() {
        return new DocumentationData( getPlan() );
    }

    private List<Actor> getParticipantActors() {
        List<Actor> actors = new ArrayList<Actor>();
        List<PlanParticipation> participations = planService.findParticipations( ChannelsUser.current( userDao ).getUsername() );
        for ( PlanParticipation participation : participations ) {
            Actor actor = participation.getActor( planService );
            if ( actor != null ) actors.add( actor );
        }
        return actors;
    }

    private List<Actor> findSupervised() {
        Set<Actor> supervisedSet = new HashSet<Actor>();
        for ( Actor actor : getParticipantActors() ) {
            supervisedSet.addAll( planService.findSupervised( actor ) );
        }
        return new ArrayList<Actor>( supervisedSet );
    }

    private Plan getPlan() {
        return planService.getPlan();
    }
}
