package com.mindalliance.channels.api.plan;

import com.mindalliance.channels.api.entities.AgentData;
import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.dao.user.PlanParticipation;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.PlanService;
import org.apache.commons.lang.StringEscapeUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
@XmlRootElement( name = "planSummary", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder = {"planIdentifier", "dateVersioned", "description", "planners", "participations", "openActors", "supervised", "documentation"} )

public class PlanSummaryData implements Serializable {

    private List<UserData> planners;
    private List<ParticipationData> participationDataList;
    private List<AgentData> openActorList;
    private List<AgentData> underlings;
    private DocumentationData documentation;
    private List<Actor> actors;
    private Plan plan;

    public PlanSummaryData() {
        // required
    }

    public PlanSummaryData(
            String serverUrl,
            PlanService planService,
            ChannelsUserDao userDao,
            PlanParticipationService planParticipationService ) {
        init( serverUrl, planService, userDao, planParticipationService );

    }

    private void init(
            String serverUrl,
            PlanService planService,
            ChannelsUserDao userDao,
            PlanParticipationService planParticipationService ) {
        plan = planService.getPlan();
        initPlanners( planService );
        initParticipations( serverUrl,planService, userDao, planParticipationService );
        initOpenActors( serverUrl, planService, userDao, planParticipationService );
        initParticipantActors( planService, userDao, planParticipationService );
        initSupervised( serverUrl, planService );
        documentation = new DocumentationData( serverUrl, getPlan() );
    }

    private void initSupervised( String serverUrl, PlanService planService ) {
        Set<Actor> supervisedSet = new HashSet<Actor>();
        for ( Actor actor : getParticipantActors() ) {
            supervisedSet.addAll( planService.findSupervised(  actor ) );
        }
        List<Actor> supervisedActors = new ArrayList<Actor>( supervisedSet );
        underlings = new ArrayList<AgentData>();
        for ( Actor underling : supervisedActors ) {
            underlings.add( new AgentData( serverUrl, underling, getPlan() ) );
        }

    }

    private void initParticipantActors(
            PlanService planService,
            ChannelsUserDao userDao,
            PlanParticipationService planParticipationService ) {
        actors = new ArrayList<Actor>();
        List<PlanParticipation> participations = planParticipationService.getParticipations(
                getPlan(),
                ChannelsUser.current( userDao ).getUserInfo(),
                planService );
        for ( PlanParticipation participation : participations ) {
            Actor actor = participation.getActor( planService );
            if ( actor != null ) actors.add( actor );
        }

    }

    private void initOpenActors(
            String serverUrl,
            PlanService planService,
            ChannelsUserDao userDao,
            PlanParticipationService planParticipationService ) {
        openActorList = new ArrayList<AgentData>();
        ChannelsUser user = ChannelsUser.current( userDao );
        List<Actor> openActors = planParticipationService.findOpenActors( user, planService );
        for ( Actor openActor : openActors ) {
            openActorList.add( new AgentData( serverUrl, openActor, getPlan() ) );
        }

    }

    private void initParticipations(
            String serverUrl,
            PlanService planService,
            ChannelsUserDao userDao,
            PlanParticipationService planParticipationService ) {
        participationDataList = new ArrayList<ParticipationData>();
        ChannelsUser user = ChannelsUser.current( userDao );
        List<PlanParticipation> participations = planParticipationService.getParticipations(
                getPlan(),
                user.getUserInfo(), planService );
        for ( PlanParticipation participation : participations ) {
            participationDataList.add( new ParticipationData( serverUrl, participation, user, planService ) );
        }

    }

    private void initPlanners( PlanService planService ) {
        planners = new ArrayList<UserData>();
        for ( ChannelsUser planner : planService.getUserDao().getPlanners( getPlan().getUri() ) ) {
            planners.add( new UserData( planner, planService ) );
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
        return underlings;
    }

    @XmlElement
    public DocumentationData getDocumentation() {
        return documentation;
    }

    private List<Actor> getParticipantActors() {
        return actors;
    }

    private Plan getPlan() {
        return plan;
    }
}
