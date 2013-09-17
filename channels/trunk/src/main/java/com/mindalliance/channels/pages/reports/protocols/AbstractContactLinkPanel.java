package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.procedures.ChannelData;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;

import java.util.List;

/**
 * Abstract contact link panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/1/12
 * Time: 1:05 PM
 */
public class AbstractContactLinkPanel extends AbstractDataPanel {

    private ContactData contactData;
    private boolean showBypassContacts;
    private List<ChannelData> workAddresses;
    private List<ChannelData> personalAddresses;


    public AbstractContactLinkPanel(
            String id,
            ContactData contactData,
            List<ChannelData> workAddresses,
            List<ChannelData> personalAddresses,
            ProtocolsFinder finder ) {
        super( id, finder );
        this.contactData = contactData;
        this.workAddresses = workAddresses;
        this.personalAddresses = personalAddresses;
        init();
    }

    public AbstractContactLinkPanel( String id, ContactData contactData, ProtocolsFinder finder ) {
        super( id, finder );
        this.contactData = contactData;
        init();
    }

    public AbstractContactLinkPanel( String id, ContactData contactData, ProtocolsFinder finder, boolean showBypassContacts ) {
        super( id, finder );
        this.contactData = contactData;
        this.showBypassContacts = showBypassContacts;
        init();
    }

    protected boolean isShowBypassContacts() {
        return showBypassContacts;
    }

    protected void init() {
        addContactLink();
        addWorkAddresses();
        addPersonalAddresses();
    }

    private void addContactLink() {
        WebMarkupContainer contactLink = makeAnchorLink( "contactLink", "#" + contactData.anchor() );
        contactLink.add( new Label( "contactName", contactData.getContactName() ) );
       // contactLink.add( new Label("contactJob", contactData.getContactJob() ) );
        add( contactLink );
    }

    private void addWorkAddresses() {
        WebMarkupContainer addressContainer = new WebMarkupContainer( "work" );
        addressContainer.setVisible( hasWorkAddresses() );
        add( addressContainer );
        addressContainer.add(
                hasWorkAddresses()
                        ? new FlatContactAddressesPanel( "addresses", workAddresses, getFinder() )
                        : new Label( "addresses", "" )
        );
    }

    private void addPersonalAddresses() {
        WebMarkupContainer addressContainer = new WebMarkupContainer( "personal" );
        addressContainer.setVisible( hasPersonalAddresses() );
        add( addressContainer );
        addressContainer.add(
                hasPersonalAddresses()
                        ? new FlatContactAddressesPanel( "addresses", personalAddresses, getFinder() )
                        : new Label( "addresses", "" )
        );
    }

    private boolean hasWorkAddresses() {
        return workAddresses != null && !workAddresses.isEmpty();
    }

    private boolean hasPersonalAddresses() {
        return personalAddresses != null && !personalAddresses.isEmpty();
    }

    public ContactData getContactData() {
        return contactData;
    }

    public List<ChannelData> getPersonalAddresses() {
        return personalAddresses;
    }

    public List<ChannelData> getWorkAddresses() {
        return workAddresses;
    }
}
