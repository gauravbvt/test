package com.mindalliance.channels.pages.components.menus;

import com.mindalliance.channels.Identifiable;
import com.mindalliance.channels.pages.components.entities.EntityPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.link.Link;

import java.util.List;
import java.util.ArrayList;

/**
 * EntityPanel show menu.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 24, 2009
 * Time: 12:52:15 PM
 */
public class EntityShowMenuPanel extends MenuPanel {

    private EntityPanel entityPanel;

    public EntityShowMenuPanel( String id, IModel<? extends Identifiable> model ) {
        super( id, model, null );
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

    public List<Component> getMenuItems() {
        List<Component> menuItems = new ArrayList<Component>();
        Link detailsLink = new AjaxFallbackLink("link"){
            public void onClick( AjaxRequestTarget target ) {
                changeAspectTo(target, "details");
            }
        };
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "Details" ),
                detailsLink ) );
        Link networkLink = new AjaxFallbackLink("link"){
             public void onClick( AjaxRequestTarget target ) {
                 changeAspectTo(target, "network");
             }
         };
         menuItems.add( new LinkMenuItem(
                 "menuItem",
                 new Model<String>( "Network" ),
                 networkLink ) );
        Link issuesLink = new AjaxFallbackLink("link"){
             public void onClick( AjaxRequestTarget target ) {
                 changeAspectTo(target, "issues");
             }
         };
         menuItems.add( new LinkMenuItem(
                 "menuItem",
                 new Model<String>( "Issues" ),
                 issuesLink ) );
        Link mapLink = new AjaxFallbackLink("link"){
             public void onClick( AjaxRequestTarget target ) {
                 changeAspectTo(target, "map");
             }
         };
         menuItems.add( new LinkMenuItem(
                 "menuItem",
                 new Model<String>( "Map" ),
                 mapLink ) );
        return menuItems;
    }

    private void changeAspectTo(AjaxRequestTarget target, String aspect) {
        entityPanel.setAspectShown( target, aspect );
    }

    public void setEntityPanel( EntityPanel entityPanel ) {
        this.entityPanel = entityPanel;
    }
}
