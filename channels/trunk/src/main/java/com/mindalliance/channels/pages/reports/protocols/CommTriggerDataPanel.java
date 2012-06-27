package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.procedures.ElementOfInformationData;
import com.mindalliance.channels.api.procedures.InformationData;
import com.mindalliance.channels.api.procedures.TriggerData;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.List;

/**
 * Request or notification trigget data panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/26/12
 * Time: 7:53 PM
 */
public class CommTriggerDataPanel extends AbstractTriggerDataPanel {
    public CommTriggerDataPanel( String id, TriggerData triggerData ) {
        super( id, triggerData );
        init();
    }

    private void init() {
        add( new Label(
                "header",
                getTriggerData().isOnNotificationFromOther()
                        ? "When notified of..."
                        : "When asked for..."
        ) );
        addInformation();
        addEois();
        addContacts();
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

    private void addContacts() {
        List<ContactData> contacts = getContacts();
        WebMarkupContainer contactsContainer = new WebMarkupContainer( "contactsContainer" );
        contactsContainer.setVisible( !contacts.isEmpty() );
        add( contactsContainer );
        ListView<ContactData> contactsListView = new ListView<ContactData>(
                "contacts",
                contacts
        ) {
            @Override
            protected void populateItem( ListItem<ContactData> item ) {
                item.add( new ContactLinkPanel( "contact", item.getModelObject() ) );
            }
        };
        contactsContainer.add( contactsListView );
    }

    private List<ContactData> getContacts() {
        return getTriggerData().isOnRequestFromOther()
                ? getTriggerData().getOnRequest().getContacts()
                : getTriggerData().getOnNotification().getContacts();
    }

    private InformationData getInformationData() {
        return getTriggerData().isOnRequestFromOther()
                ? getTriggerData().getOnRequest().getInformation()
                : getTriggerData().getOnNotification().getInformation();
    }

    private List<ElementOfInformationData> getEois() {
        return getInformationData().getEOIs();
    }
}
