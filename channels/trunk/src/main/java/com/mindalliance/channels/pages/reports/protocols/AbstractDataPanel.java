package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.ModelObjectData;
import com.mindalliance.channels.api.directory.ContactData;
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

    private ProtocolsFinder finder;

    protected AbstractDataPanel( String id, ProtocolsFinder finder ) {
        super( id );
        this.finder = finder;
    }

    protected String entityName( Class<? extends ModelObjectData> moDataClass, Long moId ) {
        ModelObjectData moData = findInScope( moDataClass, moId );
        return moData == null
                ? "???"
                : moData.getName();
    }

    protected <T extends ModelObjectData> T findInScope( Class<T>moDataClass, long moId ) {
        return finder.findInScope( moDataClass, moId );
    }

    @SuppressWarnings( "unchecked" )
    protected List<ContactData> findContacts( long actorId ) {
        return finder.findContacts( actorId );
    }

    protected WebMarkupContainer makeAnchor( String id, String anchor ) {
        WebMarkupContainer anchorContainer = new WebMarkupContainer( id );
        anchorContainer.add( new AttributeModifier( "name", anchor ) );
        return anchorContainer;
    }

    protected WebMarkupContainer makeAttributeContainer( String id, String value ) {
        WebMarkupContainer attributeContainer = new WebMarkupContainer( id );
        attributeContainer.setVisible( value != null && !value.trim().isEmpty() );
        attributeContainer.add( new Label(  "value", value == null ? "" : value.trim() ) );
        return attributeContainer;
    }

    public ProtocolsFinder getFinder() {
        return finder;
    }
}
