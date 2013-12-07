package com.mindalliance.channels.api.issues;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import org.apache.commons.lang.StringEscapeUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Web service data element for an issue.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/14/11
 * Time: 2:54 PM
 */
@XmlType( propOrder={ "about", "detected", "type", "kind", "waived", "description",
        "remediationOptions", "severity", "reportedBy"} )
public class IssueData  implements Serializable {

    private Issue issue;
    private ModelObject modelObject;
    private boolean waived;

    public IssueData() {
        // required
    }

    public IssueData( Issue issue, ModelObject modelObject, CommunityService communityService ) {
        this.issue = issue;
        this.modelObject = modelObject;
        initData( communityService );
    }

    private void initData( CommunityService communityService ) {
        waived = issue.isWaived( communityService );
    }

    @XmlElement
    public AboutData getAbout() {
        return new AboutData( modelObject );
    }

    @XmlElement
    public boolean getDetected() {
        return issue.isDetected();
    }

    @XmlElement
    public String getType() {
        return issue.getType();
    }

    @XmlElement
    public String getKind() {
        return issue.getKind();
    }

    @XmlElement
    public boolean getWaived() {
        return waived;
    }

    @XmlElement
    public String getDescription() {
        return StringEscapeUtils.escapeXml( issue.getDescription() );
    }

    @XmlElement( name = "remediation" )
    public List<String> getRemediationOptions() {
        List<String> options = new ArrayList<String>(  );
        for ( String option : issue.getRemediation().split( "\\n" ) ) {
            options.add( StringEscapeUtils.escapeXml( option.trim() ) );
        }
        return options;
    }

    @XmlElement
    public String getSeverity() {
        return issue.getSeverity().getLabel();
    }

    @XmlElement
    public String getReportedBy() {
        return StringEscapeUtils.escapeXml( issue.getReportedBy() );
    }
}
