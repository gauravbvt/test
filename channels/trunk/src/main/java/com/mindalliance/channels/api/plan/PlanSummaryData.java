package com.mindalliance.channels.api.plan;

import com.mindalliance.channels.api.entities.AgentData;
import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.dao.user.PlanParticipation;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.PlanService;
import org.apache.commons.lang.StringEscapeUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
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
@XmlType( propOrder = {"planIdentifier", "dateVersioned", "description", "planners", "participations", "openActors", "supervised", "documentation"} )

public class PlanSummaryData  implements Serializable {

    private List<UserData> planners;
    private List<ParticipationData> participationDataList;
    private List<AgentData> openActorList;
    private List<Actor> actors;
    private List<Actor> supervisedActors;
    private Plan plan;

    public PlanSummaryData() {
        // required
    }

    public PlanSummaryData( PlanService planService, ChannelsUserDao userDao ) {
        init( planService, userDao );
    }

    private void init( PlanService planService, ChannelsUserDao userDao ) {
        plan = planService.getPlan();
        initPlanners( planService );
        initParticipations( planService, userDao );
        initOpenActors( planService, userDao );
        initParticipantActors( planService, userDao );
        initSupervised( planService, userDao );
    }

    private void initSupervised( PlanService planService, ChannelsUserDao userDao ) {
        Set<Actor> supervisedSet = new HashSet<Actor>();
        for ( Actor actor : getParticipantActors() ) {
            supervisedSet.addAll( planService.findSupervised( actor ) );
        }
        supervisedActors = new ArrayList<Actor>( supervisedSet );
    }

    private void initParticipantActors( PlanService planService, ChannelsUserDao userDao ) {
        actors = new ArrayList<Actor>();
        List<PlanParticipation> participations = planService.findParticipations(
                ChannelsUser.current( userDao ).getUsername(), getPlan() );
        for ( PlanParticipation participation : participations ) {
            Actor actor = participation.getActor( planService );
            if ( actor != null ) actors.add( actor );
        }

    }

    private void initOpenActors( PlanService planService, ChannelsUserDao userDao ) {
        openActorList = new ArrayList<AgentData>(  );
        ChannelsUser user = ChannelsUser.current( userDao );
        List<Actor> openActors = planService.findOpenActors( user, getPlan() );
        for ( Actor openActor : openActors ) {
            openActorList.add( new AgentData( openActor, getPlan() ) );
        }

    }

    private void initParticipations( PlanService planService, ChannelsUserDao userDao ) {
        participationDataList = new ArrayList<ParticipationData>();
        ChannelsUser user = ChannelsUser.current( userDao );
        List<PlanParticipation> participations = planService.findParticipations( user.getUsername(), getPlan() );
        for ( PlanParticipation participation : participations ) {
            participationDataList.add( new ParticipationData( participation, user, planService ) );
        }

    }

    private void initPlanners( PlanService planService ) {
         planners = new ArrayList<UserData>();
        for ( ChannelsUser planner : planService.getUserDao().getPlanners( getPlan().getUri() ) ) {
            planners.add( new UserData( planner ) );
        }

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
        return StringEscapeUtils.escapeXml( getPlan().getDescription() );
    }

    @XmlElement( name = "planner" )
    public List<UserData> getPlanners() {
        return planners;
    }

    @XmlElement( name = "participatingAs" )
    public List<ParticipationData> getParticipations() {
                return participationDataList;
    }

    @XmlElement( name = "openAgent" )
    public List<AgentData> getOpenActors() {
        return openActorList;
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
        return actors;
    }

    private List<Actor> findSupervised() {
        return supervisedActors;
    }

    private Plan getPlan() {
        return plan;
    }
}
