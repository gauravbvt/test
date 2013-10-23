package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.TriggerData;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/26/12
 * Time: 7:54 PM
 */
public class SelfTriggerDataPanel extends AbstractSelfTriggerPanel {
    public SelfTriggerDataPanel( String id, TriggerData triggerData, ProtocolsFinder finder ) {
        super( id, triggerData, finder );
        init();
    }

    private void init() {
        addInformation();
        addEois();
    }

}
