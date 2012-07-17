package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.ObservationData;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.apache.wicket.markup.html.basic.Label;

/**
 * Observation trigger data panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/26/12
 * Time: 7:49 PM
 */
public class ObservationTriggerDataPanel extends AbstractDataPanel {

    private ObservationData observationData;

    public ObservationTriggerDataPanel( String id, ObservationData observationData, ProtocolsFinder finder ) {
        super( id, finder );
        this.observationData = observationData;
        init();
    }

    private void init() {
        add( new Label( "witnessing", ChannelsUtils.lcFirst( observationData.getObservationGerond() ) ) );
        add( new Label( "prefix", ChannelsUtils.lcFirst( observationData.getPrefix() ) ) );
        add( new Label( "scenario", ChannelsUtils.lcFirst( observationData.getScenarioLabel() ) ) );
    }


}
