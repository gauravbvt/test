package com.mindalliance.channels.api.plan;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Web Service data element for a a list of plan summaries.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/1/11
 * Time: 11:20 AM
 */
@XmlRootElement( name = "planSummary", namespace = "http://mind-alliance.com/api/isp/v1/" )
public class PlanSummariesData {

    private List<PlanSummaryData> planSummaries;

    public PlanSummariesData() {
        // required
    }


    public PlanSummariesData( List<PlanSummaryData> planSummaries ) {
        this.planSummaries = planSummaries;
    }

    @XmlElement( name = "planSummary" )
    public List<PlanSummaryData> getPlanSummaries() {
        return planSummaries;
    }
}
