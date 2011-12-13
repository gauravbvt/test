package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.api.entities.PlaceData;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Goal;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.query.PlanService;

import javax.jws.WebMethod;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Web Service data element for a task assignment according to a plan.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/11
 * Time: 10:27 AM
 */
@XmlRootElement( name = "task", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder = {"name", "category", "location", "instructions", "teamMates", "goals", "failureImpact"} )
public class TaskData extends AbstractProcedureElementData {

    private Part part;

    public TaskData() {
        // required
    }

    public TaskData( Assignment assignment,PlanService planService ) {
        super( assignment, planService );
    }

    // For consuming tasks
    public TaskData( Part part, PlanService planService ) {
        super( planService );
        this.part = part;
    }

    @XmlElement
    public String getName() {
        return getPart().getTask();
    }

    @XmlElement
    public String getCategory() {
        return getPart().getCategory() != null
                ? getPart().getCategory().getLabel()
                : null;
    }

    @XmlElement
    public PlaceData getLocation() {
        Place location = getPart().getLocation();
        return location != null
                ? new PlaceData( location )
                : null;
    }

    @XmlElement
    public String getInstructions() {
        String instructions = getPart().getDescription();
        return instructions == null
                ? null
                : instructions;
    }

    @XmlElement( name = "teamMate" )
    public List<EmploymentData> getTeamMates() {
        if ( getAssignment() == null ) {
            return null;
        } else {
            List<EmploymentData> teamMates = new ArrayList<EmploymentData>();
            for ( Assignment assign : otherTeamAssignments() ) {
                teamMates.add( new EmploymentData( assign.getEmployment() ) );
            }
            return teamMates;
        }
    }

    @XmlElement( name = "goal" )
    public List<GoalData> getGoals() {
        List<GoalData> goals = new ArrayList<GoalData>();
        for ( Goal goal : getPart().getGoals() ) {
            goals.add( new GoalData( goal ) );
        }
        return goals;
    }

    @XmlElement
    public String getFailureImpact() {
         return getPlanService().computePartPriority( getPart() ).getNegativeLabel();
     }

    private List<Assignment> otherTeamAssignments() {
        List<Assignment> otherAssignments = new ArrayList<Assignment>();
        Part part = getAssignment().getPart();
        if ( part.isAsTeam() ) {
            for ( Assignment assign : getPlanService().findAllAssignments( part, false ) ) {
                if ( !assign.equals( getAssignment() ) ) {
                    otherAssignments.add( assign );
                }
            }
        }
        return otherAssignments;
    }

    private Part getPart() {
        return part == null
            ? getAssignment().getPart()
            : part;
    }

    @WebMethod( exclude = true )
    public Long getEventId() {
        return getPart().getSegment().getEvent().getId();
    }

    @WebMethod( exclude = true )
    public Long getPhaseId() {
        return getPart().getSegment().getPhase().getId();
    }

}
