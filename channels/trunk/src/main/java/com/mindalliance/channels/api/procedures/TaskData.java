package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.api.entities.PlaceData;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Goal;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Subject;
import com.mindalliance.channels.core.query.PlanService;
import org.apache.commons.lang.StringEscapeUtils;

import javax.jws.WebMethod;
import javax.xml.bind.annotation.XmlElement;
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
@XmlType( propOrder = {"name", "category", "communicatedLocation", "location", "instructions", "teamMates", "goals", "failureImpact", "documentation"} )
public class TaskData extends AbstractProcedureElementData {

    private Part part;

    public TaskData() {
        // required
    }

    public TaskData( 
            Assignment assignment, 
            PlanService planService, 
            PlanParticipationService planParticipationService,
            ChannelsUser user ) {
        super( assignment, planService, planParticipationService, user );
    }

    // For consuming tasks
    public TaskData( 
            Part part,
            PlanService planService,
            PlanParticipationService planParticipationService,
            ChannelsUser user ) {
        super( planService, planParticipationService, user );
        this.part = part;
    }

    @XmlElement
    public String getName() {
        return StringEscapeUtils.escapeXml( getPart().getTask() );
    }

    @XmlElement
    public String getCategory() {
        return getPart().getCategory() != null
                ? getPart().getCategory().getLabel()
                : null;
    }

    @XmlElement
    public SubjectData getCommunicatedLocation() {
        if ( getAssignment() == null ) return null;
        else {
            Subject subject = getAssignment().getCommunicatedLocation();
            return subject != null
                    ? new SubjectData( subject )
                    : null;
        }
    }

    @XmlElement
    public PlaceData getLocation() {
        if ( getAssignment() == null ) return null;
        else {
            Place location = getAssignment().getLocation();
            return location != null
                    ? new PlaceData( location, getPlan() )
                    : null;
        }
    }

    @XmlElement
    public String getInstructions() {
        String instructions = getPart().getDescription();
        return instructions == null
                ? null
                : StringEscapeUtils.escapeXml( instructions );
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
        return StringEscapeUtils.escapeXml( getPlanService().computePartPriority( getPart() ).getNegativeLabel() );
    }

    @XmlElement
    public DocumentationData getDocumentation() {
        return new DocumentationData( getPart() );
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
