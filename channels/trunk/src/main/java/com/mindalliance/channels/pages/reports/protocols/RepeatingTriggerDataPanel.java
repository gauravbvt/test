package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.CycleData;
import com.mindalliance.channels.api.procedures.TriggerData;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 8/22/14
 * Time: 10:54 AM
 */
public class RepeatingTriggerDataPanel extends AbstractDataPanel {
    private TriggerData triggerData;

    public RepeatingTriggerDataPanel( String id, TriggerData triggerData, ProtocolsFinder finder ) {
        super( id, finder );
        this.triggerData = triggerData;
        init();
    }

    private void init() {
        CycleData cycleData = triggerData.getRepeatCycle();
        Label headerLabel = new Label( "header", StringUtils.capitalize( cycleData.getLabel() ) );
        add( headerLabel );
    }
}
