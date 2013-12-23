package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.entities.AvailabilityData;
import com.mindalliance.channels.api.entities.TimePeriodData;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

/**
 * Availability data panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/28/12
 * Time: 10:29 AM
 */
public class AvailabilityDataPanel extends AbstractDataPanel {

    private AvailabilityData availability;

    public AvailabilityDataPanel( String id, AvailabilityData availability, ProtocolsFinder finder ) {
        super( id, finder );
        this.availability = availability;
        init();
    }

    private void init() {
       /* ListView<TimePeriodData> timePeriodListView = new ListView<TimePeriodData>(
                "timePeriods",
                availability.getTimePeriods()
        ) {
            @Override
            protected void populateItem( ListItem<TimePeriodData> item ) {
                TimePeriodData timePeriodData = item.getModelObject();
                item.add( new Label("timePeriod", timePeriodData.getLabel( item.getIndex() )) );
            }
        };
        add(  timePeriodListView );*/
    }
}
