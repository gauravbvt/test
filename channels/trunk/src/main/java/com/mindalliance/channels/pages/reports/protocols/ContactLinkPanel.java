package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.pages.reports.AnchoredLink;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

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

    public ContactLinkPanel( String id, ContactData contactData ) {
        super( id );
        this.contactData = contactData;
        init();
    }

    private void init() {
        addContactLink();
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
}
