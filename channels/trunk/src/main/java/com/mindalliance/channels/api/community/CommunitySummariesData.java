package com.mindalliance.channels.api.community;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Web Service data element for a a list of community summaries.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/4/13
 * Time: 9:30 AM
 */
@XmlRootElement( name="communities",  namespace = "http://mind-alliance.com/api/isp/v1/")
@XmlType ( propOrder={"date", "communitySummaries"} )
public class CommunitySummariesData implements Serializable {

    private List<CommunitySummaryData> communitySummaries;

    public CommunitySummariesData() {
        // required
    }

    public CommunitySummariesData( List<CommunitySummaryData> communitySummaries ) {
        this.communitySummaries = communitySummaries;
    }

    @XmlElement
    public String getDate() {
        return new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" ).format( new Date() );
    }

    @XmlElement( name = "communitySummary" )
    public List<CommunitySummaryData> getCommunitySummaries() {
        return communitySummaries;
    }
}
