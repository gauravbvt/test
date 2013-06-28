package com.mindalliance.channels.api.plan;

import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Plan;
import org.apache.commons.lang.StringEscapeUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Web service data element for a plan summary.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/12/11
 * Time: 1:36 PM
 */
@XmlRootElement( name = "planSummary", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder = {"planIdentifier", "dateVersioned", "description", "planners", "documentation"} )

public class PlanSummaryData implements Serializable {

    private List<UserData> planners;
    private DocumentationData documentation;
    private PlanIdentifierData planIdentifierData;
    private Plan plan;

    public PlanSummaryData() {
        // required
    }

    public PlanSummaryData( String serverUrl, CommunityService communityService ) {
        init(  serverUrl, communityService );
    }

    private void init(
            String serverUrl,
            CommunityService communityService ) {
        plan = communityService.getPlan();
        initPlanners( communityService );
        documentation = new DocumentationData( serverUrl, getPlan() );
        planIdentifierData = new PlanIdentifierData( communityService );
    }

    private void initPlanners( CommunityService communityService ) {
        planners = new ArrayList<UserData>();
        for ( ChannelsUser planner : communityService.getUserRecordService().getPlanners( getPlan().getUri() ) ) {
            planners.add( new UserData( planner, communityService ) );
        }

    }

    @XmlElement
    public PlanIdentifierData getPlanIdentifier() {
        return planIdentifierData;
    }


    @XmlElement
    public String getDateVersioned() {
        return new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" ).format( getPlan().getWhenVersioned() );
    }

    @XmlElement
    public String getDescription() {
        return StringEscapeUtils.escapeXml( getPlan().getDescription() );
    }

    @XmlElement( name = "planner" )
    public List<UserData> getPlanners() {
        return planners;
    }

    @XmlElement
    public DocumentationData getDocumentation() {
        return documentation;
    }

    private Plan getPlan() {
        return plan;
    }
}
