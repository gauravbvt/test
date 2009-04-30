package com.mindalliance.channels.pages.components.menus;

import com.mindalliance.channels.model.Part;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Pages menu for a part.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 11, 2009
 * Time: 1:36:54 PM
 */
public class PartShowMenuPanel extends MenuPanel {

    public PartShowMenuPanel( String s, IModel<? extends Part> model ) {
        super( s, model, null );
        init();
    }

    private void init() {
        ListView<Component> menuItems = new ListView<Component>(
                "items",
                new PropertyModel<List<Component>>( this, "menuItems" ) ) {
            protected void populateItem( ListItem<Component> item ) {
                item.add( item.getModelObject() );
            }
        };
        add( menuItems );
    }

    /**
     * Get population of menu items.
     *
     * @return a list of menu items
     */
    public List<Component> getMenuItems() {
        List<Component> menuItems = new ArrayList<Component>();
        menuItems.addAll( getModelObjectMenuItems( "menuItem", getModelObjectWrappers() ) );
        return menuItems;
    }

    private List<ModelObjectWrapper> getModelObjectWrappers() {
        List<ModelObjectWrapper> modelObjects = new ArrayList<ModelObjectWrapper>();
        Part part = getPart();
        if ( part.getActor() != null )
            modelObjects.add( new ModelObjectWrapper( "Actor", part.getActor() ) );
        if ( part.getRole() != null )
            modelObjects.add( new ModelObjectWrapper( "Role", part.getRole() ) );
        if ( part.getOrganization() != null )
            modelObjects.add( new ModelObjectWrapper(
                    "Organization",
                    part.getOrganization() ) );
        if ( part.getJurisdiction() != null )
            modelObjects.add( new ModelObjectWrapper(
                    "Jurisdiction",
                    part.getJurisdiction() ) );
        if ( part.getLocation() != null )
            modelObjects.add( new ModelObjectWrapper(
                    "Location",
                    part.getLocation() ) );
        return modelObjects;
    }

    private Part getPart() {
        return (Part) getModel().getObject();
    }

}
