package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.AssetConnectionData;
import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.entities.FunctionData;
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
import com.mindalliance.channels.core.model.asset.AssetConnection;
import com.mindalliance.channels.core.model.time.Cycle;
import com.mindalliance.channels.core.query.ModelService;
import org.apache.commons.lang.StringEscapeUtils;

import javax.jws.WebMethod;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Web Service data element for a task assignment according to a plan.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/11
 * Time: 10:27 AM
 */
@XmlType(propOrder = {"id", "name", "category", "communicatedLocation", "cycle", "location", "function", "assetConnectionsSummary", "instructions",
        "teamContacts", "goals", "failureImpact", "assetConnections", "documentation"})
public class TaskData extends AbstractProcedureElementData {

    private FunctionData functionData;
    private String failureImpact;
    private List<CommunityAssignment> otherAssignments;
    private List<ContactData> teamContacts;
    private Level failureLevel;
    private Part part;
    private PlaceData placeData;
    private CycleData cycleData;
    private List<AssetConnectionData> assetConnectionDataList;
    private String assetConnectionsSummary;
    private DocumentationData documentation;

    public TaskData() {
        // required
    }

    public TaskData( String serverUrl,
                     CommunityAssignment assignment,
                     CommunityService communityService,
                     ChannelsUser user ) {
        super( communityService, assignment, user );
        part = assignment.getPart();
        initFunction( serverUrl, communityService );
        initData( communityService.getModelService() );
        initLocation( serverUrl, communityService );
        initDocumentation( serverUrl );
        initOtherAssignments( communityService );
        initTeamContacts( serverUrl, communityService );
        initAssetConnections();
        initCycle();
    }

    public TaskData( String serverUrl,
                     CommunityService communityService,
                     Part part,
                     ChannelsUser user ) {
        super( communityService, null, user );
        assert part != null;
        this.part = part;
        initData( communityService.getModelService() );
        initAssetConnections();
    }

    private void initData( ModelService modelService ) {
        assetConnectionsSummary = getPart().getAssetConnections().getFirstPersonLabel();
        failureLevel = modelService.computePartPriority( getPart() );
        failureImpact = StringEscapeUtils.escapeXml( failureLevel.getNegativeLabel() );
    }

    private void initFunction( String serverUrl, CommunityService communityService ) {
        if ( getPart().getFunction() != null )
            functionData = new FunctionData( serverUrl, getPart().getFunction(), communityService );
    }

    private void initTeamContacts( String serverUrl, CommunityService communityService ) {
        teamContacts = new ArrayList<ContactData>();
        if ( getAssignment() != null )
            for ( CommunityEmployment employment : getTeamEmployments() ) {
                teamContacts.addAll( ContactData.findContactsFromEmploymentAndCommitment(
                        serverUrl,
                        employment,
                        null,
                        communityService,
                        ChannelsUser.current().getUserRecord() ) );
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
        if ( getAssignment() != null ) {
            Place location = communityService.resolveLocation( getAssignment().getLocation( communityService ) );
            placeData = location != null
                    ? new PlaceData( serverUrl, location, communityService )
                    : null;
        }
    }

    private void initAssetConnections() {
        assetConnectionDataList = new ArrayList<AssetConnectionData>();
        for ( AssetConnection assetConnection : part.getAssetConnections() ) {
            assetConnectionDataList.add( new AssetConnectionData( assetConnection ) );
        }
    }

    private void initDocumentation( String serverUrl ) {
        documentation = new DocumentationData( serverUrl, getPart() );
    }

    private void initCycle() {
        Cycle cycle = getPart().getCycle();
        if ( cycle != null )
            cycleData = new CycleData( cycle );
    }

    @XmlElement
    public String getId() {
        return Long.toString( getPart().getId() );
    }

    @XmlElement
    public String getName() {
        return StringEscapeUtils.escapeXml( getPart().getTaskLabel() );
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
    public String getAssetConnectionsSummary() {
        return assetConnectionsSummary;
    }

    @XmlElement
    public FunctionData getFunction() {
        return functionData;
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

    @XmlElement(name = "teamMate")
    public List<ContactData> getTeamContacts() {
        return teamContacts;
    }


    @XmlElement(name = "goal")
    public List<GoalData> getGoals() {
        List<GoalData> goals = new ArrayList<GoalData>();
        for ( Goal goal : getPart().getGoals() ) {
            goals.add( new GoalData( goal ) );
        }
        return goals;
    }

    @XmlElement
    public CycleData getCycle() {
        return cycleData;
    }

    @XmlElement
    public String getFailureImpact() {
        return failureImpact;
    }

    @XmlElement
    public DocumentationData getDocumentation() {
        return documentation;
    }

    @XmlElement(name = "assetConnection")
    public List<AssetConnectionData> getAssetConnections() {
        return assetConnectionDataList;
    }

    private List<CommunityAssignment> otherTeamAssignments() {
        return otherAssignments;
    }

    private Part getPart() {
        return part != null ? part : getAssignment().getPart();
    }

    @WebMethod(exclude = true)
    public List<CommunityEmployment> getTeamEmployments() {
        List<CommunityEmployment> employments = new ArrayList<CommunityEmployment>();
        for ( CommunityAssignment assignment : otherTeamAssignments() ) {
            employments.add( assignment.getCommunityEmployment() );
        }
        return employments;
    }

    @WebMethod(exclude = true)
    public Long getEventId() {
        return getPart().getSegment().getEvent().getId();
    }

    @WebMethod(exclude = true)
    public Long getPhaseId() {
        return getPart().getSegment().getPhase().getId();
    }

    public String getAnchor() {
        return "" + getPart().getId();
    }

    public String getLabel() {
        return getPart().getTaskLabel();
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

    @WebMethod(exclude = true)
    public Part part() {
        return getPart();
    }


    public Set<Long> allPlaceIds() {
        Set<Long> ids = new HashSet<Long>();
        if ( getLocation() != null ) {
            ids.add( getLocation().getId() );
        }
        return ids;
    }

    public Set<Long> allFunctionIds() {
        Set<Long> ids = new HashSet<Long>();
        if ( getPart().getFunction() != null )
            ids.add( getPart().getFunction().getId() );
        return ids;
    }

    public Set<Long> allInfoProductIds() {
        Set<Long> ids = new HashSet<Long>();
        if ( functionData != null )
            ids.addAll( functionData.allInfoProductIds() );
        return ids;
    }

    public Set<Long> allAssetIds() {
        Set<Long> ids = new HashSet<Long>();
        if ( functionData != null )
            ids.addAll( functionData.allAssetIds() );
        for ( AssetConnectionData assetConnectionData : assetConnectionDataList ) {
            ids.add( assetConnectionData.getAssetId() );
        }
        return ids;
    }
}

