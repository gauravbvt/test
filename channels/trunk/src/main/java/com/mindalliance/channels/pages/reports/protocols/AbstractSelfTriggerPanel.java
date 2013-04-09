package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.ElementOfInformationData;
import com.mindalliance.channels.api.procedures.InformationData;
import com.mindalliance.channels.api.procedures.TriggerData;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.List;

/**
 * OBSOLETE
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/29/12
 * Time: 4:01 PM
 */
public class AbstractSelfTriggerPanel extends AbstractTriggerDataPanel {

    public AbstractSelfTriggerPanel( String id, TriggerData triggerData, ProtocolsFinder finder ) {
        super( id, triggerData, finder );
    }

    protected void addInformation() {
        add( new Label(
                "header",
                "OBSOLETE"
        ) );
        add( new Label( "information", getInformationData().getName() ) );
    }

    protected void addEois() {
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

    protected InformationData getInformationData() {
        return null; // OBSOLETE
    }

    protected List<ElementOfInformationData> getEois() {
        return getInformationData().getEOIs();
    }

}
