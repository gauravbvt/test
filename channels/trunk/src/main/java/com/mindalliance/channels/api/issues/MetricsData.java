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
 * Time: 6:16 PM
 */
@XmlType( propOrder={"count", "percent", "kindsCount", "waived" })
public class MetricsData implements Serializable {

    private IssueMetrics.Metrics metrics;

    public MetricsData() {
    }

    public MetricsData( IssueMetrics.Metrics metrics ) {
        this.metrics = metrics;
    }

    @XmlElement
    public int getCount() {
        return metrics.getCount();
    }

    @XmlElement
    public double getPercent() {
        return metrics.getPercent();
    }

    @XmlElement
    public int getKindsCount() {
        return metrics.getKindsCount();
    }

    @XmlElement
    public boolean getWaived() {
        return metrics.isWaived();
    }

}
