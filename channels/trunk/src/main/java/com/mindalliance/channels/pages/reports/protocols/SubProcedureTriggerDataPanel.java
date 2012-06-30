package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.TriggerData;

/**
 * Sub-procedure trigger data panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/29/12
 * Time: 4:03 PM
 */
public class SubProcedureTriggerDataPanel extends AbstractSelfTriggerPanel  {

    public SubProcedureTriggerDataPanel( String id, TriggerData triggerData, ProtocolsFinder finder ) {
        super( id, triggerData, finder );
        init();
    }

    private void init() {
        addInformation();
        addEois();
    }


}
