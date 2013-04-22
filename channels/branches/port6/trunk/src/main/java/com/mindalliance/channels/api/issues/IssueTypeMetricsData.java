package com.mindalliance.channels.api.issues;

import com.mindalliance.channels.engine.analysis.IssueMetrics;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/12/13
 * Time: 6:08 PM
 */
@XmlType( propOrder = {"type", "metricsData"})
public class IssueTypeMetricsData implements Serializable {

    private String type;
    private IssueMetrics.Metrics metrics;

    public IssueTypeMetricsData() {
    }

    public IssueTypeMetricsData( String type, IssueMetrics.Metrics metrics ) {
        this.type = type;
        this.metrics = metrics;
    }

    @XmlElement
    public String getType() {
        return type;
    }

    @XmlElement (name = "metrics")
    public MetricsData getMetricsData() {
        return new MetricsData( metrics );
    }

}
