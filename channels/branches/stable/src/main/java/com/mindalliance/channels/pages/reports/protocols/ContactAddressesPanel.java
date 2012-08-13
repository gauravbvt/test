package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.ChannelData;

import java.util.List;

/**
 * Contact addresses panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/26/12
 * Time: 10:58 PM
 */
public class ContactAddressesPanel extends AbstractContactAddressesPanel {


    public ContactAddressesPanel( String id, List<ChannelData> channelDataList, ProtocolsFinder finder ) {
        super( id, channelDataList, finder );
        init();
    }

}
