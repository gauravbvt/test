package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.TriggerData;
import org.apache.wicket.markup.html.basic.Label;

/**
 * Observation trigger data panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/26/12
 * Time: 7:49 PM
 */
public class ObservationTriggerDataPanel extends AbstractTriggerDataPanel {

    public ObservationTriggerDataPanel( String id, TriggerData triggerData ) {
        super( id, triggerData );
        init();
    }

    private void init() {
        add( new Label( "situation", getTriggerData().getLabel() ) );
    }
}
