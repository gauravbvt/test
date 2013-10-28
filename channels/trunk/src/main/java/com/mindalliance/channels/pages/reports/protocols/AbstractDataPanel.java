package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.ModelObjectData;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

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
        setOutputMarkupId( true );
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

    protected WebMarkupContainer makeAnchor( String id, String anchor ) {
        WebMarkupContainer anchorContainer = new WebMarkupContainer( id );
        anchorContainer.add( new AttributeModifier( "name", anchor ) );
        return anchorContainer;
    }

    protected WebMarkupContainer makeAnchorLink( String id, String anchor ) {
        WebMarkupContainer anchorContainer = new WebMarkupContainer( id );
        anchorContainer.add( new AttributeModifier( "href", anchor ) );
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

    protected Component addTipTitle( Component component, String title ) {
        return addTipTitle( component, new Model<String>( title ) );
    }

    protected Component addTipTitle( Component component, String title, boolean keepAlive ) {
        return addTipTitle( component, new Model<String>( title ), keepAlive );
    }

    protected Component addTipTitle( Component component, IModel<String> titleModel ) {
        return addTipTitle( component, titleModel, false );
    }

    protected Component addTipTitle( Component component, IModel<String> titleModel, boolean keepAlive ) {
        component.add( new AttributeModifier( "title", titleModel ) );
        return component;
    }

    /**
     * Set and update a component's visibility.
     *
     * @param target    an ajax request target
     * @param component a component
     * @param visible   a boolean
     */
    protected static void makeVisible( AjaxRequestTarget target, Component component, boolean visible ) {
        makeVisible( component, visible );
        target.add( component );
    }

    /**
     * Set a component's visibility.
     *
     * @param component a component
     * @param visible   a boolean
     */
    protected static void makeVisible( Component component, boolean visible ) {
        component.add( new AttributeModifier( "style", new Model<String>( visible ? "" : "display:none" ) ) );
    }


}
