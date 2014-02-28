package com.mindalliance.channels.api.plan;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Web Service data element for a a list of plan summaries.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/1/11
 * Time: 11:20 AM
 */
@XmlRootElement( name = "models", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder = {"date", "modelSummaries"} )
public class ModelSummariesData implements Serializable {

    private List<ModelSummaryData> modelSummaries;

    public ModelSummariesData() {
        // required
    }


    public ModelSummariesData( List<ModelSummaryData> modelSummaries ) {
        this.modelSummaries = modelSummaries;
    }

    @XmlElement
    public String getDate() {
        return new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" ).format( new Date() );
    }

    @XmlElement( name = "modelSummary" )
    public List<ModelSummaryData> getModelSummaries() {
        return modelSummaries;
    }
}
