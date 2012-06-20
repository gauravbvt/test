package com.mindalliance.channels.api.issues;

import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import org.apache.commons.lang.StringEscapeUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Web service data element for an issue.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/14/11
 * Time: 2:54 PM
 */
@XmlType( propOrder={ "about", "detected", "type", "kind", "waived", "description",
        "remediation", "severity", "reportedBy"} )
public class IssueData {

    private Issue issue;
    private ModelObject modelObject;

    public IssueData() {
        // required
    }

    public IssueData( Issue issue, ModelObject modelObject ) {
        this.issue = issue;
        this.modelObject = modelObject;
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
        return issue.isWaived();
    }

    @XmlElement
    public String getDescription() {
        return StringEscapeUtils.escapeXml( issue.getDescription() );
    }

    @XmlElement
    public String getRemediation() {
        return StringEscapeUtils.escapeXml( issue.getRemediation() );
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
