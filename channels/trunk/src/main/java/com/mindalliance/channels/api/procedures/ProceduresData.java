package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.api.plan.PlanIdentifierData;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.PlanParticipation;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.core.query.Assignments;
import com.mindalliance.channels.core.query.Commitments;
import com.mindalliance.channels.core.query.PlanService;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
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
@XmlType( propOrder = {"date", "planIdentifier", "dateVersioned", "actorIds", "employments", "procedures", "environment"} )
public class ProceduresData {

    private Plan plan;
    private List<Actor> actors;
    private PlanService planService;
    private PlanParticipationService planParticipationService;
    private ChannelsUser user;
    private Assignments assignments;
    private List<ProcedureData> procedures;
    private List<EmploymentData> employments;


    public ProceduresData() {
        // required
    }

    public ProceduresData(
            Plan plan,
            List<PlanParticipation> participations,
            PlanService planService,
            PlanParticipationService planParticipationService,
            ChannelsUser user ) {
        this.plan = plan;
        this.planService = planService;
        this.planParticipationService = planParticipationService;
        this.user = user;
        this.actors = getActors( participations );
    }

    public ProceduresData(
            Plan plan,
            Actor actor,
            PlanService planService,
            PlanParticipationService planParticipationService ) {
        this.plan = plan;
        this.planService = planService;
        this.planParticipationService = planParticipationService;
        this.actors = new ArrayList<Actor>();
        actors.add( actor );
    }


    @XmlElement
    public String getDate() {
        return new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" ).format( new Date() );
    }

    @XmlElement( name = "plan" )
    public PlanIdentifierData getPlanIdentifier() {
        return new PlanIdentifierData( plan );
    }

    @XmlElement
    public String getDateVersioned() {
        return new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" ).format( plan.getWhenVersioned() );
    }

    @XmlElement( name = "agentId" )
    public List<Long> getActorIds() {
        List<Long> actorIds = new ArrayList<Long>();
        for ( Actor actor : actors ) {
            actorIds.add( actor.getId() );
        }
        return actorIds;
    }

    private List<Actor> getActors( List<PlanParticipation> participations ) {
        List<Actor> participatingActors = new ArrayList<Actor>();
        for ( PlanParticipation participation : participations ) {
            Actor actor = participation.getActor( planService );
            if ( actor != null ) participatingActors.add( actor );
        }
        return participatingActors;
    }

    @XmlElement( name = "employment" )
    public List<EmploymentData> getEmployments() {
        if ( employments == null ) {
            employments = new ArrayList<EmploymentData>();
            for ( Actor actor : actors )
                for ( Employment employment : planService.findAllEmploymentsForActor( actor ) ) {
                    employments.add( new EmploymentData( employment ) );
                }
        }
        return employments;
    }

    @XmlElement( name = "procedure" )
    public List<ProcedureData> getProcedures() {
        if ( procedures == null ) {
            procedures = new ArrayList<ProcedureData>();
            Commitments allCommitments = planService.getAllCommitments( true, false );
            Set<Assignment> assignments = new HashSet<Assignment>();
            for ( Actor actor : actors ) {
                for ( Assignment assignment : getActorAssignments( actor ) ) {
                    assignments.add( assignment );
                }
            }
            for ( Assignment assignment : assignments ) {
                procedures.add( new ProcedureData(
                        assignment,
                        allCommitments.benefiting( assignment ),
                        allCommitments.committing( assignment ),
                        planService,
                        planParticipationService,
                        user ) );
            }
        }
        return procedures;
    }

    @XmlElement
    public EnvironmentData getEnvironment() {
        return new EnvironmentData( this, planService );
    }

    private Assignments getActorAssignments( Actor actor ) {
        if ( assignments == null ) {
            assignments = planService.getAssignments().with( new ResourceSpec( actor ) );
        }
        return assignments;
    }

}
