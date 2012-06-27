package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.procedures.ChannelData;
import com.mindalliance.channels.pages.reports.AnchoredLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;

import java.util.List;

/**
 * Contact link panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/24/12
 * Time: 10:48 AM
 */
public class ContactLinkPanel extends AbstractDataPanel {

    private ContactData contactData;
    private List<ChannelData> workAddresses;
    private List<ChannelData> personalAddresses;


    public ContactLinkPanel( String id, ContactData contactData ) {
        super( id );
        this.contactData = contactData;
        init();
    }

    public ContactLinkPanel(
            String id,
            ContactData contactData,
            List<ChannelData> workAddresses,
            List<ChannelData> personalAddresses ) {
        super( id );
        this.contactData = contactData;
        this.workAddresses = workAddresses;
        this.personalAddresses = personalAddresses;
    }

    private void init() {
        addContactLink();
        addWorkAddresses();
        addPersonalAddresses();
    }

     private void addContactLink() {
        AnchoredLink<ProtocolsPage> link = new AnchoredLink<ProtocolsPage>(
                "contactLink",
                ProtocolsPage.class,
                getProtocolsPage().getParameters(),
                contactData.getId() );
        add(  link );
        link.add( new Label( "contact", contactData.toLabel() ) );
    }

    private void addWorkAddresses() {
        WebMarkupContainer addressContainer = new WebMarkupContainer( "work" );
        addressContainer.setVisible( hasWorkAddresses() );
        add( addressContainer );
        addressContainer.add(
                hasWorkAddresses()
                    ? new ContactAddressesPanel( "addresses", workAddresses )
                    : new Label( "addresses", "" )
        );
    }

    private void addPersonalAddresses() {
        WebMarkupContainer addressContainer = new WebMarkupContainer( "personal" );
        addressContainer.setVisible( hasPersonalAddresses() );
        add( addressContainer );
        addressContainer.add(
                hasPersonalAddresses()
                        ? new ContactAddressesPanel( "addresses", personalAddresses )
                        : new Label( "addresses", "" )
        );
    }


    private boolean hasWorkAddresses() {
        return workAddresses != null && !workAddresses.isEmpty();
    }

    private boolean hasPersonalAddresses() {
        return personalAddresses != null && !personalAddresses.isEmpty();
    }


}
