package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.entities.AgentData;
import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.api.plan.PlanIdentifierData;
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
import java.util.List;

/**
 * Web Service data element for the procedures of an actor according to a plan.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/5/11
 * Time: 12:25 PM
 */
@XmlRootElement( name = "procedures", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder = {"date", "planIdentifier", "actor", "employments", "procedures", "environment"} )
public class ProceduresData {

    private Plan plan;
    private Actor actor;
    private PlanService planService;
    private Assignments assignments;
    private List<ProcedureData> procedures;
    private List<EmploymentData> employments;


    public ProceduresData() {
        // required
    }

    public ProceduresData( Plan plan, Actor actor, PlanService planService ) {
        this.plan = plan;
        this.actor = actor;
        this.planService = planService;
    }

    @XmlElement
    public String getDate() {
        return new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" ).format( new Date() );
    }

    @XmlElement( name = "plan" )
    public PlanIdentifierData getPlanIdentifier() {
        return new PlanIdentifierData( plan );
    }

    @XmlElement( name = "agent" )
    public AgentData getActor() {
        return new AgentData( actor, plan );
    }

    @XmlElement( name = "employment" )
    public List<EmploymentData> getEmployments() {
        if ( employments == null ) {
            employments = new ArrayList<EmploymentData>();
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
            for ( Assignment assignment : getActorAssignments() ) {
                procedures.add( new ProcedureData(
                        assignment,
                        allCommitments.benefiting( assignment ),
                        allCommitments.committing( assignment ),
                        planService ) );
            }
        }
        return procedures;
    }

    @XmlElement
    public EnvironmentData getEnvironment() {
        return new EnvironmentData( this, planService );
    }

    private Assignments getActorAssignments() {
        if ( assignments == null ) {
            assignments = planService.getAssignments().with( new ResourceSpec( actor ) );
        }
        return assignments;
    }

}
