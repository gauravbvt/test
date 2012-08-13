package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.procedures.ChannelData;

import java.util.List;

/**
 * No bypassContact link panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/1/12
 * Time: 1:03 PM
 */
public class NoBypassContactLinkPanel extends AbstractContactLinkPanel {
    public NoBypassContactLinkPanel(
            String id,
            ContactData contactData,
            List<ChannelData> workChannels,
            List<ChannelData> personalChannels,
            ProtocolsFinder finder ) {
        super( id, contactData, workChannels, personalChannels, finder );
    }

    protected  void init() {
        super.init();
        // all done
    }
}
