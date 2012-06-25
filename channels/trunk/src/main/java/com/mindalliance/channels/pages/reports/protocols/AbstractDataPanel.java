package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.ModelObjectData;
import com.mindalliance.channels.api.directory.ContactData;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.List;

/**
 * Abstract data panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/23/12
 * Time: 1:35 PM
 */
public abstract class AbstractDataPanel extends Panel {

    protected AbstractDataPanel( String id ) {
        super( id );
    }

    protected String entityName( Class<? extends ModelObjectData> moDataClass, Long moId ) {
        ModelObjectData moData = getProtocolsPage().findInScope( moDataClass, moId );
        return moData == null
                ? "???"
                : moData.getName();
    }

    protected List<ContactData> findContacts( long actorId ) {
        return getProtocolsPage().findContacts( actorId );
    }

    ProtocolsPage getProtocolsPage() {
        return (ProtocolsPage) getPage();
    }

}
