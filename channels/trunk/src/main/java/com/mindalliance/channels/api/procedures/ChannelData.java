package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.query.QueryService;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/20/12
 * Time: 9:46 PM
 */
@XmlType( propOrder = {"mediumId", "medium", "address"} )
public class ChannelData  implements Serializable {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ChannelData.class );


    private long transmissionMediumId;
    private String address;
    private TransmissionMedium medium;

    public ChannelData() {
        // required
    }

    public ChannelData( Channel channel, QueryService queryService ) {
        transmissionMediumId = channel.getMedium().getId();
        address = channel.getAddress();
        init( queryService );
    }

    public ChannelData( long transmissionMediumId, String address, QueryService queryService ) {
        this.transmissionMediumId = transmissionMediumId;
        this.address = address;
        init( queryService );
    }

    private void init( QueryService queryService ) {
        try {
             medium = queryService.find( TransmissionMedium.class, transmissionMediumId );
        } catch ( NotFoundException e ) {
            LOG.warn( "Medium not found " + transmissionMediumId );
        }
    }

    @XmlElement
    public Long getMediumId() {
        return transmissionMediumId;
    }

    @XmlElement
    public String getMedium() {
        return medium == null ? null : medium.getName();
    }

    @XmlElement
    public String getAddress() {
        return StringEscapeUtils.escapeXml( address );
    }

    public String getLabel() {
        return "(" + getMedium() + ") " + getAddress();
    }
}
