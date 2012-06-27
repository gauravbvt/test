package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.TriggerData;

/**
 * Abstract trigger data panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/26/12
 * Time: 10:01 AM
 */
public abstract class AbstractTriggerDataPanel extends AbstractDataPanel {

    private TriggerData triggerData;

    public AbstractTriggerDataPanel( String id, TriggerData triggerData ) {
        super( id );
        this.triggerData = triggerData;
    }

    protected TriggerData getTriggerData() {
        return triggerData;
    }

}
