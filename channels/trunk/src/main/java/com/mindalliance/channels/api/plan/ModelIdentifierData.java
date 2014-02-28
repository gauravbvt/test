package com.mindalliance.channels.api.plan;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.CollaborationModel;
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
public class ModelIdentifierData implements Serializable {

    private CollaborationModel collaborationModel;

    public ModelIdentifierData() {
        // required
    }

    public ModelIdentifierData( CommunityService communityService ) {
        collaborationModel = communityService.getPlan();
    }

    protected SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat( "MMM d yyyy HH:mm z" );
    }

    @XmlElement
    public String getUri() {
        return StringEscapeUtils.escapeXml( collaborationModel.getUri() );
    }

    @XmlElement
    public String getName() {
        return StringEscapeUtils.escapeXml( collaborationModel.getName() );
    }

    @XmlElement
    public int getVersion() {
        return collaborationModel.getVersion();
    }

    @XmlElement
    public String getRelease() {
        return collaborationModel.isDevelopment()
                ? "development"
                : collaborationModel.isProduction()
                ? "production"
                : "retired";
    }

    @XmlElement
    public String getDateVersioned() {
        return getDateFormat().format( collaborationModel.getWhenVersioned() );
    }

    public String getTimeNow() {
        return getDateFormat().format( new Date() );
    }
}
