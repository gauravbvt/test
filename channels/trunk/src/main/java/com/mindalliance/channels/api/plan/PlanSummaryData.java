package com.mindalliance.channels.api.plan;

import com.mindalliance.channels.api.entities.ActorData;
import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.participation.Agent;
import com.mindalliance.channels.core.community.participation.UserParticipation;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Plan;
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
    private List<ActorData> openActorList;
    private List<ActorData> underlings;
    private DocumentationData documentation;
    private PlanIdentifierData planIdentifierData;
    private List<Actor> actors;
    private Plan plan;

    public PlanSummaryData() {
        // required
    }

    public PlanSummaryData( String serverUrl, CommunityService communityService ) {
        init(  serverUrl, communityService );
    }

    private void init(
            String serverUrl,
            CommunityService communityService ) {
        plan = communityService.getPlan();
        initPlanners( communityService );
        initParticipations( serverUrl,communityService );
        initOpenActors( serverUrl, communityService );
        initParticipantActors( communityService );
        initSupervised( serverUrl, communityService );
        documentation = new DocumentationData( serverUrl, getPlan() );
        planIdentifierData = new PlanIdentifierData( communityService );
    }

    private void initSupervised( String serverUrl, CommunityService communityService ) {
        Set<Actor> supervisedSet = new HashSet<Actor>();
        for ( Actor actor : getParticipantActors() ) {
            supervisedSet.addAll( communityService.getPlanService().findSupervised(  actor ) );
        }
        List<Actor> supervisedActors = new ArrayList<Actor>( supervisedSet );
        underlings = new ArrayList<ActorData>();
        for ( Actor underling : supervisedActors ) {
            underlings.add( new ActorData( serverUrl, underling, communityService ) );
        }

    }

    private void initParticipantActors(
            CommunityService communityService ) {
        actors = new ArrayList<Actor>();
        List<UserParticipation> participations = communityService.getUserParticipationService().getActiveUserParticipations(
                ChannelsUser.current( communityService.getUserDao() ),
                communityService );
        for ( UserParticipation participation : participations ) {
            Actor actor = participation.getAgent( communityService ).getActor();  // todo - agents!
            if ( actor != null ) actors.add( actor );
        }

    }

    private void initOpenActors(
            String serverUrl,
            CommunityService communityService ) {
        openActorList = new ArrayList<ActorData>();
        ChannelsUser user = ChannelsUser.current( communityService.getUserDao() );
        List<Agent> openAgents = communityService.getParticipationManager()
                .findSelfAssignableOpenAgents( communityService, user );

        for ( Agent openAgent : openAgents ) {
            openActorList.add( new ActorData( serverUrl, openAgent.getActor(), communityService ) ); // todo - agents!
        }

    }

    private void initParticipations(
            String serverUrl,
            CommunityService communityService ) {
        participationDataList = new ArrayList<ParticipationData>();
        ChannelsUser user = ChannelsUser.current( communityService.getUserDao() );
        List<UserParticipation> participations = communityService.getUserParticipationService()
                .getUserParticipations( user, communityService );
        for ( UserParticipation participation : participations ) {
            participationDataList.add( new ParticipationData(
                    serverUrl,
                    communityService,
                    user,
                    participation ) );
        }

    }

    private void initPlanners( CommunityService communityService ) {
        planners = new ArrayList<UserData>();
        for ( ChannelsUser planner : communityService.getUserDao().getPlanners( getPlan().getUri() ) ) {
            planners.add( new UserData( planner, communityService ) );
        }

    }

    @XmlElement
    public PlanIdentifierData getPlanIdentifier() {
        return planIdentifierData;
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
    public List<ActorData> getOpenActors() {
        return openActorList;
    }

    @XmlElement( name = "supervised" )
    public List<ActorData> getSupervised() {
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
