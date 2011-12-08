package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.api.entities.PlaceData;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Goal;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.query.PlanService;

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
@XmlType( propOrder = {"name", "category", "location", "teamMates", "goals"} )
public class TaskData  extends AbstractProcedureElementData {

    public TaskData() {
        // required
    }

    public TaskData( Assignment assignment, PlanService planService ) {
        super( assignment, planService );
    }

    @XmlElement
    public String getName() {
        return getAssignment().getName();
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
    @XmlElement( name = "teamMate" )
    public List<EmploymentData> getTeamMates() {
        List<EmploymentData> teamMates = new ArrayList<EmploymentData>(  );
         for ( Assignment assign : otherAssignments() ) {
             teamMates.add(  new EmploymentData( assign.getEmployment() ) );
         }
        return teamMates;
    }

    @XmlElement( name = "goal" )
    public List<GoalData> getGoals() {
        List<GoalData> goals = new ArrayList<GoalData>(  );
        for ( Goal goal : getPart().getGoals() ) {
            goals.add( new GoalData( goal ) );
        }
        return goals;
    }

    private List<Assignment> otherAssignments() {
        List<Assignment> otherAssignments = new ArrayList<Assignment>(  );
        for (Assignment assign : getPlanService().findAllAssignments( getAssignment().getPart(), false ) ) {
            if ( !assign.equals( getAssignment() ) ) {
                otherAssignments.add(  assign );
            }
        }
        return otherAssignments;
    }

    private Part getPart() {
        return getAssignment().getPart();
    }


}
