package com.mindalliance.channels.api.plan;

import com.mindalliance.channels.core.model.Plan;
import org.apache.commons.lang.StringEscapeUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 * Web Service data element for the identity of a plan.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/1/11
 * Time: 11:28 AM
 */
@XmlType( propOrder = {"uri", "name", "version", "release", "dateVersioned"} )
public class PlanIdentifierData  implements Serializable {

    private Plan plan;

    public PlanIdentifierData() {
        // required
    }

    public PlanIdentifierData( Plan plan ) {
        this.plan = plan;
    }

    @XmlElement
    public String getUri() {
        return StringEscapeUtils.escapeXml( plan.getUri() );
    }

    @XmlElement
    public String getName() {
        return StringEscapeUtils.escapeXml( plan.getName() );
    }

    @XmlElement
    public int getVersion() {
        return plan.getVersion();
    }

    @XmlElement
    public String getRelease() {
        return plan.isDevelopment()
                ? "development"
                : plan.isProduction()
                ? "production"
                : "retired";
    }

    @XmlElement
    public String getDateVersioned() {
        return new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" ).format( plan.getWhenVersioned() );
    }

}
