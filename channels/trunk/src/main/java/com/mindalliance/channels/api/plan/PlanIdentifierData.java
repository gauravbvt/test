package com.mindalliance.channels.api.plan;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Plan;
import org.apache.commons.lang.StringEscapeUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    /**
     * Simple date format.
     */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "MMM d yyyy HH:mm z" );

    private Plan plan;

    public PlanIdentifierData() {
        // required
    }

    public PlanIdentifierData( CommunityService communityService ) {
        plan = communityService.getPlan();
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
        return DATE_FORMAT.format( plan.getWhenVersioned() );
    }

    public String getTimeNow() {
        return DATE_FORMAT.format( new Date() );
    }
}
