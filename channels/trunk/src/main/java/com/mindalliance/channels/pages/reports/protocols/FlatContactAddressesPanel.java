package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.ChannelData;

import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/28/12
 * Time: 5:36 PM
 */
public class FlatContactAddressesPanel extends AbstractContactAddressesPanel {

    public FlatContactAddressesPanel( String id, List<ChannelData> channelDataList, ProtocolsFinder finder ) {
        super( id, channelDataList, finder );
        init();
    }
}
