package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.ElementOfInformationData;
import com.mindalliance.channels.api.procedures.InformationData;
import com.mindalliance.channels.api.procedures.TriggerData;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/26/12
 * Time: 7:54 PM
 */
public class SelfTriggerDataPanel extends AbstractTriggerDataPanel {
    public SelfTriggerDataPanel( String id, TriggerData triggerData ) {
        super( id, triggerData );
        init();
    }

    private void init() {
        add( new Label(
                "header",
                getTriggerData().isOnDiscovering()
                        ? "When  discovering by yourself..."
                        : "When asked for..."
        ) );
        addInformation();
        addEois();
    }

    private void addInformation() {
        add( new Label( "information", getInformationData().getName() ) );
    }

    private void addEois() {
        WebMarkupContainer eoisContainer = new WebMarkupContainer( "eoisContainer" );
        eoisContainer.setVisible( !getEois().isEmpty() );
        add( eoisContainer );
        ListView<ElementOfInformationData> eoisListView = new ListView<ElementOfInformationData>(
                "eois",
                getEois()
        ) {
            @Override
            protected void populateItem( ListItem<ElementOfInformationData> item ) {
                ElementOfInformationData eoiData = item.getModelObject();
                item.add( new Label( "content", eoiData.getName() ) );
            }
        };
        eoisContainer.add( eoisListView );
    }

    private InformationData getInformationData() {
        return getTriggerData().isOnDiscovering()
                ? getTriggerData().getOnDiscovery().getInformationDiscovered().getInformation()
                : getTriggerData().getOnResearch().getInformation();
    }

    private List<ElementOfInformationData> getEois() {
        return getInformationData().getEOIs();
    }

}
