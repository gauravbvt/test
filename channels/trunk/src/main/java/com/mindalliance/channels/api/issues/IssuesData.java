package com.mindalliance.channels.api.issues;

import com.mindalliance.channels.api.plan.PlanSummaryData;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.engine.analysis.Analyst;

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
@XmlType( propOrder = {"planSummary", "planMetrics", "issues"} )
public class IssuesData  implements Serializable {


    private PlanSummaryData planSummaryData;
    private PlanMetricsData planMetricsData;
    private List<IssueData> issues;

    public IssuesData() {
    }

    public IssuesData(
            PlanService planService,
            Analyst analyst,
            ChannelsUserDao userDao,
            PlanParticipationService planParticipationService ) {
        init( planService, analyst, userDao, planParticipationService );
    }

    private void init(
            PlanService planService,
            Analyst analyst,
            ChannelsUserDao userDao,
            PlanParticipationService planParticipationService ) {
        planSummaryData = new PlanSummaryData( planService, userDao, planParticipationService );
        planMetricsData = new PlanMetricsData( planService );
        initIssues( planService, analyst );
    }

    private void initIssues( PlanService planService, Analyst analyst ) {
        issues = new ArrayList<IssueData>();
        for ( ModelObject mo : planService.list( ModelObject.class ) ) {
            for ( Issue issue : analyst.listIssues( planService, mo, true ) ) {
                issues.add( new IssueData( issue, mo ) );
            }
        }
    }


    @XmlElement( name = "plan" )
    public PlanSummaryData getPlanSummary( ) {
        return planSummaryData;
    }

    @XmlElement
    public PlanMetricsData getPlanMetrics() {
        return planMetricsData;
    }


    @XmlElement( name = "issue" )
    public List<IssueData> getIssues() {
        return issues;
    }


}
