package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.entities.PlaceData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.protocols.CommunityAssignment;
import com.mindalliance.channels.core.community.protocols.CommunityEmployment;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Goal;
import com.mindalliance.channels.core.model.Level;
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
@XmlType( propOrder = {"id", "name", "category", "communicatedLocation", "location", "instructions",
        "teamContacts", "goals", "failureImpact", "documentation"} )
public class TaskData extends AbstractProcedureElementData {

    private String failureImpact;
    private List<CommunityAssignment> otherAssignments;
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
            CommunityAssignment assignment,
            CommunityService communityService,
            ChannelsUser user ) {
        super( communityService, assignment,  user );
        initData( communityService.getPlanService() );
        initLocation( serverUrl, communityService );
        initDocumentation( serverUrl );
        initOtherAssignments( communityService );
        initTeamContacts( serverUrl, communityService );
    }


    public TaskData( String serverUrl, CommunityService communityService, Part part, ChannelsUser user ) {
        super( communityService, null,  user );
        this.part = part;
        initData( communityService.getPlanService() );
    }

    private void initData( PlanService planService ) {
        failureLevel = planService.computePartPriority( getPart() );
        failureImpact = StringEscapeUtils.escapeXml( failureLevel.getNegativeLabel() );
    }

    private void initTeamContacts( String serverUrl, CommunityService communityService ) {
        teamContacts = new ArrayList<ContactData>();
        if ( getAssignment() != null )
        for ( CommunityEmployment employment : getTeamEmployments() ) {
            teamContacts.addAll( ContactData.findContactsFromEmployment(
                    serverUrl,
                    employment,
                    null,
                    communityService,
                    ChannelsUser.current().getUserInfo() ) );
        }
    }

    private void initOtherAssignments( CommunityService communityService ) {
        otherAssignments = new ArrayList<CommunityAssignment>();
        Part part = getAssignment().getPart();
        if ( part.isAsTeam() ) {
            for ( CommunityAssignment assign : communityService.getAllAssignments().assignedTo( part ) ) {
                if ( !assign.equals( getAssignment() ) ) {
                    otherAssignments.add( assign );
                }
            }
        }
    }

    private void initLocation( String serverUrl, CommunityService communityService ) {
        if ( getAssignment() != null ){
            Place location = getAssignment().getLocation( communityService );
            placeData = location != null
                    ? new PlaceData( serverUrl, location, communityService )
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

    private List<CommunityAssignment> otherTeamAssignments() {
        return otherAssignments;
    }

    private Part getPart() {
        return part != null ? part : getAssignment().getPart();
    }

    @WebMethod( exclude = true )
    public List<CommunityEmployment> getTeamEmployments() {
        List<CommunityEmployment> employments = new ArrayList<CommunityEmployment>();
        for ( CommunityAssignment assignment : otherTeamAssignments() ) {
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

