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
 * Time: 6:11 PM
 */
@XmlType( propOrder = {"type", "kind", "metricsData"})

public class IssueKindMetricsData implements Serializable {

    private String type;
    private String kind;
    private IssueMetrics.Metrics metrics;

    public IssueKindMetricsData() {
    }

    public IssueKindMetricsData( String type, String kind, IssueMetrics.Metrics metrics ) {
        this.type = type;
        this.kind = kind;
        this.metrics = metrics;
    }

    @XmlElement
    public String getType() {
        return type;
    }

    @XmlElement
    public String getKind() {
        return kind;
    }

    @XmlElement (name = "metrics")
    public MetricsData getMetricsData() {
        return new MetricsData( metrics );
    }

}
