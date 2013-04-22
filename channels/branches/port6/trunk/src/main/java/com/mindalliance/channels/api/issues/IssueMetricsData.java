package com.mindalliance.channels.api.issues;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.engine.analysis.IssueMetrics;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/12/13
 * Time: 5:55 PM
 */
@XmlType( propOrder={"waivedIssuesCount", "unwaivedIssuesCount", "allIssueTypeMetricsData", "allIssueKindMetricsData"})
public class IssueMetricsData implements Serializable {

    private IssueMetrics issueMetrics;

    public IssueMetricsData() {
    }

    public IssueMetricsData( CommunityService communityService ) {
        init( communityService );
    }

    private void init( CommunityService communityService ) {
        issueMetrics = new IssueMetrics( communityService );
    }

    @XmlElement
    public int getWaivedIssuesCount() {
        return issueMetrics.getAllWaivedIssuesCount();
    }

    @XmlElement
    public int getUnwaivedIssuesCount() {
        return issueMetrics.getAllUnwaivedIssuesCount();
    }

    @XmlElement( name = "issueTypeMetrics" )
    public List<IssueTypeMetricsData> getAllIssueTypeMetricsData() {
        List<IssueTypeMetricsData> list = new ArrayList<IssueTypeMetricsData>(  );
         for ( String type: Issue.TYPES ) {
             list.add( new IssueTypeMetricsData( type, issueMetrics.getIssueSummaryMetrics( type, true ) ) );
             list.add( new IssueTypeMetricsData( type, issueMetrics.getIssueSummaryMetrics( type, false ) ) );
         }
        return list;
    }

    @XmlElement( name = "issueKindMetrics" )
    public List<IssueKindMetricsData> getAllIssueKindMetricsData() {
        List<IssueKindMetricsData> list = new ArrayList<IssueKindMetricsData>(  );
        for ( String type: Issue.TYPES ) {
            IssueMetrics.IssueTypeMetrics issueTypeMetrics = issueMetrics.getIssueTypeMetrics( type );
            for ( String kind : issueTypeMetrics.getIssueKinds() ) {
                list.add( new IssueKindMetricsData( type, kind, issueTypeMetrics.getIssueTypeMetrics( kind )) );
            }
        }
        return list;
    }

}
