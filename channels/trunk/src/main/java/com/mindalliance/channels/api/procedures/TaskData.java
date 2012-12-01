package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.entities.PlaceData;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Goal;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Subject;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.core.query.QueryService;
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
@XmlType( propOrder = {"id", "name", "category", "communicatedLocation", "location", "instructions", "teamContacts", "goals", "failureImpact", "documentation"} )
public class TaskData extends AbstractProcedureElementData {

    private String failureImpact;
    private List<Assignment> otherAssignments;
    private List<ContactData> teamContacts;
    private Level failureLevel;
    private Part part;
    private PlaceData placeData;
    private DocumentationData documentation;

    public TaskData() {
        // required
    }

    public TaskData(
            String serverUrl,
            Assignment assignment,
            PlanCommunity planCommunity,
            ChannelsUser user ) {
        super( planCommunity, assignment,  user );
        initData( planCommunity.getPlanService() );
        initLocation( serverUrl );
        initDocumentation( serverUrl );
        initOtherAssignments( planCommunity.getPlanService() );
        initTeamContacts( serverUrl, planCommunity );
    }


    public TaskData( String serverUrl, PlanCommunity planCommunity, Part part, ChannelsUser user ) {
        super( planCommunity, null,  user );
        this.part = part;
        initData( planCommunity.getPlanService() );
    }

    private void initData( PlanService planService ) {
        failureLevel = planService.computePartPriority( getPart() );
        failureImpact = StringEscapeUtils.escapeXml( failureLevel.getNegativeLabel() );
    }

    private void initTeamContacts( String serverUrl, PlanCommunity planCommunity ) {
        teamContacts = new ArrayList<ContactData>();
        if ( getAssignment() != null )
        for ( Employment employment : getTeamEmployments() ) {
            teamContacts.addAll( ContactData.findContactsFromEmployment(
                    serverUrl,
                    employment,
                    null,
                    planCommunity,
                    ChannelsUser.current().getUserInfo() ) );
        }
    }

    private void initOtherAssignments( QueryService queryService ) {
        otherAssignments = new ArrayList<Assignment>();
        Part part = getAssignment().getPart();
        if ( part.isAsTeam() ) {
            for ( Assignment assign : queryService.findAllAssignments( part, false ) ) {
                if ( !assign.equals( getAssignment() ) ) {
                    otherAssignments.add( assign );
                }
            }
        }
    }

    private void initLocation( String serverUrl ) {
        if ( getAssignment() != null ){
            Place location = getAssignment().getLocation();
            placeData = location != null
                    ? new PlaceData( serverUrl, location, getPlan() )
                    : null;
        }
    }

    private void initDocumentation( String serverUrl ) {
        documentation = new DocumentationData( serverUrl, getPart() );
    }

    @XmlElement
    public String getId() {
        return Long.toString( getPart().getId() );
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
       return placeData;
    }

    @XmlElement
    public String getInstructions() {
        String instructions = getPart().getDescription();
        return instructions == null
                ? null
                : StringEscapeUtils.escapeXml( instructions );
    }

    @XmlElement( name = "teamMate" )
    public List<ContactData> getTeamContacts() {
        return teamContacts;
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
        return failureImpact;
    }

    @XmlElement
    public DocumentationData getDocumentation() {
        return documentation;
    }

    private List<Assignment> otherTeamAssignments() {
        return otherAssignments;
    }

    private Part getPart() {
        return part != null ? part : getAssignment().getPart();
    }

    @WebMethod( exclude = true )
    public List<Employment> getTeamEmployments() {
        List<Employment> employments = new ArrayList<Employment>();
        for ( Assignment assignment : otherTeamAssignments() ) {
            employments.add( assignment.getEmployment() );
        }
        return employments;
    }

    @WebMethod( exclude = true )
    public Long getEventId() {
        return getPart().getSegment().getEvent().getId();
    }

    @WebMethod( exclude = true )
    public Long getPhaseId() {
        return getPart().getSegment().getPhase().getId();
    }

    public String getAnchor() {
        return "" + getPart().getId();
    }

    public String getLabel() {
        return getPart().getTask();
    }

    public Level getFailureSeverity() {
        return failureLevel;
    }

    @Override
    public boolean equals( Object object ) {
        return object instanceof TaskData
                && ( (TaskData) object ).getPart().equals( getPart() );
    }

    public int hashCode() {
        return getPart().hashCode();
    }

    @WebMethod( exclude = true )
    public Part part() {
        return getPart();
    }


}

