package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.ModelObjectData;
import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.core.model.Employment;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
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

    protected List<ContactData> findContacts( Employment employment ) {
        return getProtocolsPage().findContacts( employment );
    }


    protected WebMarkupContainer makeAnchor( String id, String anchor ) {
        WebMarkupContainer anchorContainer = new WebMarkupContainer( id );
        anchorContainer.add( new AttributeModifier( "name", anchor ) );
        return anchorContainer;
    }

    protected WebMarkupContainer makeAttributeContainer( String id, String value ) {
        WebMarkupContainer attributeContainer = new WebMarkupContainer( id );
        attributeContainer.setVisible( value != null && !value.isEmpty() );
        attributeContainer.add( new Label(  "value", value == null ? "" : value ) );
        return attributeContainer;
    }

    ProtocolsPage getProtocolsPage() {
        return (ProtocolsPage) getPage();
    }

}
