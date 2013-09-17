package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.procedures.ChannelData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.List;

/**
 * Contact link panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/24/12
 * Time: 10:48 AM
 */
public class ContactLinkPanel extends AbstractContactLinkPanel {



    public ContactLinkPanel( String id, ContactData contactData, ProtocolsFinder finder ) {
        super( id, contactData, finder );
    }

    public ContactLinkPanel( String id, ContactData contactData, ProtocolsFinder finder, boolean showBypassContacts ) {
        super( id, contactData, finder, showBypassContacts );
    }


    public ContactLinkPanel(
            String id,
            ContactData contactData,
            List<ChannelData> workAddresses,
            List<ChannelData> personalAddresses,
            ProtocolsFinder finder ) {
        super( id, contactData, workAddresses, personalAddresses, finder );
    }

    protected void init() {
        super.init();
        addBypassContacts();
    }

    private void addBypassContacts() {
        WebMarkupContainer bypassContactsContainer = new WebMarkupContainer( "bypassContactsContainer" );
        List<ContactData> bypassContacts = getContactData().getBypassContacts();
        bypassContactsContainer.setVisible( !bypassContacts.isEmpty() );
        add( bypassContactsContainer );
        Label bypassLabel = new Label(
                "bypassLabel",
                bypassContacts.isEmpty()
                    ? ""
                    : getBypassLabel( bypassContacts) );
        bypassContactsContainer.add( bypassLabel );
        ListView<ContactData> bypassContactsListView = new ListView<ContactData>(
                "bypassContacts",
                bypassContacts
        ) {
            @Override
            protected void populateItem( ListItem<ContactData> item ) {
                ContactData bypassContactData = item.getModelObject();
                item.add( new NoBypassContactLinkPanel(
                        "bypassContact",
                        bypassContactData,
                        getWorkChannels( bypassContactData ),
                        getPersonalChannels( bypassContactData ),
                        getFinder() ) );
            }
        };
        bypassContactsContainer.add( bypassContactsListView );
        bypassContactsContainer.setVisible( isShowBypassContacts() );
        add(  bypassContactsContainer );
    }

    private String getBypassLabel( List<ContactData> bypassContacts ) {
        StringBuilder sb = new StringBuilder(  );
        sb.append( "If unreachable, " );
        sb.append( getContactData().forNotification()
                ? "I notify "
                : "I ask "
        );
        if ( bypassContacts.size() > 1) {
           sb.append( getContactData().bypassToAll()
                   ? "all of"
                   : "one of"
           );
        }
        return sb.toString();
    }


    @SuppressWarnings( "unchecked" )
    private List<ChannelData> getWorkChannels( ContactData contactData ) {
        final List<Long> mediumIds = getContactData().getBypassMediumIds();
        return (List<ChannelData>) CollectionUtils.select(
                contactData.getWorkChannels(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return mediumIds.contains( ( (ChannelData) object ).getMediumId() );
                    }
                }
        );
    }

    @SuppressWarnings( "unchecked" )
    private List<ChannelData> getPersonalChannels( ContactData contactData ) {
        final List<Long> mediumIds = getContactData().getBypassMediumIds();
        return (List<ChannelData>) CollectionUtils.select(
                contactData.getPersonalChannels(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return mediumIds.contains( ( (ChannelData) object ).getMediumId() );
                    }
                }
        );
    }

}
