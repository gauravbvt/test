package com.mindalliance.channels.api.community;

import com.mindalliance.channels.api.entities.OrganizationData;
import com.mindalliance.channels.api.plan.UserData;
import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.db.data.communities.OrganizationParticipation;
import com.mindalliance.channels.db.data.communities.UserParticipation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Web service data element for a community summary.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/4/13
 * Time: 9:31 AM
 */
@XmlRootElement( name="communitySummary", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder={"communityIdentifier", "dateCreated", "description", "planners", "documentation",
        "placeholderOrganizations", "fixedOrganizations", "agencies", "agencyParticipationList", "userParticipationList"})
public class CommunitySummaryData implements Serializable {

    private Date creationDate;
    private String description;
    private List<UserData> planners;
    private DocumentationData documentation;
    private CommunityIdentifierData communityIdentifier;
    private List<OrganizationData> placeholderOrganizations;
    private List<OrganizationData> fixedOrganizations;
    private List<AgencyData> agencies;
    private List<AgencyParticipationData> agencyParticipationList;
    private List<UserParticipationData> userParticipationList;

    public CommunitySummaryData() {
        // required
    }

    public CommunitySummaryData( String serverUrl, CommunityService communityService ) {
        PlanCommunity planCommunity = communityService.getPlanCommunity();
        creationDate = planCommunity.getDateCreated();
        description = planCommunity.getDescription();
        documentation = new DocumentationData( serverUrl, planCommunity );
        communityIdentifier = new CommunityIdentifierData( serverUrl, communityService );
        initPlanners( communityService );
        initFixedOrganizations( serverUrl, communityService );
        initPlaceholders( serverUrl, communityService );
        initAgencies( serverUrl, communityService );
        initAgencyParticipation( serverUrl, communityService );
        initUserParticipation( serverUrl, communityService );
    }

    private void initPlanners( CommunityService communityService ) {
        planners = new ArrayList<UserData>();
        for ( ChannelsUser planner : communityService.getCommunityPlanners() ) {
            planners.add( new UserData( planner, communityService ) );
        }
    }

    private void initPlaceholders( String serverUrl, CommunityService communityService ) {
        placeholderOrganizations = new ArrayList<OrganizationData>(  );
        for ( Organization placeholder : communityService.getPlanService().listPlaceholderOrganizations() ) {
            placeholderOrganizations.add( new OrganizationData( serverUrl, placeholder, communityService ) );
        }
    }

    private void initFixedOrganizations( String serverUrl, CommunityService communityService ) {
        fixedOrganizations = new ArrayList<OrganizationData>(  );
        for ( Organization placeholder : communityService.getPlanService().listFixedOrganizations() ) {
            fixedOrganizations.add( new OrganizationData( serverUrl, placeholder, communityService ) );
        }
    }

    private void initAgencies( String serverUrl, CommunityService communityService ) {
        agencies = new ArrayList<AgencyData>(  );
        for ( Agency agency : communityService.getParticipationManager().getAllKnownAgencies( communityService ) ) {
            agencies.add( new AgencyData( serverUrl, agency, communityService ) );
        }
    }

    private void initAgencyParticipation( String serverUrl, CommunityService communityService ) {
        agencyParticipationList = new ArrayList<AgencyParticipationData>(  );
        for ( OrganizationParticipation orgParticipation :
                communityService.getOrganizationParticipationService().
                        getAllOrganizationParticipations( communityService ) ) {
             agencyParticipationList.add( new AgencyParticipationData( serverUrl, orgParticipation, communityService ) );
        }
    }

    private void initUserParticipation( String serverUrl, CommunityService communityService ) {
        userParticipationList = new ArrayList<UserParticipationData>(  );
        for ( UserParticipation userParticipation :
                communityService.getUserParticipationService().getAllParticipations( communityService )) {
                userParticipationList.add( new UserParticipationData(  serverUrl, userParticipation, communityService ) );
        }
    }

    @XmlElement
    public String getDateCreated() {
       return new SimpleDateFormat( "MMM d yyyy HH:mm z" ).format( creationDate );
    }

    @XmlElement
    public String getDescription() {
        return description;
    }

    @XmlElement( name = "agency")
    public List<AgencyData> getAgencies() {
        return agencies;
    }

    @XmlElement( name = "agencyParticipation" )
    public List<AgencyParticipationData> getAgencyParticipationList() {
        return agencyParticipationList;
    }

    @XmlElement
    public CommunityIdentifierData getCommunityIdentifier() {
        return communityIdentifier;
    }

    @XmlElement
    public DocumentationData getDocumentation() {
        return documentation;
    }

    @XmlElement( name = "placeholderOrganization" )
    public List<OrganizationData> getPlaceholderOrganizations() {
        return placeholderOrganizations;
    }

    @XmlElement( name = "fixedOrganization" )
    public List<OrganizationData> getFixedOrganizations() {
        return fixedOrganizations;
    }

    @XmlElement( name =  "planner" )
    public List<UserData> getPlanners() {
        return planners;
    }

    @XmlElement( name = "userParticipation" )
    public List<UserParticipationData> getUserParticipationList() {
        return userParticipationList;
    }
}
