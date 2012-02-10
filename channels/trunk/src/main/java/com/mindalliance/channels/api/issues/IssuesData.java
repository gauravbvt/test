package com.mindalliance.channels.api.issues;

import com.mindalliance.channels.api.plan.PlanSummaryData;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.engine.analysis.Analyst;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
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
@XmlType( propOrder = {"planSummary", "planMetrics", "issues"} )
public class IssuesData {

    private PlanService planService;
    private Analyst analyst;
    private ChannelsUserDao userDao;

    public IssuesData() {
    }

    public IssuesData( PlanService planService, Analyst analyst, ChannelsUserDao userDao ) {
        this.planService = planService;
        this.analyst = analyst;
    }

    @XmlElement( name = "plan" )
    public PlanSummaryData getPlanSummary( ) {
        return new PlanSummaryData( planService, userDao );
    }

    @XmlElement
    public PlanMetricsData getPlanMetrics() {
        return new PlanMetricsData( planService );
    }


    @XmlElement( name = "issue" )
    public List<IssueData> getIssues() {
        List<IssueData> issues = new ArrayList<IssueData>();
        for ( ModelObject mo : planService.list( ModelObject.class ) ) {
            for ( Issue issue : analyst.listIssues( planService, mo, true ) ) {
                issues.add( new IssueData( issue, mo ) );
            }
        }
        return issues;
    }

    private Plan getPlan() {
        return planService.getPlan();
    }

}
