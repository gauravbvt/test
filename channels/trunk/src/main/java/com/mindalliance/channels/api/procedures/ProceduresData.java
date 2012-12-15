package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.api.entities.OrganizationData;
import com.mindalliance.channels.api.plan.PlanIdentifierData;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.participation.UserParticipation;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.core.query.Assignments;
import com.mindalliance.channels.core.query.Commitments;
import com.mindalliance.channels.core.query.PlanService;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Web Service data element for the procedures of an actor according to a plan.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/5/11
 * Time: 12:25 PM
 */
@XmlRootElement( name = "procedures", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder = {"date", "planIdentifier", "userEmail", "dateVersioned", "actorIds", "employments", "procedures", "environment"} )
public class ProceduresData implements Serializable {

    private List<Actor> actors;
    private ChannelsUser user;
    private List<ProcedureData> procedures;
    private List<EmploymentData> employments;
    private List<Actor> participatingActors;
    private EnvironmentData environmentData;
    private PlanIdentifierData planIdentifierData;
    private String dateVersioned;

    public ProceduresData() {
        // required
    }

    public ProceduresData(
            String serverUrl,
            PlanCommunity planCommunity,
            List<UserParticipation> participations,
            ChannelsUser user ) {
        this.user = user;
        initData( serverUrl, participations, planCommunity );
    }

    public ProceduresData(
            String serverUrl,
            PlanCommunity planCommunity,
            Actor actor ) {
        this.actors = new ArrayList<Actor>();
        actors.add( actor );
        initData( serverUrl, planCommunity );
    }


    private void initData(
            String serverUrl,
            List<UserParticipation> participations,
            PlanCommunity planCommunity ) {
        initParticipatingActors( serverUrl, participations, planCommunity );
        this.actors = getActors( participations );
        initData( serverUrl, planCommunity );
    }

    private void initData( String serverUrl, PlanCommunity planCommunity ) {
        initProcedures( serverUrl, planCommunity );
        initEmployments( planCommunity );
        environmentData = new EnvironmentData( serverUrl, this, planCommunity );
        planIdentifierData = new PlanIdentifierData( planCommunity );
        dateVersioned = new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" )
                .format( planCommunity.getPlan().getWhenVersioned() );
    }

    private void initEmployments( PlanCommunity planCommunity ) {
        employments = new ArrayList<EmploymentData>();
        for ( Actor actor : actors )
            for ( Employment employment : planCommunity.getPlanService().findAllEmploymentsForActor( actor ) ) {
                employments.add( new EmploymentData( employment ) );
            }
    }

    private void initProcedures( String serverUrl, PlanCommunity planCommunity ) {
        procedures = new ArrayList<ProcedureData>();
        PlanService planService = planCommunity.getPlanService();
        Commitments allCommitments = planService.getAllCommitments( true, false );
        Set<Assignment> assignments = new HashSet<Assignment>();
        for ( Actor actor : actors ) {
            for ( Assignment assignment : getActorAssignments( actor, planService ) ) {
                assignments.add( assignment );
            }
        }
        for ( Assignment assignment : assignments ) {
            procedures.add( new ProcedureData(
                    serverUrl,
                    planCommunity,
                    assignment,
                    allCommitments.benefiting( assignment ),
                    allCommitments.committing( assignment ),
                    user ) );
        }
    }

    private void initParticipatingActors(
            String serverUrl,
            List<UserParticipation> participations,
            PlanCommunity planCommunity ) {
        participatingActors = new ArrayList<Actor>();
        PlanService planService = planCommunity.getPlanService();
        for ( UserParticipation participation : participations ) {
            Actor actor = participation.getAgent( planCommunity ).getActor();
            if ( actor != null ) participatingActors.add( actor );
        }

    }


    @XmlElement
    public String getDate() {
        return new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" ).format( new Date() );
    }

    @XmlElement( name = "plan" )
    public PlanIdentifierData getPlanIdentifier() {
        return planIdentifierData;
    }

    @XmlElement
    public String getDateVersioned() {
        return dateVersioned;
    }

    @XmlElement( name = "agentId" )
    public List<Long> getActorIds() {
        List<Long> actorIds = new ArrayList<Long>();
        for ( Actor actor : actors ) {
            actorIds.add( actor.getId() );
        }
        return actorIds;
    }

    private List<Actor> getActors( List<UserParticipation> participations ) {
        return participatingActors;
    }

    @XmlElement( name = "employment" )
    // Get given actor's or user's employments
    public List<EmploymentData> getEmployments() {
        return employments;
    }

    @XmlElement( name = "procedure" )
    public List<ProcedureData> getProcedures() {
        return procedures;
    }

    @XmlElement
    public EnvironmentData getEnvironment() {
        return environmentData;
    }

    @XmlElement
    public String getUserEmail() {
        return user.getEmail();
    }

    private Assignments getActorAssignments( Actor actor, PlanService planService ) {
        return planService.getAssignments().with( new ResourceSpec( actor ) );
    }

    public ChannelsUser getUser() {
        return user;
    }

    public List<Actor> getParticipatingActors() {
        return participatingActors;
    }

    public Set<ContactData> allContacts() {
        Set<ContactData> allContacts = new HashSet<ContactData>();
        for ( ProcedureData procedureData : getProcedures() ) {
            allContacts.addAll( procedureData.allContacts() );
        }
        return allContacts;
    }

    public Set<OrganizationData> allEmployers() {
        Set<OrganizationData> allEmployers = new HashSet<OrganizationData>();
        for ( ProcedureData procedureData : getProcedures() ) {
            allEmployers.add( procedureData.employer() );
        }
        return allEmployers;
    }

 }
