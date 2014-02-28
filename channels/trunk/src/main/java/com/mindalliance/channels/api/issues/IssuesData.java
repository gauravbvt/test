package com.mindalliance.channels.api.issues;

import com.mindalliance.channels.api.plan.ModelSummaryData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.engine.analysis.Doctor;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Web service element for the issues in a (version of a) plan.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/14/11
 * Time: 2:34 PM
 */
@XmlRootElement( name = "issues", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder = {"modelSummary", "modelMetrics", "issueMetricsData", "issues"} )
public class IssuesData  implements Serializable {


    private ModelSummaryData modelSummaryData;
    private ModelMetricsData modelMetricsData;
    private IssueMetricsData issueMetricsData;
    private List<IssueData> issues;

    public IssuesData() {
    }

     public IssuesData( String serverUrl, CommunityService communityService ) {
        init( serverUrl, communityService );
    }

    private void init(
            String serverUrl,
            CommunityService communityService ) {
        modelSummaryData = new ModelSummaryData( serverUrl, communityService );
        modelMetricsData = new ModelMetricsData( communityService );
        issueMetricsData = new IssueMetricsData( communityService );
        initIssues( communityService );
    }

    private void initIssues( CommunityService communityService ) {
        issues = new ArrayList<IssueData>();
        Doctor doctor = communityService.getDoctor();
        for ( ModelObject mo : communityService.list( ModelObject.class ) ) {
            for ( Issue issue : doctor.listIssues( communityService, mo, true ) ) {
                issues.add( new IssueData( issue, mo, communityService ) );
            }
        }
    }


    @XmlElement( name = "model" )
    public ModelSummaryData getModelSummary() {
        return modelSummaryData;
    }

    @XmlElement
    public ModelMetricsData getModelMetrics() {
        return modelMetricsData;
    }

    @XmlElement
    public IssueMetricsData getIssueMetricsData() {
        return issueMetricsData;
    }


    @XmlElement( name = "issue" )
    public List<IssueData> getIssues() {
        return issues;
    }


}
