package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.ElementOfInformationData;
import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.procedures.SharedInformationData;
import com.mindalliance.channels.api.procedures.SituationData;
import com.mindalliance.channels.api.procedures.TriggerData;
import com.mindalliance.channels.core.util.ChannelsUtils;
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
    public CommTriggerDataPanel( String id, TriggerData triggerData, ProtocolsFinder finder ) {
        super( id, triggerData, finder );
        init();
    }

    private void init() {
        add( new Label(
                "header",
                getTriggerData().isOnNotificationFromOther()
                        ? "When I am notified of"
                        : "When I am asked for"
        ) );
        addIntent();
        addInformation();
        addCommunicatedContext();
        addEois();
        addContacts();
    }

    private void addCommunicatedContext() {
        SituationData communicatedContext = getTriggerData().getSituation();
        Label commContextLabel = new Label(
                "communicatedContext",
                communicatedContext == null
                        ? ""
                        : ChannelsUtils.lcFirst( communicatedContext.getSituationLabel() ) );
        commContextLabel.setVisible( communicatedContext != null );
        add( commContextLabel );
    }

    private void addIntent() {
        String intent = getInformationIntent();
        add( new Label(
                "intent",
                intent ) );
    }

    private void addInformation() {
        add( new Label(
                "information",
                "\"" + getInformationData().getName() + "\"" ) );
    }

    private void addEois() {
        int count = getEois().size();
        WebMarkupContainer eoisContainer = new WebMarkupContainer( "eoisContainer" );
        eoisContainer.setVisible( count > 0 );
        add( eoisContainer );
        eoisContainer.add( new Label( "eois", asCSVs( getEois() ) ));
    }


    private void addContacts() {
        List<ContactData> contacts = getContacts();
        final int lastIndex = contacts.size() - 1;
        WebMarkupContainer contactsContainer = new WebMarkupContainer( "contactsContainer" );
        contactsContainer.setVisible( !contacts.isEmpty() );
        add( contactsContainer );
        String anyOf = contacts.size() > 1 ? "by any of" : "by";
        contactsContainer.add( new Label( "anyOf", anyOf ) );
        ListView<ContactData> contactsListView = new ListView<ContactData>(
                "contacts",
                contacts
        ) {
            @Override
            protected void populateItem( ListItem<ContactData> item ) {
                item.add( new ContactLinkPanel( "contact", item.getModelObject(), getFinder() ) );
                item.add( new Label("sep", (item.getIndex() != lastIndex ) ? "," : "") );
/*
                if (item.getIndex() != lastIndex ) {
                    item.add( new AttributeModifier( "class", "notLast") );
                }
*/
            }
        };
        contactsContainer.add( contactsListView );
    }

    private List<ContactData> getContacts() {
        return getTriggerData().isOnRequestFromOther()
                ? getTriggerData().getOnRequest().getContacts()
                : getTriggerData().getOnNotification().getContacts();
    }

    private String getInformationIntent() {
        return getTriggerData().isOnRequestFromOther()
                ? getTriggerData().getOnRequest().getIntentText()
                : getTriggerData().getOnNotification().getIntentText();
    }

    private SharedInformationData getInformationData() {
        return getTriggerData().isOnRequestFromOther()
                ? getTriggerData().getOnRequest().getInformation()
                : getTriggerData().getOnNotification().getInformation();
    }

    private List<ElementOfInformationData> getEois() {
        return getInformationData().getEOIs();
    }
}
