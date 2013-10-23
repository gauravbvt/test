package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.ElementOfInformationData;
import com.mindalliance.channels.api.procedures.TriggerData;
import com.mindalliance.channels.core.util.ChannelsUtils;

import java.util.Iterator;
import java.util.List;

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

    public AbstractTriggerDataPanel( String id, TriggerData triggerData, ProtocolsFinder finder ) {
        super( id, finder );
        this.triggerData = triggerData;
    }

    protected TriggerData getTriggerData() {
        return triggerData;
    }

    protected String asCSVs( List<ElementOfInformationData> eois ) {
        StringBuilder sb = new StringBuilder(  );
        Iterator<ElementOfInformationData> iter = eois.iterator();
        while( iter.hasNext() ) {
            sb.append( ChannelsUtils.smartUncapitalize( iter.next().getName() ) );
            if ( iter.hasNext() ) sb.append( ", " );
        }
        return sb.toString();
    }


}
