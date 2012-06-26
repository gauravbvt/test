package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.query.QueryService;
import org.apache.commons.lang.StringEscapeUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/20/12
 * Time: 9:46 PM
 */
@XmlType( propOrder = {"mediumId", "medium", "address"} )
public class ChannelData {

    private Channel channel;
    private long transmissionMediumId;
    private String address;
    private QueryService queryService;

    public ChannelData() {
        // required
    }

    public ChannelData( Channel channel, QueryService queryService ) {
        this.queryService = queryService;
        transmissionMediumId = channel.getMedium().getId();
        address = channel.getAddress();
    }

    public ChannelData( long transmissionMediumId, String address, QueryService queryService ) {
        this.queryService = queryService;
        this.transmissionMediumId = transmissionMediumId;
        this.address = address;
    }

    @XmlElement
    public Long getMediumId() {
        return transmissionMediumId;
    }

    @XmlElement
    public String getMedium() {
        try {
            TransmissionMedium medium = queryService.find( TransmissionMedium.class, transmissionMediumId );
            return medium.getName();
        } catch ( NotFoundException e ) {
            return null;
        }
    }

    @XmlElement
    public String getAddress() {
        return StringEscapeUtils.escapeXml( address );
    }
}
