package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.procedures.AbstractFlowData;
import com.mindalliance.channels.api.procedures.ChannelData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.List;

/**
 * Commitment data panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/26/12
 * Time: 4:42 PM
 */
public class CommitmentDataPanel extends AbstractDataPanel {

    private final AbstractFlowData flowData;
    private final boolean received;

    public CommitmentDataPanel( String id, AbstractFlowData flowData, boolean received ) {
        super(id );
        this.flowData = flowData;
        this.received = received;
        init();
    }

    private void init() {
        addHeader();
        addContacts();
        addMaxDelay();
        addOnFailure();
        addEois();
        addInstructions();
        addBypassContacts();
        addFailureImpact();
    }

    private void addHeader() {
        add(  new Label(
                "mode",
                flowData.isNotification()
                    ? "Notify of"
                    : "When asked, provide") );
        add( new Label( "information", flowData.getInformation().getName() ) );
    }

    private void addContacts() {
        ListView<ContactData> contactsListView = new ListView<ContactData>(
                "contacts",
                flowData.getContacts()
        ) {
            @Override
            protected void populateItem( ListItem<ContactData> item ) {
                ContactData contactData = item.getModelObject();
                item.add(  new ContactLinkPanel(
                        "contact",
                        contactData,
                        getWorkChannels( contactData ),
                        getPersonalChannels( contactData ) ) );
            }
        };
        add( contactsListView );
    }



    private void addMaxDelay() {
        // todo
    }

    private void addOnFailure() {
        // todo
    }


    private void addEois() {
        // todo
    }

    private void addInstructions() {
        // todo
    }

    private void addBypassContacts() {
        // todo
    }

    private void addFailureImpact() {
        // todo
    }

    @SuppressWarnings( "unchecked" )
    private List<ChannelData> getWorkChannels( ContactData contactData ) {
        final List<Long> mediumIds = flowData.getMediumIds();
        return (List<ChannelData>) CollectionUtils.select(
                contactData.getWorkChannels(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return mediumIds.contains( ((ChannelData)object).getMediumId() );
                    }
                }
        );
    }

    @SuppressWarnings( "unchecked" )
    private List<ChannelData> getPersonalChannels( ContactData contactData ) {
        final List<Long> mediumIds = flowData.getMediumIds();
        return (List<ChannelData>) CollectionUtils.select(
                contactData.getPersonalChannels(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return mediumIds.contains( ((ChannelData)object).getMediumId() );
                    }
                }
        );
    }

}
